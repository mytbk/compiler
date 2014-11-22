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
}
