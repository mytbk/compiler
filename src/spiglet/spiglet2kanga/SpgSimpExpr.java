package spiglet.spiglet2kanga;

import java.util.HashSet;

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
	
	public HashSet<SpgTemp> getTmpUsed() {
		if (this instanceof SpgTemp) {
			System.err.println("Should not go here...");
			HashSet<SpgTemp> s = new HashSet<SpgTemp>();
			s.add((SpgTemp)this);
			return s;
		} else {
			return null;
		}
	}
}
