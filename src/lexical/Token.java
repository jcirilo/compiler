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
		this.col = col-text.length()+1;
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

	public TokenType setType(TokenType type) {
		this.type = type;
		return this.type;
	}

	public String setText(String text) {
		this.text = text;
		return this.text;
	}

	public int setRow(int row) {
		this.row = row;
		return this.row;
	}

	public int setCol(int col) {
		this.col = col;
		return this.col;
	}

	@Override
	public String toString() {
		return "Token [type = " + type + ", text = " + text + "]";
	}

}
