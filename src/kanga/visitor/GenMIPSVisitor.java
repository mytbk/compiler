package kanga.visitor;

import aux.CCPrinter;
import kanga.kanga2mips.KangaSym;
import kanga.syntaxtree.ALoadStmt;
import kanga.syntaxtree.AStoreStmt;
import kanga.syntaxtree.BinOp;
import kanga.syntaxtree.CJumpStmt;
import kanga.syntaxtree.CallStmt;
import kanga.syntaxtree.ErrorStmt;
import kanga.syntaxtree.Exp;
import kanga.syntaxtree.Goal;
import kanga.syntaxtree.HAllocate;
import kanga.syntaxtree.HLoadStmt;
import kanga.syntaxtree.HStoreStmt;
import kanga.syntaxtree.IntegerLiteral;
import kanga.syntaxtree.JumpStmt;
import kanga.syntaxtree.Label;
import kanga.syntaxtree.MoveStmt;
import kanga.syntaxtree.NoOpStmt;
import kanga.syntaxtree.Operator;
import kanga.syntaxtree.PassArgStmt;
import kanga.syntaxtree.PrintStmt;
import kanga.syntaxtree.Procedure;
import kanga.syntaxtree.Reg;
import kanga.syntaxtree.SimpleExp;
import kanga.syntaxtree.SpilledArg;
import kanga.syntaxtree.Stmt;
import kanga.syntaxtree.StmtList;

public class GenMIPSVisitor extends GJDepthFirst<KangaSym, KangaSym> {
	static final String Registers[] = {"a0", "a1", "a2", "a3",
		"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
		"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
		"t8", "t9", "v0", "v1"
	};
	static final String BinInst[] = { "slt", "add", "sub", "mul" };
	
	public CCPrinter prn;
	KangaSym _sym;
	int argoffset; // used for spilled arg and pass arg
	
	public GenMIPSVisitor() {
		prn = new CCPrinter();
		_sym = new KangaSym(null);
	}
	
	   //
	   // User-generated visitor methods below
	   //

