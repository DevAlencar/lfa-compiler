package enums;

public enum Spelling {
	IDENTIFIER("<identifier>"),
	INTEGER_LITERAL("<integer-literal>"),
	OPERATOR("<operator>"),
	BEGIN("begin"),
	CONST("const"),
	DO("do"),
	ELSE("else"),
	END("end"),
	IF("if"),
	IN("in"),
	LET("let"),
	THEN("then"),
	VAR("var"),
	WHILE("while"),
	SEMICOLON(";"),
	COLON(":"),
	BECOMES(":="),
	IS("~"),
	LPAREN("("),
	RPAREN(")"),
	TRUE("true"),
	FALSE("false"),
	EOT("<eot>");

	private final String value;

	Spelling(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
