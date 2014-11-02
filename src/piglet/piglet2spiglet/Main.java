package piglet.piglet2spiglet;


import piglet.ParseException;
import piglet.PigletParser;
import piglet.TokenMgrError;
import piglet.syntaxtree.Node;
import piglet.visitor.GJDepthFirst;
import piglet.visitor.GenSpigletVisitor;
import piglet.visitor.ScanTempVisitor;


public class Main { 
 
    public static void main(String[] args) {
    	try {
    		Node root = new PigletParser(System.in).Goal();
    		/*
    		 * TODO: Implement your own Visitors and other classes.
    		 * 
    		 */
    		ScanTempVisitor vt = new ScanTempVisitor();
    		root.accept(vt);
    		GenSpigletVisitor gen = new GenSpigletVisitor();
    		gen.nTemp = Math.max(vt.maxtmp, 20);
    		root.accept(gen, null);
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
    	
    }
}