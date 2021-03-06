/**
 * miniJava to Piglet visitor
 */
package minijava.visitor;

import java.util.Enumeration;

import aux.CCPrinter;
import minijava.minijava2piglet.PigletBinding;
import minijava.minijava2piglet.PigletLabel;
import minijava.minijava2piglet.PigletTemp;
import minijava.symboltable.*;
import minijava.syntaxtree.*;
import minijava.typecheck.PrintError;

public class GenPigletVisitor extends GJDepthFirst<MType, MType> {
	
	int nParams;
	int usedParams;
	String tmp;
	public CCPrinter prn;
	
	public GenPigletVisitor() {
		prn = new CCPrinter();
	}
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public MType visit(NodeList n, MType argu) {
      MType _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public MType visit(NodeListOptional n, MType argu) {
      if ( n.present() ) {
         MType _ret=null;
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
         }
         return _ret;
      }
      else
         return null;
   }

   public MType visit(NodeOptional n, MType argu) {
      if ( n.present() )
         return n.node.accept(this,argu);
      else
         return null;
   }

   public MType visit(NodeSequence n, MType argu) {
      MType _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public MType visit(NodeToken n, MType argu) { return null; }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
   public MType visit(Goal n, MType argu) {
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> PrintStatement()
    * f15 -> "}"
    * f16 -> "}"
    */
   public MType visit(MainClass n, MType argu) {
	   MClasses all_classes = (MClasses)argu;
	   	prn.println("MAIN");
	   	n.f14.accept(this, all_classes.main_class.findMethodByName("main"));
	   	prn.println("\nEND\n");
	   	return null;
   }

   /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public MType visit(TypeDeclaration n, MType argu) {
      n.f0.accept(this, argu);
      return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public MType visit(ClassDeclaration n, MType argu) {
		MClasses all_classes = (MClasses)argu;
		// 处理类定义的标识符Identifier()
		// 获得类名
		String class_name = ((MIdentifier) n.f1.accept(this, null)).getName();
		MClass the_class = all_classes.findClassByName(class_name);
		
		// 处理类方法
		n.f4.accept(this, the_class);
		return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   public MType visit(ClassExtendsDeclaration n, MType argu) {
		MClasses all_classes = (MClasses)argu;
		// 处理类定义的标识符Identifier()
		// 获得类名
		String class_name = ((MIdentifier) n.f1.accept(this, null)).getName();
		MClass the_class = all_classes.findClassByName(class_name);
		
		// 处理类方法
		n.f6.accept(this, the_class);
		return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public MType visit(VarDeclaration n, MType argu) {
	   return null;
   }

   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   public MType visit(MethodDeclaration n, MType argu) {
	   // 注意argu为方法所属的类
	   MClass m_class = (MClass)argu;
	   String method_name = n.f2.accept(this, null).getName();
	   MMethod the_method = m_class.findMethodByName(method_name);
	   int nParams = the_method.paramList.size();
	   
	   prn.println(m_class.getName() + "_" + method_name 
			   + " [ " + Math.min(20,(nParams+1)) +" ] ");
	   prn.println("BEGIN");
	   
	   // generate local variable bindings
	   the_method.genLocalVarBindings();
	   
      // 语句处理
      n.f8.accept(this, the_method);
      // 返回表达式
      prn.print("\nRETURN ");      
      n.f10.accept(this, the_method);
      
      prn.println("\nEND\n");
      return null;
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
   public MType visit(FormalParameterList n, MType argu) {
	   return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public MType visit(FormalParameter n, MType argu) {
	   return null;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public MType visit(FormalParameterRest n, MType argu) {
	   return null;
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public MType visit(Type n, MType argu) {
      return null;
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public MType visit(ArrayType n, MType argu) {
	   return null;
   }

   /**
    * f0 -> "boolean"
    */
   public MType visit(BooleanType n, MType argu) {
	   return null;
   }

   /**
    * f0 -> "int"
    */
   public MType visit(IntegerType n, MType argu) {
	   return null;
   }

   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
   public MType visit(Statement n, MType argu) {
	   n.f0.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public MType visit(Block n, MType argu) {
	   n.f1.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public MType visit(AssignmentStatement n, MType argu) {
	   // argu是MMethod类
	   MMethod m_method = (MMethod)argu;
	   String identifier = n.f0.accept(this, null).getName();
	   
	   // 判断Identifier是TEMP还是内存单元
	   PigletBinding binding = m_method.getBinding(identifier);
	   
	   if (binding.write==null) {
		   // Identifier为TEMP，则使用MOVE
		   prn.print("\nMOVE " + binding.read + " ");
		   n.f2.accept(this, argu);
	   } else {
		   // Identifier为内存单元，使用HSTORE
		   prn.print("\nHSTORE " + binding.write + " ");
		   n.f2.accept(this, argu);
	   }
	   
	   return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   public MType visit(ArrayAssignmentStatement n, MType argu) {
	   MMethod m_method = (MMethod)argu;
	   String t_base = PigletTemp.newTmp();
	   String t_idx = PigletTemp.newTmp();
	   // load t_base
	   String identifier = n.f0.accept(this, null).getName();
	   PigletBinding binding = m_method.getBinding(identifier);
	   prn.println("\nMOVE " + t_base + " " + binding.read);
	   // load index
	   prn.print("\nMOVE " + t_idx + " ");
	   n.f2.accept(this, argu);
	   // calculate address 
	   prn.print("\nMOVE " + t_idx + " TIMES 4 PLUS 1 " + t_idx);
	   prn.print("\nMOVE " + t_base + " PLUS " + t_base + " " + t_idx);
	   prn.print("\nHSTORE " + t_base + " 0 ");
	   n.f5.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
   public MType visit(IfStatement n, MType argu) {
	   prn.print("\nCJUMP ");
	   n.f2.accept(this, argu);
	   String lb_false = PigletLabel.newLabel();
	   String lb_end = PigletLabel.newLabel();
	   prn.println(" " + lb_false);
	   n.f4.accept(this, argu);
	   prn.println("\nJUMP " + lb_end);
	   prn.print(lb_false+" ");
	   n.f6.accept(this, argu);
	   prn.println("\n" + lb_end + " NOOP");
	   return null;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public MType visit(WhileStatement n, MType argu) {
	   String lb_start = PigletLabel.newLabel();
	   String lb_end = PigletLabel.newLabel();
	   prn.print("\n" + lb_start + " CJUMP ");
	   n.f2.accept(this, argu);
	   prn.println(" " + lb_end);
	   n.f4.accept(this, argu);
	   prn.println("\nJUMP " + lb_start);
	   prn.println(lb_end + " NOOP");
	   return null;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public MType visit(PrintStatement n, MType argu) {
	   prn.print("\nPRINT ");
	   n.f2.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
   public MType visit(Expression n, MType argu) {
	   return n.f0.accept(this, argu);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
   public MType visit(AndExpression n, MType argu) {
	   prn.println("\nBEGIN");
	   String tmp1 = PigletTemp.newTmp();
	   String tmp2 = PigletTemp.newTmp();
	   String lb = PigletLabel.newLabel();
	   prn.println("MOVE " + tmp2 + " 0");
	   prn.print("MOVE " + tmp1 + " ");
	   n.f0.accept(this, argu);
	   prn.println("\nCJUMP " + tmp1 + " " + lb );
	   prn.print("MOVE " + tmp2 + " ");
	   n.f2.accept(this, argu);
	   prn.println("\n" + lb + " NOOP");
	   prn.println("RETURN " + tmp2);
	   prn.println("END");
	   return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public MType visit(CompareExpression n, MType argu) {
	   prn.print("LT ");
	   n.f0.accept(this, argu);
	   prn.print(" ");
	   n.f2.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public MType visit(PlusExpression n, MType argu) {
	   prn.print("PLUS ");
	   n.f0.accept(this, argu);
	   prn.print(" ");
	   n.f2.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public MType visit(MinusExpression n, MType argu) {
	   prn.print("MINUS ");
	   n.f0.accept(this, argu);
	   prn.print(" ");
	   n.f2.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public MType visit(TimesExpression n, MType argu) {
	   prn.print("TIMES ");
	   n.f0.accept(this, argu);
	   prn.print(" ");
	   n.f2.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public MType visit(ArrayLookup n, MType argu) {
	   prn.println("\nBEGIN");
	   String tmp_arr = PigletTemp.newTmp();
	   String tmp_idx = PigletTemp.newTmp();
	   String tmp_ret = PigletTemp.newTmp();
	   prn.print("MOVE " + tmp_arr + " ");
	   n.f0.accept(this, argu);
	   prn.print("\nMOVE " + tmp_idx + " ");
	   n.f2.accept(this, argu);
	   prn.println("\nMOVE " + tmp_arr + " PLUS " + tmp_arr + " TIMES " + tmp_idx + " 4");
	   prn.println("HLOAD " + tmp_ret + " " + tmp_arr + " 4");
	   prn.println("RETURN " + tmp_ret);
	   prn.println("END");
	   return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public MType visit(ArrayLength n, MType argu) {
	   prn.println("\nBEGIN");
	   String tmp_arr = PigletTemp.newTmp();
	   prn.print("HLOAD " + tmp_arr + " ");
	   n.f0.accept(this, argu);
	   prn.println(" 0");
	   prn.println("RETURN " + tmp_arr);
	   prn.println("END");
	   return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public MType visit(MessageSend n, MType argu) {
	   prn.println("\nBEGIN");
	   String t_exp = PigletTemp.newTmp();
	   String t_method = PigletTemp.newTmp();
	   prn.print("MOVE " + t_exp + " ");
	   MClass exp_class = (MClass)(n.f0.accept(this, argu));
	   String m_name = n.f2.accept(this, null).getName();
	   int m_offs = exp_class.getMethodBinding(m_name);
	   nParams = exp_class.r_findMethodByName(m_name).paramList.size();
	   prn.println("\nHLOAD " + t_method + " " + t_exp + " 0");
	   prn.println("HLOAD " + t_method + " " + t_method + " " + m_offs);
	   prn.println("RETURN");
	   prn.print("CALL " + t_method + " ( " + t_exp + " ");
	   // arguments in ExpressionList
	   n.f4.accept(this, argu);
	   if (nParams>=20) {
		   prn.println("\nRETURN " + tmp);
		   prn.println("END");
	   }
	   prn.println(" ) ");
	   prn.println("END");
	   MMethod m = exp_class.r_findMethodByName(m_name);
	   String ret_type_name = m.ret_type_name;
	   MClass ret_class = exp_class.all_classes.findClassByName(ret_type_name);
	   return ret_class;
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public MType visit(ExpressionList n, MType argu) {
	   n.f0.accept(this, argu);
	   usedParams = 1;
	   n.f1.accept(this, argu);
	   return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public MType visit(ExpressionRest n, MType argu) {
	   usedParams++;
	   prn.print(" ");
	   if (nParams>=20 && usedParams==19) {
		   // allocate the memory
		   tmp = PigletTemp.newTmp();
		   prn.println("\nBEGIN");
		   prn.println("MOVE " + tmp + " HALLOCATE " + (nParams-18)*4);
		   // store the first param
		   prn.print("HSTORE " + tmp + " 0 ");
		   n.f1.accept(this, argu);
	   } else if (nParams>=20 && usedParams>=20) {
		   prn.print("\nHSTORE " + tmp + " " + (usedParams-19)*4 + " ");
		   n.f1.accept(this, argu);
	   } else {
		   n.f1.accept(this, argu);
	   }
	   
	   return null;
   }

   /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
   public MType visit(PrimaryExpression n, MType argu) {
	   return n.f0.accept(this, argu);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public MType visit(IntegerLiteral n, MType argu) {
	   int val = Integer.parseInt(n.f0.tokenImage);
	   prn.print(val+" ");
	   return null;
   }

   /**
    * f0 -> "true"
    */
   public MType visit(TrueLiteral n, MType argu) {
	   prn.print(1+" ");
	   return null;
   }

   /**
    * f0 -> "false"
    */
   public MType visit(FalseLiteral n, MType argu) {
	   prn.print(0+" ");
	   return null;
  }

   /**
    * f0 -> <IDENTIFIER>
    */
   public MType visit(Identifier n, MType argu) {
     String identifier_name = n.f0.toString();
     MIdentifier _ret = new MIdentifier(identifier_name, n.f0.beginLine, n.f0.beginColumn);
     if (argu==null) {
    	 // the identifier is in a statement should return identifier
    	 return _ret;
     } else {
    	 // argu is some expression in method, should return a class and print the code
    	 MVariable vv = ((MMethod)argu).findVarByName(identifier_name);
    	 MClass the_class = ((MMethod)argu).method_class.all_classes.findClassByName(vv.typename);
    	 prn.print( ((MMethod)argu).getBinding(identifier_name).read );
    	 return the_class;
     }
   }

   /**
    * f0 -> "this"
    */
   public MType visit(ThisExpression n, MType argu) {
	   prn.print("TEMP 0 ");
      return ((MMethod)argu).method_class;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public MType visit(ArrayAllocationExpression n, MType argu) {
	   String tmp_len = PigletTemp.newTmp();
	   String tmp_arr = PigletTemp.newTmp();
	   prn.println("\nBEGIN");
	   prn.print("MOVE " + tmp_len + " ");
	   n.f3.accept(this, argu);
	   // the first element of the array stores the length
	   // the rest store the elements
	   prn.println("\nMOVE " + tmp_arr + " HALLOCATE TIMES 4 PLUS 1 " + tmp_len);
	   prn.println("HSTORE " + tmp_arr + " 0 " + tmp_len);
	   prn.println("RETURN " + tmp_arr);
	   prn.println("END");
	   return null;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public MType visit(AllocationExpression n, MType argu) {
	   MClasses all_classes = ((MMethod)argu).method_class.all_classes;
	   MClass new_class = all_classes.findClassByName(n.f1.accept(this, null).getName());
	   prn.println(new_class.newString());
	   return new_class;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public MType visit(NotExpression n, MType argu) {
	   String false_label = PigletLabel.newLabel();
	   String tmp = PigletTemp.newTmp();
	   String result = PigletTemp.newTmp();
	   
	   prn.println("\nBEGIN");
	   prn.println("MOVE " + result + " 1");
	   prn.print("MOVE " + tmp + " ");
	   n.f1.accept(this, argu);
	   prn.println("\nCJUMP LT 0 " + tmp + " " + false_label);
	   prn.println("MOVE " + result + " 0");
	   prn.println(false_label + " NOOP");
		prn.println("RETURN " + result);
	   prn.println("END");
	   return null;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public MType visit(BracketExpression n, MType argu) {
	   return n.f1.accept(this, argu);
   }

}