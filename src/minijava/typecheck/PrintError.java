/**
 * 存放错误信息并统一打印
 */
package minijava.typecheck;

import java.util.Vector;

public class PrintError {
	private static Vector<String> errors = new Vector<String>();

	public static void print(int line, int column, String error_msg) {
		String msg = "Line " + line + " Column " + column + ": " + error_msg;
		errors.addElement(msg); // 存储错误信息
	}

	// 统一进行打印
	public static void printAll() {
		int sz = errors.size();
		for (int i = 0; i < sz; i++) {
			System.err.println(errors.elementAt(i));
		}
	}
	
	public static void outputResult() {
		if (errors.size()>0) {
			System.out.println("Type error");
		} else {
			System.out.println("Program type checked successfully");
		}
	}
}

