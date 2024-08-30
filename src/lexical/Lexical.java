package lexical;

import java.util.ArrayList;

public class Lexical {
    public Lexical () {}

    public ArrayList<Token> tokenizer(String source) {
        Scanner sc = new Scanner(source);
		Token tk;
		ArrayList<Token> buffer = new ArrayList<Token>();

		while(true) {
			tk = sc.nextToken();
			if(tk == null) {
				break;
			}
			buffer.add(tk);
			System.out.println(tk);
		}
		
		return buffer;
	}
}
