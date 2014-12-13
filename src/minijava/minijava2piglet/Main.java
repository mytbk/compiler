package minijava.minijava2piglet;

import java.io.InputStream;

import aux.CCPrinter;
import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.symboltable.MClasses;
import minijava.syntaxtree.Node;
import minijava.visitor.BuildSymbolTableVisitor;
import minijava.visitor.GenPigletVisitor;

public class Main { 
	public static CCPrinter execute(InputStream stream) {
		try {
			new MiniJavaParser(stream);
			Node root = MiniJavaParser.Goal();

			// 初始化符号表中最大的类
			MClasses my_classes = new MClasses();

			// 遍历抽象语法树，建立符号表，检查是否重复定义
			root.accept(new BuildSymbolTableVisitor(), my_classes);
			
			// 建立类的继承关系，寻找循环继承
			my_classes.buildClassRelation();
			
			// 对每个类，建立其可用方法列表
			for (int i=0; i<my_classes.mj_classes.size(); i++) {
				my_classes.mj_classes.elementAt(i).buildMethodRef();
			}
			
    		/*
    		 * TODO: Implement your own Visitors and other classes.
    		 * 
    		 */
    		GenPigletVisitor v = new GenPigletVisitor();
    		root.accept(v, my_classes);
    		return v.prn;
		} catch(TokenMgrError e){
			//Handle Lexical Errors
			e.printStackTrace();
		} catch (ParseException e){
			//Handle Grammar Errors
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		execute(System.in).printAll();
	}
    
}