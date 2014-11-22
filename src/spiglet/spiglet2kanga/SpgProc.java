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
	
	public void printProc() {
		System.err.println(name + "[" + argCount + "]");
		for (int i=0; i<statements.size(); i++) {
			System.err.println(statements.elementAt(i).toString());
		}
		if (retexp!=null) {
			System.err.println("RETURN " + retexp.toString());
		}
		System.err.println();
	}
}
