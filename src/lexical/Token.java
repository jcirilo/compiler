package lexical;

import utils.TokenType;

public class Token {
	private TokenType type;
	private String text;
	private int row;
	private int col;
	public Token(TokenType type, String text, int row, int col) {
		super();
		this.type = type;
		this.text = text;
		this.row = row;
		this.col = col;
	}

	public TokenType getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public String toString() {
		return "Token [type = " + type + ", text = " + text + "]";
	}

}
