package spiglet.spiglet2kanga;

import java.util.Vector;

public class SpgProc extends SpgSym {
	String name;
	int argCount;
	int stkCount;
	int maxCallArgCount;
	
	public Vector<SpgStmt> statements;
	public SpgSimpExpr retexp;
	
	public SpgProc(String s, int n) {
		name = s;
		argCount = n;
		statements = new Vector<SpgStmt>();
	}
	
	public void addStmt(SpgStmt s) {
		statements.addElement(s);
	}
	
}
