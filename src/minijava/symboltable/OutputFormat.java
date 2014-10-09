package minijava.symboltable;

public class OutputFormat {
	public static String spaces(int n) {
		String s = "";
		while (n>0) {
			s+=" ";
			n--;
		}
		return s;
	}
}
