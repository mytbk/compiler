package spiglet.spiglet2kanga;

public class SpgSimpExpr extends SpgExpr {
	public enum SExprType { TEMP, INT, LB };
	SExprType type;
	public int num;
	public String s;
	
	public SpgSimpExpr(SExprType t) {
		super(SpgExpr.ExpType.Simple);
		type = t;
	}
	
	public String toString() {
		switch (type) {
		case INT:
			return String.valueOf(num);
		case LB:
			return s;
		case TEMP:
			return ((SpgTemp)this).toString();
		default:
			return null;
		
		}
	}
}
