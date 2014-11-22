package spiglet.visitor;
import spiglet.spiglet2kanga.SpgExpr;
import spiglet.spiglet2kanga.SpgGoal;
import spiglet.spiglet2kanga.SpgProc;
import spiglet.spiglet2kanga.SpgSimpExpr;
import spiglet.spiglet2kanga.SpgStmt;
import spiglet.spiglet2kanga.SpgSym;
import spiglet.spiglet2kanga.SpgTemp;
import spiglet.syntaxtree.BinOp;
import spiglet.syntaxtree.CJumpStmt;
import spiglet.syntaxtree.Call;
import spiglet.syntaxtree.ErrorStmt;
import spiglet.syntaxtree.Exp;
import spiglet.syntaxtree.Goal;
import spiglet.syntaxtree.HAllocate;
import spiglet.syntaxtree.HLoadStmt;
import spiglet.syntaxtree.HStoreStmt;
import spiglet.syntaxtree.IntegerLiteral;
import spiglet.syntaxtree.JumpStmt;
import spiglet.syntaxtree.Label;
import spiglet.syntaxtree.MoveStmt;
import spiglet.syntaxtree.NoOpStmt;
import spiglet.syntaxtree.Operator;
import spiglet.syntaxtree.PrintStmt;
import spiglet.syntaxtree.Procedure;
import spiglet.syntaxtree.SimpleExp;
import spiglet.syntaxtree.Stmt;
import spiglet.syntaxtree.StmtExp;
import spiglet.syntaxtree.StmtList;
import spiglet.syntaxtree.Temp;

/**
* Provides default methods which visit each node in the tree in depth-first
* order.  Your visitors may extend this class.
*/
public class GenKangaVisitor extends GJDepthFirst<SpgSym, SpgSym> {

