package kanga.kanga2mips;

public class KangaSym {
	public enum Type { HALLOC, BINOP, SIMPLE };
	public enum SimpleType { REG, INT, LABEL };
	
	public Type e_type;
	public SimpleType s_type;
	public int value; // for integer simple type
	public int binop; // Binary operator
	public String name; // for register or label name
	public String name2; // for second oprand
	public KangaSym oprand2; // for second oprand
	
	public KangaSym(Type t) {
		e_type = t;
	}
	
	public KangaSym(Type t, SimpleType s) {
		e_type = t;
		s_type = s;
	}
	
}
