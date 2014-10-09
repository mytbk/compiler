package minijava.symboltable;

import minijava.typecheck.PrintError;

public class MStatement extends MType {
	public enum Keyword {
		Block, Assign, ArrAssign, If, While, Print;
	}
	Keyword s_type;
	public MStatementList s_list; // for block statement
	public MIdentifier s_id; // for assign, array assign
	public MExpression e_first, e_second; // for assign(e_first), array assign, if, while, print
	public MStatement s_first, s_second; // for if, while
	
	public MStatement(int s_line, int s_column, Keyword keyw) {
		super(s_line, s_column);
		s_type = keyw;
		s_list = null;
		s_id = null;
		e_first = e_second = null;
		s_first = s_second = null;
	}
	
	public void checkStatement(MMethod m) {
		switch (s_type) {
		case ArrAssign:
			// id[expr] = expr
			if (m.findVarByName(s_id.name).typename==MIdentifier.arrType
			&& e_first.exprType(m)==MIdentifier.intType 
			&& e_second.exprType(m)==MIdentifier.intType) {
				return;
			} else {
				PrintError.print(line, column, "type mismatch in array assign statement");
			}
			break;
		case Assign:
			// id = expr
			MVariable m_var = m.findVarByName(s_id.name);
			if (m_var==null) {
				PrintError.print(line, column, "variable "+s_id.name+" not exist!");
				return;
			} else if (m_var.typename.equals(e_first.exprType(m))) {
				return;
			} else {
				PrintError.print(line, column, "type mismatch in assign statement");
			}
			break;
		case Block:
			s_list.checkStatements(m);
			break;
		case If:
			if (e_first.exprType(m)==MIdentifier.boolType) {
				s_first.checkStatement(m);
				s_second.checkStatement(m);
			} else {
				PrintError.print(line, column, "invalid type in if statement.");
			}
			break;
		case Print:
			if (e_first.exprType(m)!=null) {
				return;
			} else {
				PrintError.print(line, column, "Invalid type in print statement");
			}
			break;
		case While:
			// while (expr) state
			if (e_first.exprType(m)==MIdentifier.boolType) {
				s_first.checkStatement(m);
			} else {
				PrintError.print(line, column, "invalid type in while statement.");
			}
			break;
		default:
			break;
		
		}
		
	}
	
	public void printStatement(int spaces) {
		String sp = OutputFormat.spaces(spaces);
		
		switch (s_type) {
		case Block:
			System.err.println(sp+"Block statement:");
			break;
		case Assign:
			System.err.print(sp+"="+" ("+s_id.getName()+") (");
			e_first.printExpr(0);
			System.err.println(")");
			break;
		case ArrAssign:
			System.err.println(sp+"[]= ("+s_id.getName()+") (");
			e_first.printExpr(0);
			System.err.print(") (");
			e_second.printExpr(0);
			System.err.println(")");
			break;
		case If:
			System.err.println(sp+"if (");
			e_first.printExpr(0);
			System.err.println(")");
			s_first.printStatement(spaces+2);
			s_second.printStatement(spaces+2);
			break;
		case While:
			System.err.println(sp+"while (");
			e_first.printExpr(0);
			System.err.println(")");
			s_first.printStatement(spaces+2);
			break;
		case Print:
			System.err.println(sp+"print (");
			e_first.printExpr(0);
			System.err.println(")");
			break;
		}
	}

}
