package spiglet.spiglet2kanga;

import java.util.HashSet;
import java.util.Vector;

public class SpgExpr extends SpgSym {
	public enum ExpType { CALL, ALLOC, BinOp, Simple };
	
	/* 
	 * CALL Simp (Tmp Tmp ...)
	 * HALLOCATE Simp
	 * BinOP Tmp Simp
	 * Simp := Tmp | Int | Label
	 */
	
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
	
	public String toString() {
		String str;
		switch (type) {
		case ALLOC:
			return "ALLOC " + se.toString();
		case BinOp:
			return op + " " + oprand.toString() + " " + se.toString();
		case CALL:
			str = "CALL " + se.toString();
			for (int i=0; i<callParams.size(); i++) {
				str += " " + callParams.elementAt(i).toString();
			}
			return str;
		case Simple:
			return ((SpgSimpExpr)this).toString();
		default:
			return null;
		
		}
	}
	
	public HashSet<SpgTemp> getTmpUsed() {
		HashSet<SpgTemp> s = new HashSet<SpgTemp>();
		HashSet<SpgTemp> stmp;
		switch (type) {
		case ALLOC:
			return se.getTmpUsed();
		case BinOp:
			s.add(oprand);
			stmp = se.getTmpUsed();
			if (stmp!=null) {
				s.addAll(se.getTmpUsed());
			}
			return s;
		case CALL:
			stmp = se.getTmpUsed();
			if (stmp!=null) {
				s.addAll(se.getTmpUsed());
			}
			s.addAll(callParams);
			return s;
		case Simple:
			System.err.println("Should not be here...");
			return ((SpgSimpExpr)this).getTmpUsed();
		default:
			return null;
		
		}
	}
}
