/* 可定义局部变量的抽象类 */

package minijava.symboltable;

public abstract class MLocalVarType extends MType {
	public MVarList vars;
	
	public MLocalVarType(int m_line, int m_column) {
		super(m_line, m_column);
		vars = new MVarList();
	}
	public abstract String insertVar(MVariable var);
}
