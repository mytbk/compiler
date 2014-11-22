package spiglet.spiglet2kanga;

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
}
