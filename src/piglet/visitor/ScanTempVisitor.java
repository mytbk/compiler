package piglet.visitor;

import piglet.syntaxtree.BinOp;
import piglet.syntaxtree.CJumpStmt;
import piglet.syntaxtree.Call;
import piglet.syntaxtree.ErrorStmt;
import piglet.syntaxtree.Exp;
import piglet.syntaxtree.Goal;
import piglet.syntaxtree.HAllocate;
import piglet.syntaxtree.HLoadStmt;
import piglet.syntaxtree.HStoreStmt;
import piglet.syntaxtree.IntegerLiteral;
import piglet.syntaxtree.JumpStmt;
import piglet.syntaxtree.Label;
import piglet.syntaxtree.MoveStmt;
import piglet.syntaxtree.NoOpStmt;
import piglet.syntaxtree.Operator;
import piglet.syntaxtree.PrintStmt;
import piglet.syntaxtree.Procedure;
import piglet.syntaxtree.Stmt;
import piglet.syntaxtree.StmtExp;
import piglet.syntaxtree.StmtList;
import piglet.syntaxtree.Temp;

public class ScanTempVisitor extends DepthFirstVisitor {
	public int maxtmp = 0;
	
	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public void visit(Goal n) {
		n.f1.accept(this);
		n.f3.accept(this);
	}

	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	 */
	public void visit(StmtList n) {
		n.f0.accept(this);
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public void visit(Procedure n) {
		n.f4.accept(this);
	}

	/**
	 * f0 -> NoOpStmt()
	 *       | ErrorStmt()
	 *       | CJumpStmt()
	 *       | JumpStmt()
	 *       | HStoreStmt()
	 *       | HLoadStmt()
	 *       | MoveStmt()
	 *       | PrintStmt()
	 */
	public void visit(Stmt n) {
		n.f0.accept(this);
	}

	/**
	 * f0 -> "NOOP"
	 */
	public void visit(NoOpStmt n) {
		return;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public void visit(ErrorStmt n) {
		return;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Exp()
	 * f2 -> Label()
	 */
	public void visit(CJumpStmt n) {
		n.f1.accept(this);
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public void visit(JumpStmt n) {
		return;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Exp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Exp()
	 */
	public void visit(HStoreStmt n) {
		n.f1.accept(this);
		n.f3.accept(this);
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 * f3 -> IntegerLiteral()
	 */
	public void visit(HLoadStmt n) {
		n.f1.accept(this);
		n.f2.accept(this);
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public void visit(MoveStmt n) {
		n.f1.accept(this);
		n.f2.accept(this);
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> Exp()
	 */
	public void visit(PrintStmt n) {
		n.f1.accept(this);
	}

	/**
	 * f0 -> StmtExp()
	 *       | Call()
	 *       | HAllocate()
	 *       | BinOp()
	 *       | Temp()
	 *       | IntegerLiteral()
	 *       | Label()
	 */
	public void visit(Exp n) {
		n.f0.accept(this);
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> Exp()
	 * f4 -> "END"
	 */
	public void visit(StmtExp n) {
		n.f1.accept(this);
		n.f3.accept(this);
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> Exp()
	 * f2 -> "("
	 * f3 -> ( Exp() )*
	 * f4 -> ")"
	 */
	public void visit(Call n) {
		n.f1.accept(this);
		n.f3.accept(this);
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> Exp()
	 */
	public void visit(HAllocate n) {
		n.f1.accept(this);
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Exp()
	 * f2 -> Exp()
	 */
	public void visit(BinOp n) {
		n.f1.accept(this);
		n.f2.accept(this);
	}

	/**
	 * f0 -> "LT"
	 *       | "PLUS"
	 *       | "MINUS"
	 *       | "TIMES"
	 */
	public void visit(Operator n) {
		return;
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public void visit(Temp n) {
		int x = Integer.parseInt(n.f1.f0.tokenImage);
		//System.out.println(x);
		if (x>maxtmp) {
			maxtmp = x;
		}
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public void visit(IntegerLiteral n) {
		return;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public void visit(Label n) {
		return;
	}

}
