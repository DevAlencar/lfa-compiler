import enums.Kind;
import enums.Spelling;

public class Token {
	public byte kind;
	public String spelling;

	public Token(byte kind, String spelling) {
		this.kind = kind;
		this.spelling = spelling;

		if (this.kind == Kind.IDENTIFIER.getValue()) {
			for (
				int k = Kind.BEGIN.getValue();
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
