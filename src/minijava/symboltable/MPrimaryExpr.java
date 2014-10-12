package minijava.symboltable;

import minijava.typecheck.PrintError;

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
	
	public String primExprType(MMethod m) {
		switch (e_type) {
		case Int:
			return MIdentifier.intType;
		case Alloc:
			return e_id.getName();
		case ArrayAlloc:
			if (e_exp.exprType(m)!=MIdentifier.intType) {
				PrintError.print(getLine(), getColumn(), "wrong type of expression in array alloc");
				return null;
			}
			return MIdentifier.arrType;
		case Braket:
			return e_exp.exprType(m);
		case True:
		case False:
			return MIdentifier.boolType;
		case Id:
			MVariable m_var = m.findVarByName(e_id.name);
			if (m_var!=null) {
				return m_var.typename;
			} else {
				return null;
			}
		case Not:
			if (e_exp.exprType(m)==MIdentifier.boolType) {
				return MIdentifier.boolType;
			} else {
				PrintError.print(getLine(), getColumn(), "A non boolean expr after '!' operator");
				return null;
			}
		case This:
			return m.method_class.name;
		default:
			return null;
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
		case Alloc:
			System.err.print("new "+e_id.getName()+"()");
			break;
		case ArrayAlloc:
			System.err.print("new int[");
			e_exp.printExpr(0);
			System.err.print("]");
			break;
		}
	}
}
