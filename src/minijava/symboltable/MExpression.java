package minijava.symboltable;

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
	
	public MExpression(Operator op) {
		e_op = op;
		e_exp = new MPrimaryExpr();
		e_list = new MExpressionList();
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
		case ArrayLen:
		case MsgSend:
		}
	}
}
