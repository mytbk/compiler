package spiglet.spiglet2kanga;

import java.util.HashSet;

public class SpgTemp extends SpgSimpExpr {
	public SpgTemp(int n) {
		super(SpgSimpExpr.SExprType.TEMP);
		num = n;
	}
	
	public int tempNum() {
		return num;
	}
	
	public String toString() {
		return "TEMP " + num;
	}

	public HashSet<SpgTemp> getTmpUsed() {
		HashSet<SpgTemp> s = new HashSet<SpgTemp>();
		s.add((SpgTemp)this);
		return s;
	}
	
	@Override
	public boolean equals(Object t) {
		if (t instanceof SpgTemp) {
			return num==((SpgTemp)t).num;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return num;
	}
}
