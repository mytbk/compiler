package spiglet.spiglet2kanga;

import java.util.HashSet;

public class SpgStmt extends SpgSym{
	public enum StmtType { NOOP, ERROR, CJUMP, JUMP, STORE, LOAD, MOVE, PRINT };
	StmtType type;
	public SpgTemp tmp1, tmp2;
	public int imm;
	public String jmptarget;
	public SpgExpr exp;
	
	public String lb;
	public SpgStmt succ1, succ2;
	
	public HashSet<SpgTemp> def, use;
	public HashSet<SpgTemp> in, out;
	
	public SpgStmt(StmtType t) {
		type = t;
		in = new HashSet<SpgTemp>();
		out = new HashSet<SpgTemp>();
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
	
	public void getDefUse() {
		switch (type) {
		case CJUMP:
			def = null;
			use = tmp1.getTmpUsed();
			break;
		case ERROR:
			def = use = null;
			break;
		case JUMP:
			def = use = null;
			break;
		case LOAD:
			def = tmp1.getTmpUsed();
			use = tmp2.getTmpUsed();
			break;
		case MOVE:
			def = tmp1.getTmpUsed();
			use = exp.getTmpUsed();
			break;
		case NOOP:
			def = use = null;
			break;
		case PRINT:
			def = null;
			use = exp.getTmpUsed();
			break;
		case STORE:
			def = null;
			use = tmp1.getTmpUsed();
			use.add(tmp2);
			break;
		default:
			System.err.println("Unknown statement type");
			def = use = null;
			break;
		
		}
	}
	
	public void printDefUse() {
		if (def!=null) {
			System.err.print("def: ");
			SpgTemp[] d = def.toArray(new SpgTemp[0]);
			for (int i=0; i<d.length; i++) {
				System.err.print(d[i].num+" ");
			}
		}
		System.err.println();
		if (use!=null) {
			System.err.print("use: ");
			SpgTemp[] u = use.toArray(new SpgTemp[0]);
			for (int i=0; i<u.length; i++) {
				System.err.print(u[i].num+" ");
			}
		}
		System.err.println();
	}
	
	public void printLiveIn() {
		System.err.print("LiveIn: ");
		SpgTemp[] live = in.toArray(new SpgTemp[0]);
		for (int i=0; i<live.length; i++) {
			System.err.print(live[i].num+" ");
		}
		System.err.println();
	}
	
}
