package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*

class Scanner(private val source: String) {

    private var start: Int = 0
    private var current: Int = 0
    private var line: Int = 1
    private val length = source.length
    private val tokens = mutableListOf<Token>()

    fun scanTokens(): List<Token> {

        while(!isEof()) {
            start = current
            scanToken()
        }

        tokens.add(Token(type = EOF, line = line))
        return tokens
    }
/*
    // single-char lexemes
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // single, but possibly two-char
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // literals
    IDENTIFIER, STRING, NUMBER,

    // keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
 */
    private fun scanToken() {
        when (advance()) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '/' -> if (nextCharIs('/')) singleLineComment() else addToken(SLASH)
            '!' -> addIfNextCharIsEquals(BANG_EQUAL, BANG)
            '=' -> addIfNextCharIsEquals(EQUAL_EQUAL, EQUAL)
            '<' -> addIfNextCharIsEquals(LESS_EQUAL, LESS)
            '>' -> addIfNextCharIsEquals(GREATER_EQUAL, GREATER)
            '\n' -> line++
            ' ', '\t', '\r' -> {}
            else -> when {
//                c.isAlpha() -> identifier()
                currentChar().isDigit() -> number()
            }
        }
    }

    fun number() {
        while (!isEof() && nextCharIsDigit()) advance()
        if (nextCharIs('.')) advance()
        while (!isEof() && nextCharIsDigit()) advance()

        val lexeme = currentLexeme()
        val literal = lexeme.toDouble()
        addToken(NUMBER, literal, lexeme)
    }

    private fun singleLineComment() {
        while (!isEof() && !nextCharIs('\n')) advance()
        System.err.println(currentLexeme())
    }

    private fun addIfNextCharIsEquals(typeIfNextCharIsEquals: TokenType, typeOtherwise: TokenType) {
        if (nextCharIs('=')) {
            advance()
            addToken(typeIfNextCharIsEquals)
        } else {
            addToken(typeOtherwise)
        }
    }

    private fun addToken(tokenType: TokenType, literal: Any = Token.NoLiteral, lexeme: String? = null) {
        val lex = lexeme ?: currentLexeme()
        val token = Token(tokenType, lex, literal, line)
        tokens.add(token)
    }

    private fun isEof() = current >= length
    private fun currentLexeme() = source.substring(start, current)
    private fun nextCharIs(c: Char) = if (isEof()) false else c == source[current]
    private fun nextCharIsDigit() = if (isEof()) false else source[current].isDigit()
    private fun advance() = source[current++]
    private fun currentChar() = source[current - 1]


    companion object {
        private val keywords = mapOf(
            "and" to AND,
            "class" to CLASS,
            "else" to ELSE,
            "false" to FALSE,
            "for" to FOR,
            "fun" to FUN,
            "if" to IF,
            "nil" to NIL,
            "or" to OR,
            "return" to RETURN,
            "super" to SUPER,
            "this" to THIS,
            "true" to TRUE,
            "var" to VAR,
            "while" to WHILE,
        )

        fun tokenTypeForLexeme(literal: String) = keywords.getOrDefault(literal, IDENTIFIER)
    }
}