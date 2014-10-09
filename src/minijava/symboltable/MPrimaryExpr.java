package minijava.symboltable;

public class MPrimaryExpr extends MType {
	   /**
	    * f0 -> IntegerLiteral()
	    *       | TrueLiteral()
	    *       | FalseLiteral()
	    *       | Identifier()
	    *       | ThisExpression()
	    *       | ArrayAllocationExpression()
	    *       | AllocationExpression()
	    *       | NotExpression()
	    *       | BracketExpression()
	    */
	 
	public enum E_type {
		Int, True, False, Id, This, ArrayAlloc, Alloc, Not, Braket;
	}
	public E_type e_type;
	public int i_val; // int
	public MIdentifier e_id = null; // Id, ArrayAlloc, Alloc
	public MExpression e_exp = null; // ArrayAlloc, Not, Braket
	
	public int toInt() {
		return i_val;
	}
	
	public boolean toBool() {
		if (e_type==E_type.True) {
			return true;
		} else if (e_type==E_type.False) {
			return false;
		} else {
			throw new IllegalArgumentException("invalid call to toBool()");
		}
	}
	
	public void printPrimExpr(int spaces) {
		System.err.print(OutputFormat.spaces(spaces));
		
		switch (e_type) {
		case Int:
			System.err.print(i_val);
			break;
		case True:
		case False:
			System.err.print(this.toBool());
			break;
		case Id:
			System.err.print(e_id.getName());
			break;
		}
	}
}
