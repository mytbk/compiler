/**
 * 该类用于表示声明的类
 */
package minijava.symboltable;

import minijava.typecheck.PrintError;

public class MClass extends MLocalVarType {
	public MClasses all_classes; // 所有类的列表，在建立符号表时完成
	public boolean isDeclared = false; // 是否已声明，用于检查符号表
	public String extend_class_name = null; // 所继承的类名
	public MClass extend_class = null; // 所继承的类，在符号表建立完成后才能求得
	public int extend_tag = 0; // 检测循环继承时用，0表示未检测
	public MMethodList methods;

	public MClass(String v_name, MClasses all, int m_line, int m_column) {
		super(m_line, m_column);
		name = v_name;
		all_classes = all;
		methods = new MMethodList();
	}
	
	public String insertVar(MVariable var) {
		String var_name = var.getName();
		if (vars.findVar(var_name)!=-1) {
			return "Variable double declaration " + "\"" + var_name + "\"";
		}
		vars.insertVar(var);
		return null;
	}
	
	public String insertMethod(MMethod method) {
		String method_name = method.getName();
		if (vars.findVar(method_name)!=-1) {
			return "Method name \'" + method_name + "\' is the same of some variable.";
		}
		if (methods.findMethod(method_name)!=-1) {
			return "Method double declaration " + "\"" + method_name + "\".";
		}
		methods.addMethod(method);
		method.method_class = this;
		return null;
	}
	
	// 检测类型名是否有效
	public boolean validType(String s_type) {
		if (s_type==MIdentifier.intType
				|| 	s_type==MIdentifier.boolType 
				||	s_type==MIdentifier.arrType 
				||	s_type==MIdentifier.voidType
				|| s_type==MIdentifier.sArrayType) {
			return true;
		}
		
		if (all_classes.findClassByName(s_type)!=null) {
			return true;
		}
		
		return false;
	}
	
	// 对类进行类型检查
	public boolean classVarCheck() {
		boolean result = true;
		for (int i=0; i<methods.size(); i++) {
			result &= methods.methods.elementAt(i).validVars();
		}
		return result;
	}
	
	public boolean classFuncCheck() {
		boolean result = true;
		for (int i=0; i<methods.size(); i++) {
			result &= methods.methods.elementAt(i).validFunc();
		}
		return result;
	}
	
	public void classStatementCheck() {
		for (int i=0; i<methods.size(); i++) {
			methods.methods.elementAt(i).checkStatements();
		}
	}
	
	// 调试输出: 打印该类的信息
	public void printClass(int spaces) {
		String ps = OutputFormat.spaces(spaces);
		if (this.extend_class_name!=null) {
			System.err.println(ps + "Class "+this.name+" extends "+this.extend_class_name);
		} else {
			System.err.println(ps + "Class "+this.name);
		}
		for (int i=0; i<vars.size(); i++) {
			vars.varlist.elementAt(i).printVar(spaces+2);
			System.err.println();
		}
		for (int i=0; i<methods.size(); i++) {
			methods.methods.elementAt(i).printMethod(spaces+2);
		}
	}
}

