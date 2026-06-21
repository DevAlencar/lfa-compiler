import enums.Kind;
import enums.Spelling;

public class Token {
	public byte kind;
	public String spelling;
	public int currentLine;
	public int currentColumn;

	public Token(byte kind, String spelling, int currentLine, int currentColumn) {
		this.kind = kind;
		this.spelling = spelling;
		this.currentColumn = currentColumn;
		this.currentLine = currentLine;

		if (this.kind == Kind.IDENTIFIER.getValue()) {
			for (
				int k = Kind.AND.getValue();
				k <= Kind.WHILE.getValue();
				k++
			) {
				if (spelling.equals(Spelling.values()[k].getValue())) {
					this.kind = (byte)k;
					break;
				}
			}
		}
	}

}
