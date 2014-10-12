package minijava.symboltable;

import minijava.syntaxtree.Identifier;
import minijava.typecheck.PrintError;

public class MExpression extends MType {
	public enum Operator {
		And, Smaller, Plus, Minus, Times, ArrayLookup,
		ArrayLen, MsgSend, Primary;
	}
	Operator e_op;
	public MExpression first, second;
	// for msgsend, we need an identifier and a expression list
	public MIdentifier e_id;
	public MExpressionList e_list;
	// Expression type
	public MPrimaryExpr e_exp;
	// Expression value (for primary expression)
	public String e_val;
	
	public MExpression(int line, int column, Operator op) {
		super(line, column);
		e_op = op;
		e_exp = new MPrimaryExpr();
		e_list = new MExpressionList();
	}
	
	public String exprType(MMethod m) {
		switch (e_op) {
		case And:
			if (first.exprType(m)==MIdentifier.boolType
			&& second.exprType(m)==MIdentifier.boolType) {
				return MIdentifier.boolType;
			} else {
				PrintError.print(line, column, "Type mismatch");
				return null;
			}
		case ArrayLen:
			if (first.exprType(m)==MIdentifier.arrType) {
				return MIdentifier.intType;
			} else {
				PrintError.print(line, column, "Type mismatch");
				return null;
			}
		case ArrayLookup:
			if (first.exprType(m)==MIdentifier.arrType
			&& second.exprType(m)==MIdentifier.intType) {
				return MIdentifier.intType;
			} else {
				PrintError.print(line, column, "Type mismatch");
				return null;
			}
		case Smaller:
			if (first.exprType(m)==MIdentifier.intType
			&& second.exprType(m)==MIdentifier.intType) {
				return MIdentifier.boolType;
			} else {
				PrintError.print(line, column, "Type mismatch");
				return null;
			}
		case Plus:
		case Minus:
		case Times:
			if (first.exprType(m)==MIdentifier.intType
			&& second.exprType(m)==MIdentifier.intType) {
				return MIdentifier.intType;
			} else {
				PrintError.print(line, column, "Type mismatch");
				return null;
			}
		case MsgSend:
			// expr.id(expr*)
			String c_type = first.exprType(m);
			MClass m_class = m.method_class.all_classes.findClassByName(c_type);
			if (m_class==null) {
				PrintError.print(line, column, "no class found in a message send expression");
				return null;
			}
			int idx=-1;
			while (m_class!=null) {
				idx = m_class.methods.findMethod(e_id.name);
				if (idx==-1) {
					m_class = m_class.extend_class; // 本类无该方法，从父类继续找
				} else {
					break;
				}
			}
			if (idx==-1) {
				PrintError.print(line, column, "method " + e_id.name + " not found");
				return null;
			}
			MMethod m_method = m_class.methods.methods.elementAt(idx);
			if (e_list.size()!=m_method.paramList.size()) {
				PrintError.print(line, column, "Method param size mismatch!");
				return null;
			}
			for (int i=0; i<e_list.size(); i++) {
				String t1 = e_list.e_list.elementAt(i).exprType(m);
				String t2 = m_method.paramList.varlist.elementAt(i).typename;
				if (!t1.equals(t2)) {
					PrintError.print(line, column, "Method param type mismatch!");
					return null;
				}
			}
			return m_method.ret_type_name;
		case Primary:
			return e_exp.primExprType(m);
		default:
			return null;
		}
	}
	
	public void printExpr(int spaces) {
		System.err.print(OutputFormat.spaces(spaces));
		
		switch (e_op) {
		case Primary:
			e_exp.printPrimExpr(0);
			break;
		case And:
			first.printExpr(0);
			System.err.print("&&");
			second.printExpr(0);
			break;
		case Smaller:
			first.printExpr(0);
			System.err.print("<");
			second.printExpr(0);
			break;
		case Plus:
			first.printExpr(0);
			System.err.print("+");
			second.printExpr(0);
			break;
		case Minus:
			first.printExpr(0);
			System.err.print("-");
			second.printExpr(0);
			break;
		case Times:
			first.printExpr(0);
			System.err.print("*");
			second.printExpr(0);
			break;
		case ArrayLookup:
			first.printExpr(0);
			System.err.print("[");
			second.printExpr(0);
			System.err.print("]");
		case ArrayLen:
		case MsgSend:
			first.printExpr(0);
			System.err.print("."+e_id.name+"(");
			for (int i=0; i<e_list.size(); i++) {
				if (i>0) System.err.print(",");
				e_list.e_list.elementAt(i).printExpr(0);
			}
			System.err.print(")");
		}
	}
}
