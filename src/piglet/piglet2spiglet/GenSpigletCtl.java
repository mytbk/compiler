package piglet.piglet2spiglet;

import java.util.Vector;

public class GenSpigletCtl {
	/* DEFAULT: return a PigletExpr
	 * PRINT: used with procedure, print the StmtExp
	 * LIST: used with call, add things to list, return null
	 */
	public enum Control { DEFAULT, PRINT, LIST };
	Control c;
	public Vector<PigletExpr> call_list;
	
	public GenSpigletCtl(Control _c) {
		c = _c;
		call_list = new Vector<PigletExpr>();
	}
	public boolean isPrint() {
		return c==Control.PRINT;
	}
	
	public boolean isList() {
		return c==Control.LIST;
	}
}
