package minijava.minijava2piglet;

public class PigletLabel {
	static int nLabels = 0;
	public static String newLabel() {
		nLabels++;
		return "L"+nLabels;
	}
}
