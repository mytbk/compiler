package minijava.symboltable;

import java.util.Vector;

public class MVarList extends MType {
	/* a class for storing variables */
	Vector<MVariable> varlist;
	public MVarList() {
		varlist = new Vector<MVariable>();
	}
	
	public void insertVar(MVariable var) {
		varlist.addElement(var);
	}
	
	public int findVar(String v_name) {
		for (int i=0; i<varlist.size(); i++) {
			if (varlist.elementAt(i).name.equals(v_name)) {
				return i;
			}
		}
		return -1;
	}
}
