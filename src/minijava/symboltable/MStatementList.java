package minijava.symboltable;

import java.util.Vector;

public class MStatementList extends MType {
	Vector<MStatement> s_list;

	public MStatementList() {
		s_list = new Vector<MStatement>();
	}
	
	public void addStatement(MStatement s) {
		s_list.addElement(s);
	}
	
	public int size() {
		return s_list.size();
	}
	
	public void printStatements(int spaces) {
		String sp = OutputFormat.spaces(spaces);
		for (int i=0; i<s_list.size(); i++) {
			s_list.elementAt(i).printStatement(spaces);
		}
	}
}
