/**
 * 所有声明的类的列表
 */
package minijava.symboltable;

import java.util.Vector;

import minijava.typecheck.PrintError;

public class MClasses extends MType {
	public Vector<MClass> mj_classes = new Vector<MClass>(); // 用于存放类

	public MClass findClassByName(String c_name) {
		for (int i=0; i<mj_classes.size(); i++) {
			if (mj_classes.elementAt(i).getName().equals(c_name)) {
				return mj_classes.elementAt(i);
			}
		}
		return null;
	}
	
	// 在表中插入类
	public String InsertClass(MClass v_class) { 
		String class_name = v_class.getName();
		if (Repeated(class_name)) // 如已经定义过该类，返回错误信息
			return "Class double declaration " + "\"" + class_name + "\"";
		mj_classes.addElement(v_class);
		return null;
	}

	// 判定是否定义同名的类
	public boolean Repeated(String class_name) {
		int sz = mj_classes.size();
		for (int i = 0; i < sz; i++) {
			String c_name = ((MClass) mj_classes.elementAt(i)).getName();
			if (c_name.equals(class_name))
				return true;
		}
		return false;
	}
	
	public void buildClassRelation() {
		// 通过extend_class_name确定extend_class, 并查找循环继承
		for (int i=0; i<mj_classes.size(); i++) {
			MClass m_class = mj_classes.elementAt(i);
			String ext_name = m_class.extend_class_name;
			if (ext_name!=null) {
				m_class.extend_class = findClassByName(ext_name);
				if (m_class.extend_class==null) {
					PrintError.print(m_class.line, m_class.column, "Extend class not exist!");
				}
			}
		}
		int tag = 1; // 扫描循环继承的标记
		for (int i=0; i<mj_classes.size(); i++) {
			MClass m_class = mj_classes.elementAt(i);
			if (m_class.extend_tag==0) {
				m_class.extend_tag = tag;
				while (m_class.extend_class!=null) {
					m_class = m_class.extend_class;
					if (m_class.extend_tag==tag) { // 循环继承
						PrintError.print(m_class.line, m_class.column, 
								"Circular extend in class"+m_class.name);
						break; // error
					} else if (m_class.extend_tag==0) {
						m_class.extend_tag = tag;
					} else {
						break; // no error
					}
				}
				tag++;
			}
		}
	}
	
	public boolean checkAllVars() {
		boolean result = true;
		for (int i=0; i<mj_classes.size(); i++) {
			result &= mj_classes.elementAt(i).classVarCheck();
		}
		return result;
	}
	
	public boolean checkAllFuncs() {
		boolean result = true;
		for (int i=0; i<mj_classes.size(); i++) {
			result &= mj_classes.elementAt(i).classFuncCheck();
		}
		return result;
	}
	
	public void checkAllStatements() {
		for (int i=0; i<mj_classes.size(); i++) {
			mj_classes.elementAt(i).classStatementCheck();
		}
	}
	
	// 调试输出: 打印所有的类的信息
	public void printClasses(int spaces) {
		for (int i=0; i<mj_classes.size(); i++) {
			mj_classes.elementAt(i).printClass(spaces);
		}
	}
}

