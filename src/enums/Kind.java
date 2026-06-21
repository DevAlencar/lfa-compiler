package enums;

public enum Kind {
	IDENTIFIER(0),
	INTLITERAL(1),
	FLOATLITERAL(2),
	BOOLLITERAL(3),
	OPERATOR(4),

	AND(5),
	BEGIN(6),
	BOOLEAN(7),
	DO(8),
	ELSE(9),
	END(10),
	IF(11),
	INTEGER(12),
	OR(13),
	PROGRAM(14),
	THEN(15),
	VAR(16),
	WHILE(17),

	SEMICOLON(18),
	COLON(19),
	BECOMES(20),
	DOT(21),
	LPAREN(22),
	RPAREN(23),
	EOT(24);

	private final int value;

	Kind(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public byte getByteValue() {
		return (byte) this.getValue();
	}

	public static Kind fromByte(byte value) {
		for (Kind k : Kind.values()) {
			if (k.getByteValue() == value) {
				return k;
			}
		}
		return null;
	}
}