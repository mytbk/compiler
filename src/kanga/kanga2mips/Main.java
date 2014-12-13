package kanga.kanga2mips;

import kanga.KangaParser;
import kanga.ParseException;
import kanga.TokenMgrError;
import kanga.syntaxtree.Node;
import kanga.visitor.GJDepthFirst;
import kanga.visitor.GenMIPSVisitor;

public class Main {

	public static void main(String[] args) {
		try {
			Node root = new KangaParser(System.in).Goal();
			/*
			 * TODO: Implement your own Visitors and other classes.
			 */
			GenMIPSVisitor v = new GenMIPSVisitor();
			// Traverse the Abstract Grammar Tree
			root.accept(v, null);
			v.prn.printAll();
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