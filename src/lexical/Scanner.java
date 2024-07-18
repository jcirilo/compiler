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
		row = 1;
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

			if (isInvalidCharacter(currentChar)) {
				error("Invalid character: " + content + currentChar);	
			}

			switch (state) {
				case 0:
					if (isSpace(currentChar)) {
						state = 0;
					} else if (isLetter(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
						state = 1;
					} else if (isDigit(currentChar)) {
						content += currentChar;
						state = 2;
					} else if (isHashtag(currentChar)) {
						state = 3;
					} else if (isOperator(currentChar)) {
						content += currentChar;
						return new Token(TokenType.MATH_OPERATOR, content);
					} else if (isAssignment(currentChar)) {
						content += currentChar;
						state = 4;
					} else if (isRelOperator(currentChar)) {
						content += currentChar;
						state = 5;
					} else if (isLeftParen(currentChar)) {
						content += currentChar;
						return new Token(TokenType.LEFT_PAREN, content);
					} else if (isRightParen(currentChar)) {
						content += currentChar;
						return new Token(TokenType.RIGHT_PAREN, content);
					} else {
						error("Unexpected character: " + currentChar);
					}
					break;

				case 1:
					if (isLetter(currentChar) || isDigit(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
						state = 1;
					} else if (reservedWords.containsKey(content) && isSpace(currentChar)) {
						back();
						return new Token(reservedWords.get(content), content);
					} else {
						back();
						return new Token(TokenType.IDENTIFIER, content);
					}
					break;

				case 2:
					if (isDigit(currentChar)) {
						content += currentChar;
						state = 2;
					} else if (isPoint(currentChar)) {
						content += currentChar;
						state = 6;
					} else if (isLetter(currentChar)) {
						error("Malformed number: " + content + currentChar);
					}else {
						back();
						return new Token(TokenType.NUMBER, content);
					}
					break;

				case 3:
					while (!isEndOfLine(currentChar)) {
						currentChar = nextChar();
					}
					state = 0;
					break;

				case 4:
					if (isAssignment(currentChar)) {
						content += currentChar;
						return new Token(TokenType.REL_OPERATOR, content);
					} else {
						back();
						return new Token(TokenType.ASSIGNMENT, content);
					}

				case 5:
					if (isAssignment(currentChar)) {
						content += currentChar;
						return new Token(TokenType.REL_OPERATOR, content);
					} else {
						back();
						return new Token(TokenType.REL_OPERATOR, content);
					}

				case 6:
					if (isDigit(currentChar)) {
						content += currentChar;
						state = 7;
					} else {
						error("Malformed number: " + content + currentChar);
					}
					break;

				case 7:
					if (isDigit(currentChar)) {
						content += currentChar;
						state = 7;
					} else if (isLetter(currentChar)) {
						error("Malformed number: " + content + currentChar);
					} else {
						back();
						return new Token(TokenType.NUMBER, content);
					}
					break;

				default:
					break;
			}
		}
	}

	private boolean isPoint(char c) {
		return c == '.';
	}

	private boolean isUnderscore (char c) {
		return c == '_';
	}

	private boolean isHashtag (char c) {
		return c == '#';
	}

	private boolean isEndOfLine(char c) {
		return (c == '\n') || (c == '\r');
	}

	private boolean isLetter(char c) {
		return Character.isLetter(c);
	}

	private boolean isDigit(char c) {
		return Character.isDigit(c);
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
		return c == '>' || c == '<' || c == '!';
	}

	private boolean isLeftParen(char c) {
		return c == '(';
	}

	private boolean isRightParen(char c) {
		return c == ')';
	}

	private boolean isInvalidCharacter(char c){
		return c == 'ç' || c == '@' || c == '`' || c == '´' || c == '~' || c == '¨' ||
		c == 'á' ||c == 'à' || c == 'ã' || c == 'õ' || 
		c == 'â' || c == 'ê' || c == 'ô';

	}

	private char nextChar() {
		char currentChar = sourceBuffer[pos++];
		if(currentChar == '\n'){
			row++;
			col = 0;
		}else{
			col++;
		}
		return currentChar;
	}

	private void back() {
		pos--;
		if(pos >= 0){
			char currentChar = sourceBuffer[pos];

			if(currentChar == '\n'){
				row--;
				col = 1;
				for(int i = pos - 1; i >= 0 && sourceBuffer[i] != '\n'; i--){
					col++;
				}
			} else{
				col--;
			}
		}
	}

	private void error(String message){
		throw new RuntimeException("Error on line " + row + " and column " + col + " - " + message);	
	}

	private boolean isEOF() {
		return pos >= sourceBuffer.length;
	}
}