	//
	// User-generated visitor methods below
	//
	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public SpgSym visit(Goal n, SpgSym argu) {
		// argu is a SpgGoal instance
		SpgGoal g = (SpgGoal)argu;
		SpgProc mainproc = new SpgProc("MAIN", 0);
		g.addProc(mainproc);
		n.f1.accept(this, mainproc);
		n.f3.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	 */
	public SpgSym visit(StmtList n, SpgSym argu) {
		n.f0.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public SpgSym visit(Procedure n, SpgSym argu) {
		// argu is a SpgGoal instance
		SpgGoal g = (SpgGoal)argu;
		String name = n.f0.f0.tokenImage;
		int argc = Integer.parseInt(n.f2.f0.tokenImage);
		SpgProc p = new SpgProc(name, argc);
		g.addProc(p);
		n.f4.accept(this, p);
		return null;
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
	public SpgSym visit(Stmt n, SpgSym argu) {
		n.f0.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public SpgSym visit(NoOpStmt n, SpgSym argu) {
		SpgProc p = (SpgProc)argu;
		p.addStmt(new SpgStmt(SpgStmt.StmtType.NOOP));
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public SpgSym visit(ErrorStmt n, SpgSym argu) {
		SpgProc p = (SpgProc)argu;
		p.addStmt(new SpgStmt(SpgStmt.StmtType.ERROR));
		return null;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Temp()
	 * f2 -> Label()
	 */
	public SpgSym visit(CJumpStmt n, SpgSym argu) {
		SpgProc p = (SpgProc)argu;
		SpgStmt s = new SpgStmt(SpgStmt.StmtType.CJUMP);
		s.tmp1 = (SpgTemp)n.f1.accept(this, null);
		s.jmptarget = n.f2.f0.tokenImage;
		p.addStmt(s);
		return null;
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public SpgSym visit(JumpStmt n, SpgSym argu) {
		SpgProc p = (SpgProc)argu;
		SpgStmt s = new SpgStmt(SpgStmt.StmtType.JUMP);
		s.jmptarget = n.f1.f0.tokenImage;
		p.addStmt(s);
		return null;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Temp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Temp()
	 */
	public SpgSym visit(HStoreStmt n, SpgSym argu) {
		SpgProc p = (SpgProc)argu;
		SpgStmt s = new SpgStmt(SpgStmt.StmtType.STORE);
		s.tmp1 = (SpgTemp)n.f1.accept(this, null);
		s.tmp2 = (SpgTemp)n.f3.accept(this, null);
		s.imm = Integer.parseInt(n.f2.f0.tokenImage);
		p.addStmt(s);
		return null;
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Temp()
	 * f3 -> IntegerLiteral()
	 */
	public SpgSym visit(HLoadStmt n, SpgSym argu) {
		SpgProc p = (SpgProc)argu;
		SpgStmt s = new SpgStmt(SpgStmt.StmtType.LOAD);
		s.tmp1 = (SpgTemp)n.f1.accept(this, null);
		s.tmp2 = (SpgTemp)n.f2.accept(this, null);
		s.imm = Integer.parseInt(n.f3.f0.tokenImage);
		p.addStmt(s);
		return null;
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public SpgSym visit(MoveStmt n, SpgSym argu) {
		SpgProc p = (SpgProc)argu;
		SpgStmt s = new SpgStmt(SpgStmt.StmtType.MOVE);
		s.tmp1 = (SpgTemp)n.f1.accept(this, null);
		s.exp = (SpgExpr)n.f2.accept(this, null);
		p.addStmt(s);
		return null;
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> SimpleExp()
	 */
	public SpgSym visit(PrintStmt n, SpgSym argu) {
		SpgProc p = (SpgProc)argu;
		SpgStmt s = new SpgStmt(SpgStmt.StmtType.PRINT);
		s.exp = (SpgExpr)n.f1.accept(this, null);
		p.addStmt(s);
		return null;
	}

	/**
	 * f0 -> Call()
	 *       | HAllocate()
	 *       | BinOp()
	 *       | SimpleExp()
	 */
	public SpgSym visit(Exp n, SpgSym argu) {
		return n.f0.accept(this, null);
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> SimpleExp()
	 * f4 -> "END"
	 */
	public SpgSym visit(StmtExp n, SpgSym argu) {
		// argu should be SpgProc
		SpgProc p = (SpgProc)argu;
		n.f1.accept(this, p);
		p.retexp = (SpgSimpExpr)n.f3.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 * f2 -> "("
	 * f3 -> ( Temp() )*
	 * f4 -> ")"
	 */
	public SpgSym visit(Call n, SpgSym argu) {
		SpgExpr e = new SpgExpr(SpgExpr.ExpType.CALL);
		e.se = (SpgSimpExpr)n.f1.accept(this, null);
		n.f3.accept(this, e);
		return e;
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> SimpleExp()
	 */
	public SpgSym visit(HAllocate n, SpgSym argu) {
		SpgExpr e = new SpgExpr(SpgExpr.ExpType.ALLOC);
		e.se = (SpgSimpExpr)n.f1.accept(this, null);
		return e;
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Temp()
	 * f2 -> SimpleExp()
	 */
	public SpgSym visit(BinOp n, SpgSym argu) {
		SpgExpr e = new SpgExpr(SpgExpr.ExpType.BinOp);
		n.f0.accept(this, e);
		e.oprand = (SpgTemp)n.f1.accept(this, null);
		e.se = (SpgSimpExpr)n.f2.accept(this, null);
		return e;
	}

	/**
	 * f0 -> "LT"
	 *       | "PLUS"
	 *       | "MINUS"
	 *       | "TIMES"
	 */
	public SpgSym visit(Operator n, SpgSym argu) {
		// argu should be a BinOp expression
		final String[] ops = { "LT", "PLUS", "MINUS", "TIMES" };
		SpgExpr e = (SpgExpr)argu;
		e.op = ops[n.f0.which];
		return null;
	}

	/**
	 * f0 -> Temp()
	 *       | IntegerLiteral()
	 *       | Label()
	 */
	public SpgSym visit(SimpleExp n, SpgSym argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public SpgSym visit(Temp n, SpgSym argu) {
		int i = Integer.parseInt(n.f1.f0.tokenImage);
		SpgTemp t = new SpgTemp(i);
		if (argu!=null) {
			// should be a call expression
			SpgExpr e = (SpgExpr)argu;
			e.addCallParam(t);
		}
		return t;
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public SpgSym visit(IntegerLiteral n, SpgSym argu) {
		SpgSimpExpr e = new SpgSimpExpr(SpgSimpExpr.SExprType.INT);
		e.num = Integer.parseInt(n.f0.tokenImage);
		return e;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public SpgSym visit(Label n, SpgSym argu) {
		SpgSimpExpr e = new SpgSimpExpr(SpgSimpExpr.SExprType.LB);
		e.s = n.f0.tokenImage;
		if (argu!=null) {
			// should be a procedure
			SpgProc p = (SpgProc)argu;
			p.statements.lastElement().lb = e.s;
		}
		return e;
	}

}
