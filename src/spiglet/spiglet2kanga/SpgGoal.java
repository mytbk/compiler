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
	
	public void printGoal() {
		for (int i=0; i<procs.size(); i++) {
			procs.elementAt(i).printProc();
		}
	}
	
	public void preProcess() {
		for (int i=0; i<procs.size(); i++) {
			SpgProc p = procs.elementAt(i);
			p.setJmpTarget();
			p.getDefUse();
			p.getActiveVars();
		}
	}
}
