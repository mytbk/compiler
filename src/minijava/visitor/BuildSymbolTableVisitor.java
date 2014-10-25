/**
 * 用于建立符号表的类
 */
package minijava.visitor;

import java.util.Enumeration;

import minijava.symboltable.*;
import minijava.syntaxtree.*;
import minijava.typecheck.PrintError;

public class BuildSymbolTableVisitor extends GJDepthFirst<MType, MType> {
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
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
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
      MType _ret=null;
      MClass m_class;
      String class_name;
      String error_msg;

      // 处理类定义的标识符Identifier()
      // 获得名字
      class_name = ((MIdentifier) n.f1.accept(this, null)).getName();

      // 在符号表中插入该类，如果出错，打印出错信息
      m_class = new MClass(class_name, (MClasses) argu, n.f1.f0.beginLine,
    		  n.f1.f0.beginColumn);
      error_msg = ((MClasses) argu).InsertClass(m_class);
      if (error_msg != null)
    	  PrintError.print(m_class.getLine(), m_class.getColumn(), error_msg);
      
      // 设置该类为主类
      ((MClasses)argu).main_class = m_class;
     
      // 往main class中添加main方法
      MMethod main_method = new MMethod("main", MIdentifier.voidType,
    		  n.f6.beginLine, n.f6.beginColumn);
      //main_method.ret_type_name = MIdentifier.voidType;
      MIdentifier argid = (MIdentifier) n.f11.accept(this, null);
      MVariable param = new MVariable(argid.getName(), MIdentifier.sArrayType, 
    		  argid.getLine(), argid.getColumn());
      main_method.addParam(param);
      m_class.insertMethod(main_method);
      
      // add printStatement to main method
      n.f14.accept(this, main_method.statements);
      
