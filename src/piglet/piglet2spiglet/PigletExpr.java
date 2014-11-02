package piglet.piglet2spiglet;

public class PigletExpr {
	public enum Expr_t { Temp, Int, Label, Other };
	public Expr_t type;
	public String s;
	public PigletExpr(Expr_t t, String _s) {
		type = t;
		s = _s;
	}
	
	public boolean isSimple() {
		return type==Expr_t.Temp || type==Expr_t.Int || type==Expr_t.Label;
	}
	
	public String toString() {
		return s;
	}
}
