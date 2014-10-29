package minijava.symboltable;

import java.util.HashMap;
import java.util.Map;

import minijava.minijava2piglet.PigletBinding;
import minijava.minijava2piglet.PigletTemp;
import minijava.typecheck.PrintError;

public class MMethod extends MLocalVarType {
	MVarList params;
	public String ret_type_name;
	public MClass method_class;
	public MVarList paramList;
	public MStatementList statements;
	public MExpression ret_expr;
	public HashMap<String, String> varBinding;
	
	public MMethod(String m_name, String ret_type, int m_line, int m_column) {
		super(m_line, m_column);
		this.name = m_name;
		this.ret_type_name = ret_type;
		paramList = new MVarList();
		statements = new MStatementList();
	}
	
	public String insertVar(MVariable var) {
		String var_name = var.getName();
		if (paramList.findVar(var_name)!=-1) {
			return "Variable \'" + var_name + "\' already in param list!";
		}
		if (vars.findVar(var_name)!=-1) {
			return "Variable double declaration " + "\"" + var_name + "\"";	
		}
		vars.insertVar(var);
		return null;
	}
	
	public String addParam(MVariable var) {
		String var_name = var.getName();
		if (paramList.findVar(var_name)!=-1) {
			return "Parameter variable double declaration: " + var_name;
		}
		paramList.insertVar(var);
		return null;
	}
	
	// 检测函数的返回类型，参数类型，局部变量类型是否为有效类型
	public boolean validVars() {
		if (method_class.validType(ret_type_name)==false) {
			PrintError.print(this.line, this.column, "Return type of method "+this.name+" invalid");
			return false;
		}
		
		for (int i=0; i<paramList.size(); i++) {
			MVariable p_var = paramList.varlist.elementAt(i);
			if (method_class.validType(p_var.typename)==false) {
				PrintError.print(p_var.line, p_var.column, "Type "+p_var.typename+" invalid");
				return false;
			}
		}
		
		for (int i=0; i<vars.size(); i++) {
			MVariable m_var = vars.varlist.elementAt(i);
			if (method_class.validType(m_var.typename)==false) {
				PrintError.print(m_var.line, m_var.column, "Type "+m_var.typename+" invalid");
				return false;
			}
		}
		
		return true;
	}
	
	// 检查方法自身是否复合方法覆盖条件
	public boolean validFunc() {
		MClass m_class = method_class;
		while (m_class.extend_class!=null) {
			m_class = m_class.extend_class;
			int idx = m_class.methods.findMethod(this.name);
			if (idx!=-1) {
				// 找到同名方法，检测是否能覆盖
				if (this.ret_type_name.
						equals(m_class.methods.methods.elementAt(idx).ret_type_name)==false) {
					PrintError.print(line, column, "Method " +
						this.name + " has type different from the one in its parent class "
						+ m_class.name);
					return false;
				}
			}
		}
		return true;
	}
	
	// 从局部变量，参数表，类变量中查找变量
	public MVariable findVarByName(String s) {
		int idx;
		idx = vars.findVar(s);
		if (idx!=-1) {
			return vars.varlist.elementAt(idx);
		}
		idx = paramList.findVar(s);
		if (idx!=-1) {
			return paramList.varlist.elementAt(idx);
		}
		MClass m_class = method_class;
		idx = m_class.vars.findVar(s);		
		while (idx==-1 && m_class.extend_class!=null) {
			// 在父类继续查找
			m_class = m_class.extend_class;
			idx = m_class.vars.findVar(s);
		}
		if (idx!=-1) {
			return m_class.vars.varlist.elementAt(idx);
		}
		return null;
	}
	
	// generate local variable bindings, call it when visitor visits the method
	public void genLocalVarBindings() {
		varBinding = new HashMap<String, String>();
		for (int i=0; i<vars.size(); i++) {
			varBinding.put(vars.varlist.elementAt(i).getName(), PigletTemp.newTmp());
		}
	}
	
	// get a binding, call it when visitor visits a statement
	public PigletBinding getBinding(String id) {
		String result;
		result = varBinding.get(id);
		if (result!=null) {
			return new PigletBinding(result, null);
		}
		// not in local variables, then find paramlist
		for (int i=0; i<paramList.size(); i++) {
			if (paramList.varlist.elementAt(i).name.equals(id)) {
				if (i<18 || paramList.size()<20) {
					result = "TEMP "+(i+1);
					return new PigletBinding(result, null);
				} else {
					// >=20 parameters, the last parameter should be a pointer to 
					// the rest parameters
					int restidx = i-18;
					String tmp = PigletTemp.newTmp();
					PigletBinding ret = new PigletBinding(null, null);
					ret.read = "\nBEGIN\nHLOAD " + tmp + 
							" TEMP 19 " + restidx*4 + "\nRETURN " + tmp + "\nEND";
					ret.write = "TEMP 19 " + restidx*4;
					return ret;
				}
			}
		}
		// not in paramList either, check for the class
		return method_class.getVarBinding(id);
	}
	
	public void checkStatements() {
		statements.checkStatements(this);
		// 检查返回类型是否和返回值匹配
		if (ret_type_name==MIdentifier.voidType && ret_expr==null) {
			// ok
		} else if (!ret_type_name.equals(ret_expr.exprType(this))) {
			MClasses all = this.method_class.all_classes;
			MClass t1 = all.findClassByName(ret_type_name);
			MClass t2 = all.findClassByName(ret_expr.exprType(this));
			if (t1!=null) {
				while (t2!=null && t2!=t1) {
					t2 = t2.extend_class;
				}
			}
			if (t1!=t2) {
				PrintError.print(line, column, "return expression and return type mismatch!");
			}
		}
	}
	
	public void printMethod(int spaces) {
		String ps = OutputFormat.spaces(spaces);
		System.err.print(ps + this.ret_type_name + " " + this.name + "(");
		// 输出形式参数
		for (int i=0; i<paramList.size(); i++) {
			if (i>0) System.err.print(",");
			paramList.varlist.elementAt(i).printVar(0);
		}
		System.err.println(")");
		// 输出局部变量
		for (int i=0; i<vars.size(); i++) {
			vars.varlist.elementAt(i).printVar(spaces+2);
			System.err.println();
		}
		// 输出返回值
		if (ret_expr!=null) {
			System.err.print(ps+"  return ");
			ret_expr.printExpr(0);
			System.err.println();
		}
		statements.printStatements(spaces+2);
	}
}