      return _ret;
   }

   /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public MType visit(TypeDeclaration n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      return _ret;
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
		MType _ret = null;
		MClass m_class;
		String class_name;
		String error_msg;

		// 处理类定义的标识符Identifier()
		// 获得名字
		class_name = ((MIdentifier) n.f1.accept(this, null)).getName();

		// 在符号表中插入该类，如果出错，打印出错信息
		m_class = new MClass(class_name, (MClasses) argu, n.f1.f0.beginLine,
				n.f1.f0.beginColumn);
		error_msg = ((MClasses) argu).InsertClass(m_class);
		if (error_msg != null)
			PrintError.print(m_class.getLine(), m_class.getColumn(), error_msg);
		
		// 处理类变量声明
		n.f3.accept(this, m_class);
		
		// 处理类方法
		n.f4.accept(this, m_class);
		return _ret;
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
      MType _ret=null;
      MClass m_class;
      String class_name;
      String error_msg;      
     
      // 处理子类定义，并加入符号表
      class_name = ((MIdentifier) n.f1.accept(this, null)).getName();
      m_class = new MClass(class_name, (MClasses) argu, n.f1.f0.beginLine,
				n.f1.f0.beginColumn);
		error_msg = ((MClasses) argu).InsertClass(m_class);
		if (error_msg != null)
			PrintError.print(m_class.getLine(), m_class.getColumn(), error_msg);
     
      // 将父类名称加入类属性
      m_class.extend_class_name = ((MIdentifier) n.f3.accept(this, null)).getName();
      
      // 处理类变量声明
      n.f5.accept(this, m_class);
      
      // 处理类方法
      n.f6.accept(this, m_class);
      
      return _ret;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public MType visit(VarDeclaration n, MType argu) {
      MType _ret=null;
      MLocalVarType m_class = (MLocalVarType)argu; // m_class可能是类或方法
      String var_name = n.f1.accept(this, null).getName();
      String type_name = n.f0.accept(this, null).getName();
      // debug
      // System.out.println("Variable: \'" + var_name + "\' from class/method " + m_class.getName());
      // System.out.println("It has type: " + type_name);
      
      // 建立并插入变量
      MVariable var = new MVariable(var_name, type_name, n.f1.f0.beginLine, n.f1.f0.beginColumn);
      String _err = m_class.insertVar(var);
      if (_err!=null) {
    	  // 重复定义，则输出错误信息
    	  PrintError.print(var.getLine(), var.getColumn(), _err);
      }
      
     return _ret;
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
	   MType _ret = null;
	   MClass m_class = (MClass)argu;
	   String ret_type = n.f1.accept(this, null).getName();
	   String method_name = n.f2.accept(this, null).getName();
	   MMethod m_method = new MMethod(method_name, ret_type, 
			   n.f2.f0.beginLine, n.f2.f0.beginColumn);
	   
	   // 将定义的方法插入类中，如果出错则打印错误信息
	   String _err = m_class.insertMethod(m_method);
	   if (_err!=null) {
		   PrintError.print(n.f2.f0.beginLine, n.f2.f0.beginColumn, _err);
	   }
	   
	   // 处理参数表
      n.f4.accept(this, m_method);
      
      // 函数局部变量处理      
      n.f7.accept(this, m_method);
      
      // 语句处理
      n.f8.accept(this, m_method.statements);
      
      // 返回表达式处理
      m_method.ret_expr = (MExpression) n.f10.accept(this, null);

      return _ret;
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
   public MType visit(FormalParameterList n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public MType visit(FormalParameter n, MType argu) {
      MType _ret=null;
      MMethod m_method = (MMethod)argu;
      String p_type = n.f0.accept(this, null).getName();
      String p_name = n.f1.accept(this, null).getName();
      MVariable param = new MVariable(p_name, p_type, n.f1.f0.beginLine, n.f1.f0.beginColumn);
      // 插入方法的参数表
      String _err = m_method.addParam(param);
      if (_err!=null) {
    	  PrintError.print(n.f1.f0.beginLine, n.f1.f0.beginColumn, _err);
      }
      return _ret;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public MType visit(FormalParameterRest n, MType argu) {
      MType _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public MType visit(Type n, MType argu) {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public MType visit(ArrayType n, MType argu) {
      MType _ret = new MIdentifier(MIdentifier.arrType, n.f0.beginLine, n.f0.beginColumn);
      return _ret;
   }

   /**
    * f0 -> "boolean"
    */
   public MType visit(BooleanType n, MType argu) {
      MType _ret = new MIdentifier(MIdentifier.boolType, n.f0.beginLine, n.f0.beginColumn);
      return _ret;
   }

   /**
    * f0 -> "int"
    */
   public MType visit(IntegerType n, MType argu) {
      MType _ret = new MIdentifier(MIdentifier.intType, n.f0.beginLine, n.f0.beginColumn);
      return _ret;
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
	   return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public MType visit(Block n, MType argu) {
      MStatement s = new MStatement(n.f0.beginLine, n.f0.beginColumn,
    		  MStatement.Keyword.Block);
      s.s_list = new MStatementList();
      if (argu!=null) { // should be a statement list
    	  ((MStatementList)argu).addStatement(s);
      }
      n.f1.accept(this, s.s_list);
      return s;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public MType visit(AssignmentStatement n, MType argu) {
      MStatementList m_list = (MStatementList) argu;
      MIdentifier st_id = (MIdentifier) n.f0.accept(this, null);
      MStatement s = new MStatement(st_id.getLine(), st_id.getColumn(),
    		  MStatement.Keyword.Assign);
      s.s_id = st_id;
      s.e_first = (MExpression) n.f2.accept(this, null);
      if (m_list!=null) { // a statement list
    	  m_list.addStatement(s);
      }
      return s;
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
      MStatementList m_list = (MStatementList) argu;
      MIdentifier st_id = (MIdentifier) n.f0.accept(this, null);
      MStatement s = new MStatement(st_id.getLine(), st_id.getColumn(),
    		  MStatement.Keyword.ArrAssign);
      s.s_id = st_id;
      s.e_first = (MExpression) n.f2.accept(this, null);
      s.e_second = (MExpression) n.f5.accept(this, null);
      if (m_list!=null) { // a statement list
    	  m_list.addStatement(s);
      }
      return s;
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
      MStatementList m_list = (MStatementList) argu;
      MStatement s = new MStatement(n.f0.beginLine, n.f0.beginColumn,
    		  MStatement.Keyword.If);
      s.e_first = (MExpression) n.f2.accept(this, null);
      s.s_first = (MStatement) n.f4.accept(this, null);
      s.s_second = (MStatement) n.f6.accept(this, null);
      if (m_list!=null)	{ // a statement list
    	  m_list.addStatement(s);
      }
      return s;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public MType visit(WhileStatement n, MType argu) {
	   MStatementList m_list = (MStatementList)argu;
	   MStatement s = new MStatement(n.f0.beginLine, n.f0.beginColumn,
			   MStatement.Keyword.While);
	   s.e_first = (MExpression) n.f2.accept(this, null);
	   s.s_first = (MStatement) n.f4.accept(this, null);
	   if (m_list!=null) {
		   m_list.addStatement(s);
	   }
	   return s;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public MType visit(PrintStatement n, MType argu) {
	   MStatementList m_list = (MStatementList)argu;
	   MStatement s = new MStatement(n.f0.beginLine, n.f0.beginColumn,
			   MStatement.Keyword.Print);
	   s.e_first = (MExpression) n.f2.accept(this, null);
	   if (m_list!=null) {
		   m_list.addStatement(s);
	   }
	   return s;
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
	   MExpression e = new MExpression(n.f1.beginLine, n.f1.beginColumn,
			   MExpression.Operator.And);
	   e.first = (MExpression) n.f0.accept(this, null);
	   e.second = (MExpression) n.f2.accept(this, null);
	   if (argu!=null) {
		   ((MExpressionList)argu).add_expr(e);
	   }
	   return e;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public MType visit(CompareExpression n, MType argu) {
	   MExpression e = new MExpression(n.f1.beginLine, n.f1.beginColumn,
			   MExpression.Operator.Smaller);
	   e.first = (MExpression) n.f0.accept(this, null);
	   e.second = (MExpression) n.f2.accept(this, null);
	   if (argu!=null) {
		   ((MExpressionList)argu).add_expr(e);
	   }
	   return e;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public MType visit(PlusExpression n, MType argu) {
	   MExpression e = new MExpression(n.f1.beginLine, n.f1.beginColumn,
			   MExpression.Operator.Plus);
	   e.first = (MExpression) n.f0.accept(this, null);
	   e.second = (MExpression) n.f2.accept(this, null);
	   if (argu!=null) {
		   ((MExpressionList)argu).add_expr(e);
	   }
	   return e;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public MType visit(MinusExpression n, MType argu) {
	   MExpression e = new MExpression(n.f1.beginLine, n.f1.beginColumn,
			   MExpression.Operator.Minus);
	   e.first = (MExpression) n.f0.accept(this, null);
	   e.second = (MExpression) n.f2.accept(this, null);
	   if (argu!=null) {
		   ((MExpressionList)argu).add_expr(e);
	   }
	   return e;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public MType visit(TimesExpression n, MType argu) {
	   MExpression e = new MExpression(n.f1.beginLine, n.f1.beginColumn,
			   MExpression.Operator.Times);
	   e.first = (MExpression) n.f0.accept(this, null);
	   e.second = (MExpression) n.f2.accept(this, null);
	   if (argu!=null) {
		   ((MExpressionList)argu).add_expr(e);
	   }
	   return e;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public MType visit(ArrayLookup n, MType argu) {
	   MExpression e = new MExpression(n.f1.beginLine, n.f1.beginColumn,
			   MExpression.Operator.ArrayLookup);
	   e.first = (MExpression) n.f0.accept(this, null);
	   e.second = (MExpression) n.f2.accept(this, null);
	   if (argu!=null) {
		   ((MExpressionList)argu).add_expr(e);
	   }
	   return e;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public MType visit(ArrayLength n, MType argu) {
	   MExpression e = new MExpression(n.f1.beginLine, n.f1.beginColumn,
			   MExpression.Operator.ArrayLen);
	   e.first = (MExpression) n.f0.accept(this, null);
	   if (argu!=null) {
		   ((MExpressionList)argu).add_expr(e);
	   }
	   return e;
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
	   MExpression e = new MExpression(n.f1.beginLine, n.f1.beginColumn,
			   MExpression.Operator.MsgSend);
	   e.first = (MExpression) n.f0.accept(this, null);
	   e.e_id = (MIdentifier) n.f2.accept(this, null);
	   // 将表达式列表的内容添加至e.e_list
	   n.f4.accept(this, e.e_list);
	   return e;
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public MType visit(ExpressionList n, MType argu) {
      if (argu!=null) {
    	  MExpressionList m_elist = (MExpressionList)argu;
    	  m_elist.add_expr((MExpression)n.f0.accept(this,null));
    	  n.f1.accept(this, m_elist);
      }
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public MType visit(ExpressionRest n, MType argu) {
	   if (argu!=null) {
		   MExpressionList m_elist = (MExpressionList)argu;
		   m_elist.add_expr((MExpression)n.f1.accept(this,null));
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
	   // FIXME: cannot get line and column
	   MExpression e = new MExpression(0, 0, MExpression.Operator.Primary);
	   n.f0.accept(this, e.e_exp);
	   if (argu!=null) { // a expression list
		   ((MExpressionList)argu).add_expr(e);
	   }
	   return e;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public MType visit(IntegerLiteral n, MType argu) {
	   int val = Integer.parseInt(n.f0.tokenImage);
	   if (argu!=null) { // should be primary expression type
		   MPrimaryExpr e = (MPrimaryExpr)argu;
		   e.e_type = MPrimaryExpr.E_type.Int;
		   e.i_val = val;
	   }
	   return null;
   }

   /**
    * f0 -> "true"
    */
   public MType visit(TrueLiteral n, MType argu) {
	   if (argu!=null) { // should be primary expression type
		   MPrimaryExpr e = (MPrimaryExpr)argu;
		   e.e_type = MPrimaryExpr.E_type.True;
	   }
	   return null;
   }

   /**
    * f0 -> "false"
    */
   public MType visit(FalseLiteral n, MType argu) {
	   if (argu!=null) { // should be primary expression type
		   MPrimaryExpr e = (MPrimaryExpr)argu;
		   e.e_type = MPrimaryExpr.E_type.False;
	   }
	   return null;
  }

   /**
    * f0 -> <IDENTIFIER>
    */
   public MType visit(Identifier n, MType argu) {
     String identifier_name = n.f0.toString();
     MIdentifier _ret = new MIdentifier(identifier_name, n.f0.beginLine, n.f0.beginColumn);
     if (argu!=null) { // should be primary expression type
    	 MPrimaryExpr e = (MPrimaryExpr)argu;
    	 e.e_type = MPrimaryExpr.E_type.Id;
    	 e.e_id = _ret;
     }
     return _ret;
   }

   /**
    * f0 -> "this"
    */
   public MType visit(ThisExpression n, MType argu) {
	   if (argu!=null) { // should be primary expression
		   MPrimaryExpr e = (MPrimaryExpr)argu;
		   e.e_type = MPrimaryExpr.E_type.This;
	   }
      return null;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public MType visit(ArrayAllocationExpression n, MType argu) {
	   if (argu!=null) { // should be primary expression
		   MPrimaryExpr e = (MPrimaryExpr)argu;
		   e.e_type = MPrimaryExpr.E_type.ArrayAlloc;
		   e.e_exp = (MExpression) n.f3.accept(this, null);
	   }
	   return null;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public MType visit(AllocationExpression n, MType argu) {
	   if (argu!=null) { // should be primary expression
		   MPrimaryExpr e = (MPrimaryExpr)argu;
		   e.e_type = MPrimaryExpr.E_type.Alloc;
		   e.e_id = (MIdentifier) n.f1.accept(this, null);
	   }
	   return null;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public MType visit(NotExpression n, MType argu) {
	   if (argu!=null) { // should be primary expression
		   MPrimaryExpr e = (MPrimaryExpr)argu;
		   e.e_type = MPrimaryExpr.E_type.Not;
		   e.e_exp = (MExpression) n.f1.accept(this, null);
	   }
	   return null;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public MType visit(BracketExpression n, MType argu) {
	   if (argu!=null) { // should be primary expression
		   MPrimaryExpr e = (MPrimaryExpr)argu;
		   e.e_type = MPrimaryExpr.E_type.Braket;
		   e.e_exp = (MExpression) n.f1.accept(this, null);
	   }
	   return null;
   }

}