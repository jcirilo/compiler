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
			String buffer = new String(Files.readAllBytes(Paths.get(source))).concat("\n");
			sourceBuffer = buffer.toCharArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeReservedWords() {
		reservedWords = new HashMap<>();
		reservedWords.put("integer", TokenType.RESERVED);
		reservedWords.put("real", TokenType.RESERVED);
		reservedWords.put("program", TokenType.RESERVED);
		reservedWords.put("var", TokenType.RESERVED);
		reservedWords.put("boolean", TokenType.RESERVED);
		reservedWords.put("procedure", TokenType.RESERVED);
        reservedWords.put("begin", TokenType.RESERVED);
        reservedWords.put("end", TokenType.RESERVED);
        reservedWords.put("if", TokenType.RESERVED);
        reservedWords.put("then", TokenType.RESERVED);
        reservedWords.put("else", TokenType.RESERVED);
        reservedWords.put("while", TokenType.RESERVED);
        reservedWords.put("do", TokenType.RESERVED);
        reservedWords.put("not", TokenType.RESERVED);
		reservedWords.put("or", TokenType.ADD_OPERATOR);
		reservedWords.put("and", TokenType.MULT_OPERATOR);
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
					} else if (isDot(currentChar)) { 
						content+= currentChar;
						return new Token(TokenType.DELIMITER, content);
					} else if (isHashtag(currentChar)) {
						state = 3;
					} else if (isAddOperator(currentChar)) {
						content += currentChar;
						return new Token(TokenType.ADD_OPERATOR, content);
					} else if (isMultOperator(currentChar)) {
						content += currentChar;
						return new Token(TokenType.MULT_OPERATOR, content);
					} else if (isTwoDots(currentChar)) {
						content += currentChar;
						state = 4;
					} else if (isEquals(currentChar)) {
						content += currentChar;
						return new Token(TokenType.REL_OPERATOR, content);
					} else if (isLessThan(currentChar)) {
						content += currentChar;
						state = 5;
					} else if (isGreaterThan(currentChar)) {
						content += currentChar;
						state = 7;
					} else if (isDelimiter(currentChar)) {
						content += currentChar;
						return new Token(TokenType.DELIMITER, content);
					} else {
						error("Unexpected character: " + currentChar);
					}
					break;

				case 1:
					if (isLetter(currentChar) || isDigit(currentChar) || isUnderscore(currentChar)) {
						content += currentChar;
						state = 1;
					} else if (reservedWords.containsKey(content)) {
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
					} else if (isDot(currentChar)) {
						content += currentChar;
						state = 6;
					} else if (isLetter(currentChar) || isUnderscore(currentChar)) {
						error("Malformed number: " + content + currentChar);
					}else {
						back();
						return new Token(TokenType.INT_NUMBER, content);
					}
					break;

				case 3:
					while (!isEndOfLine(currentChar)) {
						currentChar = nextChar();
					}
					state = 0;
					break;

				case 4:
					if (isEquals(currentChar)) {
						content += currentChar;
						return new Token(TokenType.ASSIGNMENT, content);
					} else {
						back();
						return new Token(TokenType.DELIMITER, content);
					}
				case 5:
					if (isEquals(currentChar) || isGreaterThan(currentChar)) {
						content += currentChar;
						return new Token(TokenType.REL_OPERATOR, content);
					} else {
						back();
						return new Token(TokenType.REL_OPERATOR, content);
					}

				case 6:
					if (isDigit(currentChar)) {
						content += currentChar;
						state = 6;
					} else if (isLetter(currentChar) || isUnderscore(currentChar) || isDot(currentChar)) {
						error("Malformed number: " + content + currentChar);
					} else {
						back();
						return new Token(TokenType.REAL_NUMBER, content);
					}
					break;
				case 7:
					if (isEquals(currentChar)) {
						content += currentChar;
						return new Token(TokenType.REL_OPERATOR, content);
					} else {
						back();
						return new Token(TokenType.REL_OPERATOR, content);
					}
				default:
					break;
			}
		}
	}

	private boolean isDot(char c) {
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

	private boolean isAddOperator(char c) {
		return c == '+' || c == '-';
	}

	private boolean isMultOperator(char c) {
		return c == '*' || c == '/';
	}

	private boolean isDelimiter(char c) {
		return c == ';' || c == '(' || c==')' || c == ',';
	}

	private boolean isTwoDots(char c) {
		return c == ':';
	}

	private boolean isEquals(char c) {
		return c == '=';
	}

	private boolean isLessThan(char c) {
		return c == '<';
	}

	private boolean isGreaterThan(char c) {
		return c == '>';
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