package spiglet.spiglet2kanga;

public class SpgStmt extends SpgSym{
	public enum StmtType { NOOP, ERROR, CJUMP, JUMP, STORE, LOAD, MOVE, PRINT };
	StmtType type;
	public SpgTemp tmp1, tmp2;
	public int imm;
	public String jmptarget;
	public SpgExpr exp;
	
	public String lb;
	public SpgStmt succ1, succ2;
	
	public SpgStmt(StmtType t) {
		type = t;
	}
	
	public String toString() {
		String str="";
		if (lb!=null) str = lb + " ";
		switch (type) {
		case CJUMP:
			return str + "CJUMP " + tmp1.toString() + " " + jmptarget;
		case ERROR:
			return str + "ERROR";
		case JUMP:
			return str + "JUMP " + jmptarget;
		case LOAD:
			return str + "LD " + tmp1.toString() + " " + tmp2.toString() + " " + imm;
		case MOVE:
			return str + "MOVE " + tmp1.toString() + " " + exp.toString();
		case NOOP:
			return str + "NOOP";
		case PRINT:
			return str + "PRINT " + exp.toString();
		case STORE:
			return str + "STORE " + tmp1.toString() + " " + imm + " " + tmp2.toString();
		default:
			return null;		
		}
	}
}
