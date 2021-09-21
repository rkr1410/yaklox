package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
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
        assertScannerFor("\t\t  \r\r  *            ")
            .producesFirstTokenOfType(STAR)
            .withLexeme("*")
            .withNoLiteral()
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @Test
    fun `Single line comments are ignored`() {
        assertScannerFor("  // this is first comment line\n  * // another comment\n  // and another")
            .producesFirstTokenOfType(STAR)
            .withLexeme("*")
            .withNoLiteral()
            .atLine(2)
            .followedByEofAtLine(3)
    }

    @Test
    fun testTokenLinePosition() {
        val src = "var abc = 5;\n" +
                "abc = abc + 5 / 2; //a comment\n" +
                "//another comment\n" +
                "var txt = \"here, have a string\";"
        val positions = listOf(
            1, 5, 9, 11, 12,
            1, 5, 7, 11, 13, 15, 17, 18,
            //comment line has no token positions
            1, 5, 9, 11, 32
        )
        val scanner = Scanner(src)

        val positionByToken = scanner.scanTokens().zip(positions).toMap()
        positionByToken.forEach {
            assertEquals(it.key.linePosition, it.value,
                "Expected [${it.key}] to be at line position ${it.value}")
        }
    }

    // Just test some code with random variations of symbols and operations allowed.
    // Also, check the last parsed token is a EOF, and it's line corresponds to last
    // line number in the file. This should more-or-less mean all tokens scanned
    // successfully. A nonspecific test at best, but maybe it's going to catch some
    // mistake one day? Who knows.
    // Anyway, it *is* a requirement on the app, and as such deserves its own
    // test, even if the net might be cast a bit too wide.
    @Test
    @DisplayName("Check a random file with some code has EOF token on the last file line")
    fun testRandomScript() {
        val fileName = "test-code.yak"
        val resource = ScannerTest::class.java.getResource("/$fileName")
        val src = resource?.readText()
        if (src != null) {
            val scanner = Scanner(src)
            val tokens = scanner.scanTokens()

            val fileLineCount = src.lines().size
            val lastToken = tokens[tokens.size - 1]

            assertEquals(lastToken.type, EOF)
            assertEquals(lastToken.line, fileLineCount)
        } else {
            throw IllegalArgumentException("File test/resources/$fileName not found!")
        }
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

        // Fake line position - it's too much hassle to require providing it each single test. There are separate tests
        // to check just the line position
        val expectedToken = Token(type, lexeme!!, literal ?: Token.NoLiteral, tokenLine!!, 1)
        val expectedEofToken = Token(EOF, "", Token.NoLiteral, eofLine ?: tokenLine!!, 1)
        val tokens = scanner.scanTokens()
        val actualToken = tokens[0]
        val actualEofToken = tokens[1]

        assertAll("tokens",
            { assertTokenWithoutLinePosition(expectedToken, actualToken) },
            { assertTokenWithoutLinePosition(expectedEofToken, actualEofToken) }
        )
    }

    private fun assertTokenWithoutLinePosition(expectedToken: Token, actualToken: Token) {
        assertAll("tokens",
            { assertEquals(expectedToken.type, actualToken.type) },
            { assertEquals(expectedToken.lexeme, actualToken.lexeme) },
            { assertEquals(expectedToken.literal, actualToken.literal) },
            { assertEquals(expectedToken.line, actualToken.line) }
        )
    }
}
