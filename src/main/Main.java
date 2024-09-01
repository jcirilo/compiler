package main;

import java.util.ArrayList;

import lexical.Lexical;
import lexical.Token;
import parser.Parser;

public class Main {

	public static void main(String[] args) {
		Lexical lexicalAnalyzer = new Lexical();
		Parser parserAnalyzer = new Parser();
		ArrayList<Token> buffer;
		
		buffer = lexicalAnalyzer.tokenizer("source_code.mc");
		parserAnalyzer.setBuffer(buffer);
		parserAnalyzer.parse();

		System.out.println("Compilation Successful");

	}

}
