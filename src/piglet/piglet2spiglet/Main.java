package piglet.piglet2spiglet;


import java.io.InputStream;

import aux.CCPrinter;
import piglet.ParseException;
import piglet.PigletParser;
import piglet.TokenMgrError;
import piglet.syntaxtree.Node;
import piglet.visitor.GJDepthFirst;
import piglet.visitor.GenSpigletVisitor;
import piglet.visitor.ScanTempVisitor;


public class Main {
	public static CCPrinter execute(InputStream stream) {
		try {
			new PigletParser(stream);
			Node root = PigletParser.Goal();
    		/*
    		 * TODO: Implement your own Visitors and other classes.
    		 * 
    		 */
			ScanTempVisitor vt = new ScanTempVisitor();
			root.accept(vt);
			GenSpigletVisitor gen = new GenSpigletVisitor();
    		gen.nTemp = Math.max(vt.maxtmp, 20);
    		root.accept(gen, null);
    		return gen.prn;
    	}
    	catch(TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	}
    	catch (ParseException e){
    		//Handle Grammar Errors
    		e.printStackTrace();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
		return null;   	
	}
	
    public static void main(String[] args) {
    	execute(System.in).printAll();
    }
}