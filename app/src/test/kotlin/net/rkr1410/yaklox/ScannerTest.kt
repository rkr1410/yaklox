package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.IllegalStateException

internal class ScannerTest {
    companion object {
        @JvmStatic
        fun singleTokenSource() = listOf(
            Arguments.of("(", LEFT_PAREN),
            Arguments.of(")", RIGHT_PAREN),
            Arguments.of("{", LEFT_BRACE),
            Arguments.of("}", RIGHT_BRACE),
            Arguments.of(",", COMMA),
            Arguments.of(".", DOT),
            Arguments.of("-", MINUS),
            Arguments.of("+", PLUS),
            Arguments.of(";", SEMICOLON),
            Arguments.of("*", STAR),
            Arguments.of("!", BANG),
            Arguments.of("/", SLASH),
            Arguments.of("=", EQUAL),
            Arguments.of("<", LESS),
            Arguments.of(">", GREATER),
            Arguments.of("==", EQUAL_EQUAL),
            Arguments.of("!=", BANG_EQUAL),
            Arguments.of("<=", LESS_EQUAL),
            Arguments.of(">=", GREATER_EQUAL),
        )

        @JvmStatic
        fun numberSource() = listOf(
            Arguments.of("Zero", "0", 0.toDouble()),
            Arguments.of("Zero with zero decimal part", "0.0", 0.0),
            Arguments.of("Zero with decimal part", "0.222", 0.222),
            Arguments.of("No decimal part number", "1234", 1234.toDouble()),
            Arguments.of("Number with decimal part", "1234.997", 1234.997),
        )
    }

    @ParameterizedTest(name = "{0} produces a NUMBER token")
    @MethodSource("numberSource")
    fun numberTokenTest(desc: String, codeAndLexeme: String, literal: Double) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(NUMBER)
            .withLexeme(codeAndLexeme)
            .withLiteral(literal)
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "{0} produces a {1} token")
    @MethodSource("singleTokenSource")
    fun singleTokenTest(codeAndLexeme: String, tokenType: TokenType) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(tokenType)
            .withLexeme(codeAndLexeme)
            .withNoLiteral()
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @Test
    fun `Newlines increase line number`() {
        assertScannerFor("\n\n\n*\n\n")
            .producesFirstTokenOfType(STAR)
            .withLexeme("*")
            .withNoLiteral()
            .atLine(4)
            .followedByEofAtLine(6)
    }

    @Test
    fun `Space, tabs and carriage returns are ignored`() {
        assertScannerFor("\t\t  \r\r  *")
            .producesFirstTokenOfType(STAR)
            .withLexeme("*")
            .withNoLiteral()
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @Test
    fun `single line comments are ignored`() {
        assertScannerFor("  // this is first comment line\n  * // another comment\n  // and another")
            .producesFirstTokenOfType(STAR)
            .withLexeme("*")
            .withNoLiteral()
            .atLine(2)
            .followedByEofAtLine(3)

    }
}

fun assertScannerFor(source: String) = ScannerAsserter(source)

class ScannerAsserter(private val source: String) {
    fun producesFirstTokenOfType(type: TokenType) = FirstTokenAndEofAsserter(Scanner(source), type)
}

class FirstTokenAndEofAsserter(private val scanner: Scanner, private val type: TokenType) {
    private var lexeme: String? = null
    private var literal: Any? = null
    private var tokenLine: Int? = null

    fun withLexeme(lexeme: String): FirstTokenAndEofAsserter {
        this.lexeme = lexeme
        return this
    }

    fun withLiteral(literal: Any): FirstTokenAndEofAsserter {
        this.literal = literal
        return this
    }

    fun withNoLiteral(): FirstTokenAndEofAsserter {
        this.literal = null
        return this
    }

    fun atLine(line: Int): FirstTokenAndEofAsserter {
        this.tokenLine = line
        return this
    }

    fun followedByEofAtTheSameLine() {
        followedByEofAtLine(tokenLine)
    }

    fun followedByEofAtLine(eofLine: Int?) {
        tokenLine ?: throw IllegalStateException("Token line must not be null")
        lexeme ?: throw IllegalStateException("Lexeme must not be null")

        val expectedToken = Token(type, lexeme!!, literal ?: Token.NoLiteral, tokenLine!!)
        val expectedEofToken = Token(EOF, "", Token.NoLiteral, eofLine ?: tokenLine!!)
        val tokens = scanner.scanTokens()

        assertAll("tokens",
            { assertEquals(expectedToken, tokens[0]) },
            { assertEquals(expectedEofToken, tokens[1]) }
        )
    }
}
