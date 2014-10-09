package minijava.symboltable;

public class MStatement extends MType {
	public enum Keyword {
		Block, Assign, ArrAssign, If, While, Print;
	}
	Keyword s_type;
	public MStatementList s_list; // for block statement
	public MIdentifier s_id; // for assign, array assign
	public MExpression e_first, e_second; // for assign(e_first), array assign, if, while, print
	public MStatement s_first, s_second; // for if, while
	
	public MStatement(Keyword keyw) {
		s_type = keyw;
		s_list = null;
		s_id = null;
		e_first = e_second = null;
		s_first = s_second = null;
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
			System.err.println(sp+"[]=");
			break;
		case If:
			System.err.println(sp+"if");
			break;
		case While:
			System.err.println(sp+"while");
			break;
		case Print:
			System.err.println(sp+"print");
			break;
		}
	}
}
