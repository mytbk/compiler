package minijava.symboltable;

import java.util.Vector;

public class MMethodList extends MType {
	Vector<MMethod> methods;
	
	MMethodList() {
		methods = new Vector<MMethod>();
	}
	
	public void addMethod(MMethod method) {
		methods.addElement(method);
	}
	
	public int findMethod(String m_name) {
		for (int i=0; i<methods.size(); i++) {
			if (methods.elementAt(i).name.equals(m_name)) {
				return i;
			}
		}
		return -1;
	}
	
	public int size() {
		return methods.size();
	}
}
