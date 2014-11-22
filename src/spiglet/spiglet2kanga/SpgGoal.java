package spiglet.spiglet2kanga;

import java.util.Vector;

public class SpgGoal extends SpgSym {
	Vector<SpgProc> procs;
	
	public SpgGoal() {
		procs = new Vector<SpgProc>();
	}
	
	public void addProc(SpgProc p) {
		procs.addElement(p);
	}
}
