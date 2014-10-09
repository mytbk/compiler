package minijava.symboltable;

import java.util.Vector;

public class MExpressionList extends MType {
	Vector<MExpression> e_list;
	
	public MExpressionList() {
		e_list = new Vector<MExpression>();
	}
	
	public void add_expr(MExpression e) {
		e_list.addElement(e);
	}
	
	public int size() {
		return e_list.size();
	}
}
