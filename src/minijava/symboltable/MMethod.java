package minijava.symboltable;

public class MMethod extends MLocalVarType {
	MVarList params;
	String ret_type_name;
	MClass method_class;
	MVarList paramList;
	
	public MMethod(String m_name, String ret_type, int m_line, int m_column) {
		super(m_line, m_column);
		this.name = m_name;
		this.ret_type_name = ret_type;
		ret_type_name = null;
		paramList = new MVarList();
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
}
