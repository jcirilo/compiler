package main;

import java.util.ArrayList;

import lexical.Lexical;
import lexical.Token;
import parser.Parser;

public class Main {

	public static void main(String[] args) {
		ArrayList<Token> buffer;
		Lexical lexicalAnalyzer = new Lexical();
		Parser parserAnalyzer = new Parser();
		String path = "exemplo.pas";
		
		try {
			buffer = lexicalAnalyzer.tokenizer(path);
			parserAnalyzer.parse(buffer);
		} catch (RuntimeException e ){
			System.out.println(e.getMessage());
		}
	}

}
