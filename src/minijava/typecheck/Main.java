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
			MType my_classes = new MClasses();

			// 遍历抽象语法树，建立符号表，检查是否重复定义
			root.accept(new BuildSymbolTableVisitor(), my_classes);

			// 打印错误信息
			PrintError.printAll();
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