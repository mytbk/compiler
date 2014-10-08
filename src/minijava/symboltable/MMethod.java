package minijava.symboltable;

public class MMethod extends MType {
	MVarList params;
	String ret_type_name;
	
	public MMethod(String m_name, int m_type, int m_column) {
		super(m_type, m_column);
		this.name = m_name;
	}
}