	/**
	 * f0 -> "MAIN"
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> "["
	 * f5 -> IntegerLiteral()
	 * f6 -> "]"
	 * f7 -> "["
	 * f8 -> IntegerLiteral()
	 * f9 -> "]"
	 * f10 -> StmtList()
	 * f11 -> "END"
	 * f12 -> ( Procedure() )*
	 * f13 -> <EOF>
	 */
	public KangaSym visit(Goal n, KangaSym argu) {
		prn.print(".text\n.globl _print\n.globl _alloc\n"
				+ "_print:\n"
				+ "li $v0, 1\n"
				+ "syscall\n"
				+ "li $v0, 4\n"
				+ "la $a0, newLine\n"
				+ "syscall\n"
				+ "jr $ra\n\n"
				+ "_alloc:\n"
				+ "li $v0, 9\n"
				+ "syscall\n"
				+ "jr $ra\n\n"
				+ ".data\n"
				+ ".globl newLine\n"
				+ "newLine:"
				+ ".asciiz \"\\n\"\n\n");
		prn.print(".text\n.globl main\nmain:\n");
		int nMaxarg = Integer.parseInt(n.f8.f0.tokenImage);
		if (nMaxarg>4) {
			argoffset = nMaxarg-4;
		} else {
			argoffset = 0;
		}
		int nStack = Integer.parseInt(n.f5.f0.tokenImage)+2;
		prn.print("sw $fp, -8($sp)\n"
				+ "move $fp, $sp\n"
				+ "sw $ra, -4($fp)\n"
				+ "sub $sp, $sp, " + (nStack+argoffset)*4 + "\n");
		int nPassarg = Integer.parseInt(n.f2.f0.tokenImage)-4;
		for (int i=0; i<nPassarg; i++) {
			prn.print("lw $t0, " + i*4 + "($fp)\n"
					+ "sw $t0, " + (i+argoffset)*4 + "($sp)\n");
		}
		n.f10.accept(this, argu);
		prn.print("lw $ra, -4($fp)\n"
				+ "move $sp, $fp\n"
				+ "lw $fp, -8($sp)\n"
				+ "jr $ra\n");
		n.f12.accept(this, argu);
		return null;
	}	

	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	 */
	public KangaSym visit(StmtList n, KangaSym argu) {
		n.f0.accept(this, _sym);
		return null;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> "["
	 * f5 -> IntegerLiteral()
	 * f6 -> "]"
	 * f7 -> "["
	 * f8 -> IntegerLiteral()
	 * f9 -> "]"
	 * f10 -> StmtList()
	 * f11 -> "END"
	 */
	public KangaSym visit(Procedure n, KangaSym argu) {
		String name = n.f0.f0.tokenImage;
		prn.print(".text\n.globl " + name + "\n" + name + ":\n");
		int nMaxarg = Integer.parseInt(n.f8.f0.tokenImage);
		if (nMaxarg>4) {
			argoffset = nMaxarg-4;
		} else {
			argoffset = 0;
		}
		int nStack = Integer.parseInt(n.f5.f0.tokenImage)+2;
		prn.print("sw $fp, -8($sp)\n"
				+ "move $fp, $sp\n"
				+ "sw $ra, -4($fp)\n"
				+ "sub $sp, $sp, " + (nStack+argoffset)*4 + "\n");
		int nPassarg = Integer.parseInt(n.f2.f0.tokenImage)-4;
		for (int i=0; i<nPassarg; i++) {
			prn.print("lw $t0, " + i*4 + "($fp)\n"
					+ "sw $t0, " + (i+argoffset)*4 + "($sp)\n");
		}
		n.f10.accept(this, argu);
		prn.print("lw $ra, -4($fp)\n"
				+ "move $sp, $fp\n"
				+ "lw $fp, -8($sp)\n"
				+ "jr $ra\n");
		return null;
	}

	/**
	 * f0 -> NoOpStmt()
	 *       | ErrorStmt()
	 *       | CJumpStmt()
	 *       | JumpStmt()
	 *       | HStoreStmt()
	 *       | HLoadStmt()
	 *       | MoveStmt()
	 *       | PrintStmt()
	 *       | ALoadStmt()
	 *       | AStoreStmt()
	 *       | PassArgStmt()
	 *       | CallStmt()
	 */
	public KangaSym visit(Stmt n, KangaSym argu) {
		n.f0.accept(this, null);
		return null;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public KangaSym visit(NoOpStmt n, KangaSym argu) {
		prn.print("nop\n");;
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public KangaSym visit(ErrorStmt n, KangaSym argu) {
		prn.print("li $v0, 10\n"
				+ "li $a0, 1\n"
				+ "syscall\n");
		return null;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Reg()
	 * f2 -> Label()
	 */
	public KangaSym visit(CJumpStmt n, KangaSym argu) {
		prn.print("beq $" + Registers[n.f1.f0.which] + ", $0, " 
				+ n.f2.f0.tokenImage + "\n");
		return null;
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public KangaSym visit(JumpStmt n, KangaSym argu) {
		prn.print("j " + n.f1.f0.tokenImage + "\n");
		return null;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Reg()
	 * f2 -> IntegerLiteral()
	 * f3 -> Reg()
	 */
	public KangaSym visit(HStoreStmt n, KangaSym argu) {
		prn.print("sw $" + Registers[n.f3.f0.which] + ", " 
				+ n.f2.f0.tokenImage + "($" + Registers[n.f1.f0.which] + ")\n");
		return null;
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Reg()
	 * f2 -> Reg()
	 * f3 -> IntegerLiteral()
	 */
	public KangaSym visit(HLoadStmt n, KangaSym argu) {
		prn.print("lw $" + Registers[n.f1.f0.which] + ", " 
				+ n.f3.f0.tokenImage + "($" + Registers[n.f2.f0.which] + ")\n");
		return null;
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Reg()
	 * f2 -> Exp()
	 */
	public KangaSym visit(MoveStmt n, KangaSym argu) {
		String reg_name = Registers[n.f1.f0.which];
		KangaSym exp = n.f2.accept(this, argu);
		switch (exp.e_type) {
		case BINOP:
			if (exp.oprand2.s_type==KangaSym.SimpleType.REG) {
				prn.print(BinInst[exp.binop] + " $" + reg_name + ", $"
						+ exp.name + ", $" + exp.oprand2.name + "\n");
			} else {
				prn.print(BinInst[exp.binop] + " $" + reg_name + ", $"
						+ exp.name + ", " + exp.oprand2.value + "\n");
			}
			break;
		case SIMPLE:
			switch (exp.s_type) {
			case INT:
				prn.print("li $" + reg_name + ", " + exp.value + "\n");
				break;
			case LABEL:
				prn.print("la $" + reg_name + ", " + exp.name + "\n");
				break;
			case REG:
				prn.print("move $" + reg_name + ", $" + exp.name + "\n");
				break;
			default:
				break;
			
			}
			break;
		default:
			break;
		
		}
		return null;
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> SimpleExp()
	 */
	public KangaSym visit(PrintStmt n, KangaSym argu) {
		KangaSym sexp = n.f1.accept(this, argu);
		switch (sexp.s_type) {
		case INT:
			prn.print("li $a0, " + sexp.value + "\n");
			break;
		case LABEL:
			break;
		case REG:
			prn.print("move $a0, $" + sexp.name + "\n");
			break;
		default:
			break;
		
		}
		prn.print("jal _print\n");
		return null;
	}

	/**
	 * f0 -> "ALOAD"
	 * f1 -> Reg()
	 * f2 -> SpilledArg()
	 */
	public KangaSym visit(ALoadStmt n, KangaSym argu) {
		int spilledarg = Integer.parseInt(n.f2.f1.f0.tokenImage);
		prn.print("lw $" + Registers[n.f1.f0.which] + ", "
				+ (spilledarg+argoffset)*4 + "($sp)\n");
		return null;
	}

	/**
	 * f0 -> "ASTORE"
	 * f1 -> SpilledArg()
	 * f2 -> Reg()
	 */
	public KangaSym visit(AStoreStmt n, KangaSym argu) {
		int spilledarg = Integer.parseInt(n.f1.f1.f0.tokenImage);
		prn.print("sw $" + Registers[n.f2.f0.which] + ", "
				+ (spilledarg+argoffset)*4 + "($sp)\n");
		return null;
	}

	/**
	 * f0 -> "PASSARG"
	 * f1 -> IntegerLiteral()
	 * f2 -> Reg()
	 */
	public KangaSym visit(PassArgStmt n, KangaSym argu) {
		int pass = Integer.parseInt(n.f1.f0.tokenImage)-1;
		prn.print("sw $" + Registers[n.f2.f0.which] + ", "
				+ pass*4 + "($sp)\n");
		return null;
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 */
	public KangaSym visit(CallStmt n, KangaSym argu) {
		KangaSym sexp = n.f1.accept(this, argu);
		switch (sexp.s_type) {
		case INT:
			break;
		case LABEL:
			prn.print("jal " + sexp.name + "\n");
			break;
		case REG:
			prn.print("jalr $" + sexp.name + "\n");
			break;
		default:
			break;
		
		}
		return null;
	}

	/**
	 * f0 -> HAllocate()
	 *       | BinOp()
	 *       | SimpleExp()
	 */
	public KangaSym visit(Exp n, KangaSym argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> SimpleExp()
	 */
	public KangaSym visit(HAllocate n, KangaSym argu) {
		KangaSym sexp = n.f1.accept(this, argu);
		switch (sexp.s_type) {
		case INT:
			prn.print("li $a0, " + sexp.value + "\n"
					+ "jal _alloc\n");
			break;
		case LABEL:
			break;
		case REG:
			prn.print("move $a0, $" + sexp.name + "\n"
					+ "jal _alloc\n");
			break;
		default:
			break;
		
		}
		KangaSym ret = new KangaSym(KangaSym.Type.SIMPLE, 
				KangaSym.SimpleType.REG);
		ret.name = "v0";
		return ret;
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Reg()
	 * f2 -> SimpleExp()
	 */
	public KangaSym visit(BinOp n, KangaSym argu) {	
		KangaSym sym = new KangaSym(KangaSym.Type.BINOP);
		sym.binop = n.f0.f0.which;
		sym.name = Registers[n.f1.f0.which];
		sym.oprand2 = n.f2.accept(this, argu);
		return sym;
	}

	/**
	 * f0 -> "LT"
	 *       | "PLUS"
	 *       | "MINUS"
	 *       | "TIMES"
	 */
	public KangaSym visit(Operator n, KangaSym argu) {
		return null;
	}

	/**
	 * f0 -> "SPILLEDARG"
	 * f1 -> IntegerLiteral()
	 */
	public KangaSym visit(SpilledArg n, KangaSym argu) {
		return null;
	}

	/**
	 * f0 -> Reg()
	 *       | IntegerLiteral()
	 *       | Label()
	 */
	public KangaSym visit(SimpleExp n, KangaSym argu) {
		return n.f0.accept(this, null);
	}

	/**
	 * f0 -> "a0"
	 *       | "a1"
	 *       | "a2"
	 *       | "a3"
	 *       | "t0"
	 *       | "t1"
	 *       | "t2"
	 *       | "t3"
	 *       | "t4"
	 *       | "t5"
	 *       | "t6"
	 *       | "t7"
	 *       | "s0"
	 *       | "s1"
	 *       | "s2"
	 *       | "s3"
	 *       | "s4"
	 *       | "s5"
	 *       | "s6"
	 *       | "s7"
	 *       | "t8"
	 *       | "t9"
	 *       | "v0"
	 *       | "v1"
	 */

	public KangaSym visit(Reg n, KangaSym argu) {
		KangaSym sym = new KangaSym(KangaSym.Type.SIMPLE, 
				KangaSym.SimpleType.REG);
		sym.name = Registers[n.f0.which];
		return sym;
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public KangaSym visit(IntegerLiteral n, KangaSym argu) {
		KangaSym sym = new KangaSym(KangaSym.Type.SIMPLE,
				KangaSym.SimpleType.INT);
		sym.value = Integer.parseInt(n.f0.tokenImage);
		return sym;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public KangaSym visit(Label n, KangaSym argu) {
		if (argu!=null) {
			prn.print(n.f0.tokenImage+":\n");
		}
		KangaSym sym = new KangaSym(KangaSym.Type.SIMPLE,
				KangaSym.SimpleType.LABEL);
		sym.name = n.f0.tokenImage;
		return sym;
	}

}
