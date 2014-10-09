/**
 * 表示标识符的类，可用于表示变量
 */
package minijava.symboltable;

public class MIdentifier extends MType {
	// 定义内置类型
	public static final String arrType = "int[]";
	public static final String intType = "int";
	public static final String boolType = "boolean";
	public static final String voidType = "void";
	public static final String sArrayType = "String[]";
	
	public MIdentifier(String v_name, int v_line, int v_column) {
		super(v_line, v_column);
		name = v_name;
	}
}

