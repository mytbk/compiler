package minijava.symboltable;

public class MMethod extends MLocalVarType {
	MVarList params;
	String ret_type_name;
	MClass method_class;
	MVarList paramList;
	
	public MMethod(String m_name, int m_type, int m_column) {
		super(m_type, m_column);
		this.name = m_name;
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
}
