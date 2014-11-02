package piglet.visitor;
import java.util.Enumeration;
import java.util.Vector;

import piglet.piglet2spiglet.GenSpigletCtl;
import piglet.piglet2spiglet.PigletExpr;
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
import piglet.syntaxtree.Node;
import piglet.syntaxtree.NodeList;
import piglet.syntaxtree.NodeListOptional;
import piglet.syntaxtree.NodeOptional;
import piglet.syntaxtree.NodeSequence;
import piglet.syntaxtree.NodeToken;
import piglet.syntaxtree.Operator;
import piglet.syntaxtree.PrintStmt;
import piglet.syntaxtree.Procedure;
import piglet.syntaxtree.Stmt;
import piglet.syntaxtree.StmtExp;
import piglet.syntaxtree.StmtList;
import piglet.syntaxtree.Temp;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class GenSpigletVisitor extends GJDepthFirst<PigletExpr, GenSpigletCtl> {
	
	public int nTemp;
	final GenSpigletCtl Default = new GenSpigletCtl(GenSpigletCtl.Control.DEFAULT);
	final GenSpigletCtl Print = new GenSpigletCtl(GenSpigletCtl.Control.PRINT);
	final String BinOPs[] = {"LT", "PLUS", "MINUS", "TIMES" };

	String expr_to_tmp(PigletExpr e) {
		if (e.type==PigletExpr.Expr_t.Temp) {
			return e.toString();
		} else {
			nTemp++;
			System.out.println("MOVE TEMP " + nTemp + " " + e.toString());
			return "TEMP " + nTemp;
		}
	}
	
	String expr_to_simple(PigletExpr e) {
		if (e.isSimple()) {
			return e.toString();
		} else {
			nTemp++;
			System.out.println("MOVE TEMP " + nTemp + " " + e.toString());
			return "TEMP " + nTemp;
		}
	}
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
   public PigletExpr visit(Goal n, GenSpigletCtl argu) {
      System.out.println("MAIN");
      n.f1.accept(this, argu);
      System.out.println("END");
      n.f3.accept(this, argu);
      return null;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public PigletExpr visit(StmtList n, GenSpigletCtl argu) {
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
   public PigletExpr visit(Procedure n, GenSpigletCtl argu) {
	   String lb = n.f0.f0.toString();
	   String numarg = n.f2.f0.toString();
      System.out.println(lb + " [ " + numarg + " ]");
      n.f4.accept(this, Print);
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
   public PigletExpr visit(Stmt n, GenSpigletCtl argu) {
      n.f0.accept(this, argu);
      return null;
   }

   /**
    * f0 -> "NOOP"
    */
   public PigletExpr visit(NoOpStmt n, GenSpigletCtl argu) {
	   System.out.println("NOOP");
      return null;
   }

   /**
    * f0 -> "ERROR"
    */
   public PigletExpr visit(ErrorStmt n, GenSpigletCtl argu) {
	   System.out.println("ERROR");
	   return null;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Exp()
    * f2 -> Label()
    */
   public PigletExpr visit(CJumpStmt n, GenSpigletCtl argu) {
	   PigletExpr e = n.f1.accept(this, Default);
	   String lb = n.f2.f0.toString();
	   String tmpstr;
	   if (e.type==PigletExpr.Expr_t.Temp) {
		   tmpstr = e.toString();
	   } else {
		   nTemp++;
		   System.out.println("MOVE TEMP " + nTemp + " " + e.toString());
		   tmpstr = "TEMP " + nTemp;
	   }
	   System.out.println("CJUMP " + tmpstr + " " + lb);
      return null;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public PigletExpr visit(JumpStmt n, GenSpigletCtl argu) {
	   String lb = n.f1.f0.toString();
	   System.out.println("JUMP " + lb);
      return null;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Exp()
    * f2 -> IntegerLiteral()
    * f3 -> Exp()
    */
   public PigletExpr visit(HStoreStmt n, GenSpigletCtl argu) {
	   PigletExpr e1 = n.f1.accept(this, Default);
	   PigletExpr e2 = n.f3.accept(this, Default);
	   String t1, t2;
	   String offs = n.f2.f0.toString();
	   t1 = expr_to_tmp(e1);
	   t2 = expr_to_tmp(e2);
	   System.out.println("HSTORE " + t1 + " " + offs + " " + t2);
      return null;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Exp()
    * f3 -> IntegerLiteral()
    */
   public PigletExpr visit(HLoadStmt n, GenSpigletCtl argu) {
	   PigletExpr e = n.f2.accept(this, Default);
	   String s;
	   String t = n.f1.accept(this, Default).toString();
	   String i = n.f3.f0.toString();
	   s = expr_to_tmp(e);
	   System.out.println("HLOAD " + t + " " + s + " " + i);
	   return null;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public PigletExpr visit(MoveStmt n, GenSpigletCtl argu) {
	   PigletExpr t = n.f1.accept(this, Default);
	   PigletExpr e = n.f2.accept(this, Default);
	   System.out.println("MOVE " + t.toString() + " " + e.toString());
	   return null;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> Exp()
    */
   public PigletExpr visit(PrintStmt n, GenSpigletCtl argu) {
	   PigletExpr e = n.f1.accept(this, Default);
	   String s = expr_to_simple(e);
	   System.out.println("PRINT " + s);
	   return null;
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
   public PigletExpr visit(Exp n, GenSpigletCtl argu) {
      PigletExpr e_result = n.f0.accept(this, argu);
      if (argu.isList()) {
    	  argu.call_list.add(e_result);
    	  return null;
      } else {
    	  return e_result;
      }
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> Exp()
    * f4 -> "END"
    */
   public PigletExpr visit(StmtExp n, GenSpigletCtl argu) {
	   /* a normal expression or a function body */
	   if (argu.isPrint()) {
		   // a function body
		   System.out.println("BEGIN");
		   /* statement list */
		   n.f1.accept(this, null);
		   /* return */
		   PigletExpr e = n.f3.accept(this, Default);
		   String ret = expr_to_simple(e);
		   System.out.println("RETURN " + ret);
		   System.out.println("END");
		   return null;
	   } else {
		   // a normal expression, going to turn to a simpler expression
		   n.f1.accept(this, null);
		   return n.f3.accept(this, Default);
	   }
   }

   /**
    * f0 -> "CALL"
    * f1 -> Exp()
    * f2 -> "("
    * f3 -> ( Exp() )*
    * f4 -> ")"
    */
   public PigletExpr visit(Call n, GenSpigletCtl argu) {
	   String result = "";
	   /* process the method */
	   PigletExpr m = n.f1.accept(this, Default);
	   String tm = expr_to_tmp(m);
	   /* process the arguments */
	   GenSpigletCtl ctl = new GenSpigletCtl(GenSpigletCtl.Control.LIST);
	   n.f3.accept(this, ctl);
	   Vector<String> args = new Vector<String>();
	   for (int i=0; i<ctl.call_list.size(); i++) {
		   String s = expr_to_tmp(ctl.call_list.elementAt(i));
		   args.add(s);
	   }
	   result += "CALL " + tm + "( ";
	   for (int i=0; i<args.size(); i++) {
		   result += args.elementAt(i) + " ";
	   }
	   result += ")";
	   return new PigletExpr(PigletExpr.Expr_t.Other, result);
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> Exp()
    */
   public PigletExpr visit(HAllocate n, GenSpigletCtl argu) {
	   PigletExpr e = n.f1.accept(this, Default);
	   String s;
	   if (!e.isSimple()) {
		   nTemp++;
		   System.out.println("MOVE TEMP " + nTemp + " " + e.toString());
		   s = "TEMP " + nTemp;
	   } else {
		   s = e.toString();
	   }
      return new PigletExpr(PigletExpr.Expr_t.Other,
    		  "HALLOCATE " + s);
   }

   /**
    * f0 -> Operator()
    * f1 -> Exp()
    * f2 -> Exp()
    */
   public PigletExpr visit(BinOp n, GenSpigletCtl argu) {
	   String op = BinOPs[n.f0.f0.which];
	   String s1, s2;
	   PigletExpr t1 = n.f1.accept(this, Default);
	   PigletExpr t2 = n.f2.accept(this, Default);
	   /* the first expr can only be temp */
	   if (!(t1.type==PigletExpr.Expr_t.Temp)) {
		   nTemp++;
		   System.out.println("MOVE TEMP " + nTemp + " " + t1.toString());
		   s1 = "TEMP " + nTemp;
	   } else {
		   s1 = t1.toString();
	   }
	   if (!t2.isSimple()) {
		   nTemp++;
		   System.out.println("MOVE TEMP " + nTemp + " " + t2.toString());
		   s2 = "TEMP " + nTemp;
	   } else {
		   s2 = t2.toString();
	   }
      return new PigletExpr(PigletExpr.Expr_t.Other,
    		  op + " " + s1 + " " + s2);
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public PigletExpr visit(Operator n, GenSpigletCtl argu) {
	   return null;
   }

   /* the following are simple expressions
    * these visitors return the expression string
    */
   
   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public PigletExpr visit(Temp n, GenSpigletCtl argu) {
	   return new PigletExpr(PigletExpr.Expr_t.Temp,
			   "TEMP " + n.f1.f0.toString());
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public PigletExpr visit(IntegerLiteral n, GenSpigletCtl argu) {
	   return new PigletExpr(PigletExpr.Expr_t.Int, n.f0.toString());
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public PigletExpr visit(Label n, GenSpigletCtl argu) {
	   return new PigletExpr(PigletExpr.Expr_t.Label, n.f0.toString());
   }

}
