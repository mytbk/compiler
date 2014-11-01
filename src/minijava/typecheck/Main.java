/**
 * 用于类型检查的主函数入口
 */
package minijava.typecheck;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.symboltable.MClasses;
import minijava.symboltable.MType;
import minijava.syntaxtree.Node;
import minijava.visitor.BuildSymbolTableVisitor;

public class Main {
	
	public static void main(String[] args) {

		try {
			new MiniJavaParser(System.in);
			Node root = MiniJavaParser.Goal();

			// 初始化符号表中最大的类
			MClasses my_classes = new MClasses();

			// 遍历抽象语法树，建立符号表，检查是否重复定义
			root.accept(new BuildSymbolTableVisitor(), my_classes);
			
			// 输出符号表信息
			if (args.length>0 && args[0].equals("--debug")){
				my_classes.printClasses(0);
			}
			
			// 建立类的继承关系，寻找循环继承
			my_classes.buildClassRelation();
			
			// 检查类型名有效性
			my_classes.checkAllVars();
			
			// 检查方法错误覆盖
			my_classes.checkAllFuncs();
			
			my_classes.checkAllStatements();
			
			// 打印错误信息
			PrintError.printAll();
			
			// PrintError.outputResult();
		} catch (TokenMgrError e) {

			// Handle Lexical Errors
			e.printStackTrace();
		} catch (ParseException e) {

			// Handle Grammar Errors
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}