package enums;

public enum Spelling {
	IDENTIFIER("<identifier>"),
	INTEGER_LITERAL("<integer-literal>"),
	FLOAT_LITERAL("<float-literal>"),
	BOOLEAN_LITERAL("<boolean-literal>"),
	OPERATOR("<operator>"),

	AND("and"),
	BEGIN("begin"),
	BOOLEAN("boolean"),
	DO("do"),
	ELSE("else"),
	END("end"),
	IF("if"),
	INTEGER("integer"),
	OR("or"),
	PROGRAM("program"),
	THEN("then"),
	VAR("var"),
	WHILE("while"),

	SEMICOLON(";"),
	COLON(":"),
	BECOMES(":="),
	DOT("."),
	LPAREN("("),
	RPAREN(")"),
	EOT("<eot>");

	private final String value;

	Spelling(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
