package enums;

public enum Kind {
	IDENTIFIER(0),
	INTLITERAL(1),
	OPERATOR(2),
	BEGIN(3),
	CONST(4),
	DO(5),
	ELSE(6),
	END(7),
	IF(8),
	IN(9),
	LET(10),
	THEN(11),
	VAR(12),
	WHILE(13),
	SEMICOLON(14),
	COLON(15),
	BECOMES(16),
	IS(17),
	LPAREN(18),
	RPAREN(19),
	BOOLLITERAL(20),
	EOT(21);

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