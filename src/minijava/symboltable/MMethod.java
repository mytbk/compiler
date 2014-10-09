package minijava.symboltable;

import minijava.typecheck.PrintError;

public class MMethod extends MLocalVarType {
	MVarList params;
	String ret_type_name;
	public MClass method_class;
	MVarList paramList;
	public MStatementList statements;
	public MExpression ret_expr;
	
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
						equals(m_class.methods.methods.elementAt(idx))==false) {
					PrintError.print(line, column, "Method " +
						this.name + " has type different from the one in its parent class");
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
		idx = method_class.vars.findVar(s);
		if (idx!=-1) {
			return method_class.vars.varlist.elementAt(idx);
		}
		return null;
	}
	
	public void checkStatements() {
		statements.checkStatements(this);
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
