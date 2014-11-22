package spiglet.spiglet2kanga;

import java.util.Vector;

public class SpgExpr extends SpgSym {
	public enum ExpType { CALL, ALLOC, BinOp, Simple };
	
	public ExpType type;
	public String op;
	public SpgTemp oprand;
	public SpgSimpExpr se;
	public Vector<SpgTemp> callParams;
	
	public SpgExpr(ExpType t) {
		type = t;
		callParams = new Vector<SpgTemp>();
	}
	
	public void addCallParam(SpgTemp t) {
		if (type!=ExpType.CALL) {
			System.err.println("Error adding parameter, should be a CALL expression.");
			return;
		}
		callParams.addElement(t);
	}
	
}
