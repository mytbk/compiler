package minijava.symboltable;

public class MMethod extends MLocalVarType {
	MVarList params;
	String ret_type_name;
	MClass method_class;
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
