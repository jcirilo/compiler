package main;

import lexical.Scanner;
import parser.Parser;

public class Main {

	public static void main(String[] args) {
		test("/test/Test1.pas");
		test("/test/Test2.pas");
		test("/test/Test3.pas");
		test("/test/Test4.pas");
		test("/test/Test5.pas");
		test("/test/Test6.pas");
		test("/test/Test7.pas");
	}

	public static void test(String fileName) {
		try {
			String path = "%s/%s".formatted(System.getProperty("user.dir"), fileName);
			new Parser().parse(new Scanner(path).tokenizer());
			System.out.println(fileName + "\t\t" + "compilation sucessfully");
		} catch (RuntimeException e) {
			System.out.println(fileName + "\t\t" + e.getLocalizedMessage());
		}
	}

}
