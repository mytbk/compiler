package minijava.symboltable;

public class MVariable extends MType {
	String typename;
	
	public MVariable(String v_name, String v_type, int v_line, int v_column) {
		super(v_line, v_column);
		name = v_name;
		typename = v_type;
	}
	
	public void printVar(int spaces) {
		String ps = OutputFormat.spaces(spaces);
		System.err.print(ps + typename + " " + this.name);
	}
}
