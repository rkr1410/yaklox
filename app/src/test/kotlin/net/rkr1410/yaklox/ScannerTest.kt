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
            Arguments.of("0", 0.toDouble(), "Zero"),
            Arguments.of("0.0", 0.0, "Zero with zero decimal part"),
            Arguments.of("0.222", 0.222, "Zero with decimal part"),
            Arguments.of("1234", 1234.toDouble(), "No decimal part number"),
            Arguments.of("1234.997", 1234.997, "Number with decimal part"),
        )

        @JvmStatic
        fun keywordSource() = listOf(
            Arguments.of("and", AND),
            Arguments.of("class", CLASS),
            Arguments.of("else", ELSE),
            Arguments.of("false", FALSE),
            Arguments.of("for", FOR),
            Arguments.of("fun", FUN),
            Arguments.of("if", IF),
            Arguments.of("nil", NIL),
            Arguments.of("or", OR),
            Arguments.of("return", RETURN),
            Arguments.of("super", SUPER),
            Arguments.of("this", THIS),
            Arguments.of("true", TRUE),
            Arguments.of("var", VAR),
            Arguments.of("while", WHILE),
        )

        @JvmStatic
        fun identifierSource() = listOf(
            Arguments.of("variableName"),
            Arguments.of("varName"),
            Arguments.of("v"),
            Arguments.of("v_"),
            Arguments.of("_v"),
            Arguments.of("_v_"),
            Arguments.of("v1"),
            Arguments.of("v_1"),
            Arguments.of("v191something_02y"),
            Arguments.of("reallySeriouslyAbsurdlyAndDefinitivelyVeryLongActuallyJustAboutTooLongIfIAmToBe" +
                    "CompletelyFrankWithYouAlthoughTooLongAsInByMeasureOfWhatMakesSenseForAHumanReadableVariableName" +
                    "AndNotTooLongAsInMakingTheCompilationFailAsInFactThisShouldPassCompilationVariableName"),
        )

        @JvmStatic
        fun stringSource() = listOf(
            Arguments.of("\"\"", "Empty string"),
            Arguments.of("\"b\"", "Single letter string"),
            Arguments.of("\"this\"", "Single word string"),
            Arguments.of("\"this is a test string\"", "Multiple words string"),
            Arguments.of("\"this is a test string\n\"", "Two-line string"),
            Arguments.of("\"this is a test string\nThe second line\nThird line\"", "Multiple-line string"),
        )
    }

    @ParameterizedTest(name = "{0} produces a NUMBER token")
    @MethodSource("numberSource")
    fun testNumberTokens(codeAndLexeme: String, literal: Double) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(NUMBER)
            .withLexeme(codeAndLexeme)
            .withLiteral(literal)
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "{0} produces a {1} token")
    @MethodSource("singleTokenSource")
    fun testSingleTokens(codeAndLexeme: String, tokenType: TokenType) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(tokenType)
            .withLexeme(codeAndLexeme)
            .withNoLiteral()
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "{0} produces a {1} keyword token")
    @MethodSource("keywordSource")
    fun testKeywordTokens(codeAndLexeme: String, tokenType: TokenType) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(tokenType)
            .withLexeme(codeAndLexeme)
            .withNoLiteral()
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "{0} produces an IDENTIFIER token")
    @MethodSource("identifierSource")
    fun testIdentifierTokens(codeAndLexeme: String) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(IDENTIFIER)
            .withLexeme(codeAndLexeme)
            .withNoLiteral()
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @Test
    fun testme() {
        assertScannerFor("\"abc\"")
            .producesFirstTokenOfType(STRING)
            .withLexeme("\"abc\"")
            .withLiteral("\"abc\"".substring(1, "\"abc\"".length - 1))
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "String test: {1}")
    @MethodSource("stringSource")
    fun testValidString(stringValue: String, desc: String) {
        val newLIneCount = stringValue.count { it == '\n' } + 1
        assertScannerFor(stringValue)
            .producesFirstTokenOfType(STRING)
            .withLexeme(stringValue)
            .withLiteral(stringValue.substring(1, stringValue.length - 1))
            .atLine(newLIneCount)
            .followedByEofAtTheSameLine()
    }

    @Test
    fun testInvalidSingleLineString() {
        val errBaos = Yak.setNewErrorByteArrayStream()
        val scanner = Scanner("\"missing closing quote")
        scanner.scanTokens()

        val reportedError = String(errBaos.toByteArray())
        assertEquals("[1:23] Expecting \"\n", reportedError)
    }

    @Test
    fun testInvalidMultilineString() {
        val errBaos = Yak.setNewErrorByteArrayStream()
        val scanner = Scanner("\"missing closing quote\nonline number 3\n?")
        scanner.scanTokens()

        val reportedError = String(errBaos.toByteArray())
        assertEquals("[3:2] Expecting \"\n", reportedError)
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
