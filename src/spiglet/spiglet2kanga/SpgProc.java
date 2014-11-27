package spiglet.spiglet2kanga;

import java.util.HashMap;
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
	
	public void setJmpTarget() {
		// convert all the jump labels to real statement
		
		// first scan all the labels
		HashMap<String, SpgStmt> s = new HashMap<String, SpgStmt>();
		for (int i=0; i<statements.size(); i++) {
			SpgStmt stmt = statements.elementAt(i);
			if (stmt.lb!=null) {
				s.put(stmt.lb, stmt);
			}
		}
		
		for (int i=0; i<statements.size(); i++) {
			SpgStmt stmt = statements.elementAt(i);
			if (stmt.type==SpgStmt.StmtType.JUMP) {
				stmt.succ1 = s.get(stmt.jmptarget);
				stmt.succ2 = null;
			} else if (stmt.type==SpgStmt.StmtType.CJUMP) {
				if (i==statements.size()-1) {
					stmt.succ1 = null;
				} else {
					stmt.succ1 = statements.elementAt(i+1);
				}
				stmt.succ2 = s.get(stmt.jmptarget);
			} else {
				if (i==statements.size()-1) {
					stmt.succ1 = null;
				}
			}
		}
	}
	
	
	
	
}
