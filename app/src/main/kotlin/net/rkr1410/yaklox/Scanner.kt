package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*

class Scanner(private val source: String) {

    private var start: Int = 0
    private var current: Int = 0
    private var line: Int = 1
    private var linePosition: Int = 1
    private val length = source.length
    private val tokens = mutableListOf<Token>()

    fun scanTokens(): List<Token> {

        while(!isEof()) {
            start = current
            scanToken()
        }

        tokens.add(Token(type = EOF, line = line, linePosition = linePosition))
        return tokens
    }

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
            '"' -> string()
            '\n' -> increaseLineNumber()
            ' ', '\t', '\r' -> {}
            else -> when {
                currentChar().isAlpha() -> identifier()
                currentChar().isDigit() -> number()
                else -> error("Unexpected character '${currentChar()}'")
            }
        }
    }

    private fun string() {
        while (!isEof() && !nextCharIs('"')) {
            if (nextCharIs('\n')) increaseLineNumber()
            advance()
        }

        // Consume closing quote
        if (!isEof()) advance() else error("Expecting \"").also { return }

        val lexeme = currentLexeme()
        val literal = lexeme.substring(1, lexeme.length - 1)
        addToken(STRING, literal, lexeme)
    }

    private fun identifier() {
        while (!isEof() && nextCharIsIdChar()) advance()

        val lexeme = currentLexeme()
        val tokenType = tokenTypeForLexeme(lexeme)
        addToken(tokenType = tokenType, lexeme = lexeme)
    }

    private fun number() {
        while (!isEof() && nextCharIsDigit()) advance()
        if (nextCharIs('.')) advance()
        while (!isEof() && nextCharIsDigit()) advance()

        val lexeme = currentLexeme()
        val literal = lexeme.toDouble()
        addToken(NUMBER, literal, lexeme)
    }

    private fun singleLineComment() {
        while (!isEof() && !nextCharIs('\n')) advance()
    }

    private fun addIfNextCharIsEquals(typeIfNextCharIsEquals: TokenType, typeOtherwise: TokenType) {
        if (nextCharIs('=')) {
            advance().also { addToken(typeIfNextCharIsEquals) }
        } else {
            addToken(typeOtherwise)
        }
    }

    private fun addToken(tokenType: TokenType, literal: Any = Token.NoLiteral, lexeme: String? = null) {
        val lex = lexeme ?: currentLexeme()
        val token = Token(tokenType, lex, literal, line, linePosition - lex.length)
        tokens.add(token)
    }

    private fun isEof() = current >= length
    private fun currentLexeme() = source.substring(start, current)
    private fun nextCharIs(c: Char) = if (isEof()) false else c == source[current]
    private fun nextCharIsDigit() = if (isEof()) false else source[current].isDigit()
    private fun nextCharIsIdChar() = if (isEof()) false else source[current].isAlphaNumeric()
    private fun advance() = source[current++].also { linePosition++ }
    private fun increaseLineNumber() = line++.also { linePosition = 1 }
    private fun currentChar() = source[current - 1]
    private fun Char.isAlpha() = this in 'a'..'z' || this in 'A'..'Z' || this == '_'
    private fun Char.isAlphaNumeric() = this.isDigit() || this.isAlpha()
    private fun error(msg: String) = Yak.reportError(line, linePosition - 1, msg)

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