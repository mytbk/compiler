package spiglet.spiglet2kanga;

import spiglet.ParseException;
import spiglet.SpigletParser;
import spiglet.TokenMgrError;
import spiglet.syntaxtree.Node;
import spiglet.visitor.GJDepthFirst;
import spiglet.visitor.GenKangaVisitor;




public class Main { 
 
    public static void main(String[] args) {
    	try {
    		Node root = new SpigletParser(System.in).Goal();
    		/*
    		 * TODO: Implement your own Visitors and other classes.
    		 * 
    		 */
    		SpgGoal goal = new SpgGoal();
    		root.accept(new GenKangaVisitor(), goal);
    		
    		goal.preProcess();
    		goal.printGoal();
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