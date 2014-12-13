import aux.CCPrinter;

public class Main {

	public static void main(String[] args) {
		CCPrinter prn_pg, prn_spg;
		prn_pg = minijava.minijava2piglet.Main.execute(System.in);
		prn_spg = piglet.piglet2spiglet.Main.execute(prn_pg.toInputStream());
		prn_spg.printAll();
	}
}
