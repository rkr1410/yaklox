package net.rkr1410.yaklox

data class Token(
    val type: TokenType,
    val lexeme: String = "",
    val literal: Any = NoLiteral,
    val line: Int,
    val linePosition: Int
) {
    companion object NoLiteral {
        override fun toString(): String {
            return "None"
        }
    }

    override fun toString(): String {
        return "$type $lexeme $literal $line $linePosition"
    }
}



enum class TokenType {
    // single-char lexemes
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA,
    DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, QUESTION_MARK,
    COLON,

    // single, but possibly two-char
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // literals
    IDENTIFIER, STRING, NUMBER,

    // keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}