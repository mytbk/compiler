/**
 * 该类用于表示声明的类
 */
package minijava.symboltable;

import minijava.minijava2piglet.PigletBinding;
import minijava.minijava2piglet.PigletTemp;
import minijava.typecheck.PrintError;

public class MClass extends MLocalVarType {
	public MClasses all_classes; // 所有类的列表，在建立符号表时完成
	public boolean isDeclared = false; // 是否已声明，用于检查符号表
	public String extend_class_name = null; // 所继承的类名
	public MClass extend_class = null; // 所继承的类，在符号表建立完成后才能求得
	public int extend_tag = 0; // 检测循环继承时用，0表示未检测
	public MMethodList methods;
	public MMethodList all_methods; // 该类所有方法，包括其父类，考虑方法覆盖

	public MClass(String v_name, MClasses all, int m_line, int m_column) {
		super(m_line, m_column);
		name = v_name;
		all_classes = all;
		methods = new MMethodList();
		all_methods = null;
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
	
	public void buildMethodRef() {
		if (all_methods!=null) {
			return;
		}
		if (extend_class==null) {
			all_methods = methods;
			return;
		}
		// now build the all_methods list
		if (extend_class.all_methods==null) {
			extend_class.buildMethodRef();
		}
		all_methods = new MMethodList();
		// first copy the list from parent class
		for (int i=0; i<extend_class.all_methods.size(); i++) {
			all_methods.methods.addElement(extend_class.all_methods.At(i));
		}
		// then insert my methods
		for (int i=0; i<methods.size(); i++) {
			int idx = all_methods.findMethod(methods.At(i).name);
			if (idx!=-1) {
				all_methods.methods.setElementAt(methods.At(i), idx);
			} else {
				all_methods.methods.addElement(methods.At(i));
			}
		}
	}
	
	public MMethod findMethodByName(String m) {
		int idx = methods.findMethod(m);
		if (idx==-1) {
			return null;
		} else {
			return methods.methods.elementAt(idx);
		}
	}
	
	public MMethod r_findMethodByName(String m) {
		MClass c = this;
		while (c!=null) {
			MMethod method = c.findMethodByName(m);
			if (method==null) {
				c = c.extend_class;
			} else {
				return method;
			}
		}
		return null;
	}
	
	public PigletBinding getVarBinding(String v_name) {
		int prev_vars = 0;
		for (MClass c=extend_class; c!=null; c=c.extend_class) {
			prev_vars += c.vars.size();
		}
		int idx = vars.findVar(v_name);
		if (idx!=-1) {
			String tmp = PigletTemp.newTmp();
			PigletBinding ret = new PigletBinding(null, null);
			ret.read = "\nBEGIN\nHLOAD " + tmp + " TEMP 0 " + (prev_vars+idx+1)*4
					+ "\nRETURN " + tmp + "\nEND";
			ret.write = "TEMP 0 " + (prev_vars+idx+1)*4;
			return ret;
		}
		// no such variable, find the parentclss
		if (extend_class!=null) {
			return extend_class.getVarBinding(v_name);
		} else {
			return null;
		}
	}
	
	public int getMethodBinding(String m_name) {
		// return the offset of a method with m_name as name
		int idx = all_methods.findMethod(m_name);
		if (idx!=-1) {
			return idx*4;
		} else {
			return -1;
		}
	}
	
	public String newString() {
		int nMethods = 0, nVars = 0;
		String result = "";
		String t_methods = PigletTemp.newTmp();
		String t_vars = PigletTemp.newTmp();
		
		for (MClass c=this; c!=null; c=c.extend_class) {
			nVars += c.vars.size();
		}
		nMethods = all_methods.size();
		
		result += "\nBEGIN\nMOVE " + t_methods + " HALLOCATE " + nMethods*4
				+ "\nMOVE " + t_vars + " HALLOCATE " + (nVars+1)*4 + "\n";
		// store all methods
		for (int i=0; i<nMethods; i++) {
			MMethod m_method = all_methods.At(i);
			result += "HSTORE " + t_methods + " " + i*4 + " "
					+ m_method.method_class.getName() + "_"
					+ m_method.getName()
					+ "\n";
		}
		/*
		for (MClass c=this; c!=null; c=c.extend_class) {
			for (int i=c.methods.size()-1; i>=0; i--) {
				--nMethods;
				result += "HSTORE " + t_methods + " " + nMethods*4 + " "
						+ c.getName() + "_" 
						+ c.methods.methods.elementAt(i).getName()
						+ "\n";
			}
		}*/
		result += "HSTORE " + t_vars + " 0 " + t_methods + "\n";
		result += "RETURN " + t_vars + "\nEND";
		return result;
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

