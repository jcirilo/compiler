package lexical;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import utils.TokenType;

public class Scanner {
	private int state;
	private char[] sourceBuffer;
	private int pos;
	private int row;
	private int col;
	private HashMap<String, TokenType> reservedWords;

	public Scanner(String source) {
		pos = 0;
		row = 0;
		col = 0;
		initializeReservedWords();
		try {
			String buffer = new String(Files.readAllBytes(Paths.get(source)));
			sourceBuffer = buffer.toCharArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeReservedWords() {
		reservedWords = new HashMap<>();
		reservedWords.put("int", TokenType.RESERVED);
		reservedWords.put("float", TokenType.RESERVED);
		reservedWords.put("print", TokenType.RESERVED);
		reservedWords.put("if", TokenType.RESERVED);
		reservedWords.put("else", TokenType.RESERVED);
	}

	public Token nextToken() {
		state = 0;
		char currentChar;
		String content = "";
		while (true) {
			if (isEOF()) {
				return null;
			}
			currentChar = nextChar();

			switch (state) {
				case 0:
					if (isSpace(currentChar)) {
						state = 0;
					} else if (Character.isLetter(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
						state = 1;
					} else if (Character.isDigit(currentChar)) {
						content += currentChar;
						state = 3;
					} else if (currentChar == '#') {
						state = 5;
					} else if (isOperator(currentChar)) {
						content += currentChar;
						state = 6;
					} else if (isAssignment(currentChar)) {
						content += currentChar;
						state = 7;
					} else if (isRelOperator(currentChar)) {
						content += currentChar;
						state = 8;
					} else if (isLeftParen(currentChar)) {
						content += currentChar;
						return new Token(TokenType.LEFT_PAREN, content);
					} else if (isRightParen(currentChar)) {
						content += currentChar;
						return new Token(TokenType.RIGHT_PAREN, content);
					} else {
						throw new RuntimeException("Invalid character: " + currentChar);
					}
					break;

				case 1:
					if (Character.isLetter(currentChar) || Character.isDigit(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
						state = 1;
					} else {
						state = 2;
					}
					break;

				case 2:
					back();
					if (reservedWords.containsKey(content)) {
						return new Token(reservedWords.get(content), content);
					} else {
						return new Token(TokenType.IDENTIFIER, content);
					}

				case 3:
					if (Character.isDigit(currentChar)) {
						content += currentChar;
						state = 3;
					} else if (isOperator(currentChar) || isSpace(currentChar)) {
						state = 4;
					} else {
						throw new RuntimeException("Malformed number: " + content + currentChar);
					}
					break;

				case 4:
					back();
					return new Token(TokenType.NUMBER, content);

				case 5:
					if (isEndOfLine(currentChar)) {
						state = 0;
					}
					break;

				case 6:
					return new Token(TokenType.MATH_OPERATOR, content);

				case 7:
					if (currentChar == '=') {
						content += currentChar;
						return new Token(TokenType.REL_OPERATOR, content);
					} else {
						back();
						return new Token(TokenType.ASSIGNMENT, content);
					}

				case 8:
					if ((content.equals(">") || content.equals("<") || content.equals("=") || content.equals("!")) && currentChar == '=') {
						content += currentChar;
					} else {
						back();
					}
					return new Token(TokenType.REL_OPERATOR, content);

				default:
					break;
			}
		}
		
	}

	private boolean isUnderscore (char c) {
		return c == '_';
	}

	private boolean isEndOfLine(char c) {
		return (c == '\n' || c == '\r');
	}

	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isSpace(char c) {
		return c == ' ' || c == '\n' || c == '\t' || c == '\r';
	}

	private boolean isOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}

	private boolean isAssignment(char c) {
		return c == '=';
	}

	private boolean isRelOperator(char c) {
		return c == '>' || c == '<' || c == '!' || c == '=';
	}

	private boolean isLeftParen(char c) {
		return c == '(';
	}

	private boolean isRightParen(char c) {
		return c == ')';
	}

	private char nextChar() {
		return sourceBuffer[pos++];
	}

	private void back() {
		pos--;
	}

	private boolean isEOF() {
		return pos >= sourceBuffer.length;
	}
}