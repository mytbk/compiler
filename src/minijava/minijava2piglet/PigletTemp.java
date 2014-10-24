package minijava.minijava2piglet;

public class PigletTemp {
	static int tmpid = 20;
	public static String newTmp() {
		tmpid++;
		return "TEMP "+tmpid;
	}
}
