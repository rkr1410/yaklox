package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.lang.IllegalStateException

internal class ScannerTest {
    @ParameterizedTest(name = "{0} produces a NUMBER token")
    @MethodSource("net.rkr1410.yaklox.ScannerTestSources#numberSource")
    fun testNumberTokens(codeAndLexeme: String, literal: Double) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(NUMBER)
            .withLexeme(codeAndLexeme)
            .withLiteral(literal)
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "{0} produces a {1} token")
    @MethodSource("net.rkr1410.yaklox.ScannerTestSources#singleTokenSource")
    fun testSingleTokens(codeAndLexeme: String, tokenType: TokenType) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(tokenType)
            .withLexeme(codeAndLexeme)
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "{0} produces a {1} keyword token")
    @MethodSource("net.rkr1410.yaklox.ScannerTestSources#keywordSource")
    fun testKeywordTokens(codeAndLexeme: String, tokenType: TokenType) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(tokenType)
            .withLexeme(codeAndLexeme)
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "{0} produces an IDENTIFIER token")
    @MethodSource("net.rkr1410.yaklox.ScannerTestSources#identifierSource")
    fun testIdentifierTokens(codeAndLexeme: String) {
        assertScannerFor(codeAndLexeme)
            .producesFirstTokenOfType(IDENTIFIER)
            .withLexeme(codeAndLexeme)
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @ParameterizedTest(name = "String test: {1}")
    @MethodSource("net.rkr1410.yaklox.ScannerTestSources#stringSource")
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
    fun `Newlines increase line number`() {
        assertScannerFor("\n\n\n*\n\n")
            .producesFirstTokenOfType(STAR)
            .withLexeme("*")
            .atLine(4)
            .followedByEofAtLine(6)
    }

    @Test
    fun `Space, tabs and carriage returns are ignored`() {
        assertScannerFor("\t\t  \r\r  *            ")
            .producesFirstTokenOfType(STAR)
            .withLexeme("*")
            .atLine(1)
            .followedByEofAtTheSameLine()
    }

    @Test
    fun `Single line comments are ignored`() {
        assertScannerFor("  // this is first comment line\n  * // another comment\n  // and another")
            .producesFirstTokenOfType(STAR)
            .withLexeme("*")
            .atLine(2)
            .followedByEofAtLine(3)
    }

    @Test
    fun testInvalidEmptyString() {
        assertCodeGivesError("\"", "[1:1] Expecting \"")
    }

    @Test
    fun testInvalidSingleLineString() {
        assertCodeGivesError("\"missing closing quote", "[1:22] Expecting \"")
    }

    @Test
    fun testInvalidMultilineString() {
        assertCodeGivesError("\"missing closing quote\nonline number 3\n?z", "[3:3] Expecting \"")
    }

    @Test
    fun testUnrecognizedCharacter() {
        assertCodeGivesError("~", "[1:1] Unexpected character '~'")
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
            assertEquals(it.value, it.key.linePosition,
                "Expected [${it.key}] to be at line position ${it.value}")
        }
    }

    @Test
    @DisplayName("Regression scan: a file with some code has EOF token on the last file line")
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

fun assertCodeGivesError(code: String, exoectedError: String) {
    val errBaos = Yak.setNewErrorByteArrayStream()
    val scanner = Scanner(code)
    scanner.scanTokens()

    val reportedError = String(errBaos.toByteArray()).filter { it != '\n' }
    assertEquals(exoectedError, reportedError)
}

fun assertScannerFor(source: String) = ScannerAsserter(source)

class ScannerAsserter(private val source: String) {
    fun producesFirstTokenOfType(type: TokenType) = FirstTokenAndEofAsserter(Scanner(source), type)
}

class FirstTokenAndEofAsserter(private val scanner: Scanner, private val type: TokenType) {
    private var lexeme: String? = null
    private var literal: Any? = null
    private var tokenLine: Int? = null
    var checks = mutableMapOf( "lexeme" to false, "literal" to false, "line" to false )

    fun withLexeme(lexeme: String): FirstTokenAndEofAsserter {
        checks["lexeme"] = true
        this.lexeme = lexeme
        return this
    }

    fun withLiteral(literal: Any): FirstTokenAndEofAsserter {
        checks["literal"] = true
        this.literal = literal
        return this
    }

    fun atLine(line: Int): FirstTokenAndEofAsserter {
        checks["line"] = true
        this.tokenLine = line
        return this
    }

    fun followedByEofAtTheSameLine() {
        followedByEofAtLine(tokenLine)
    }

    fun shouldCheck(type: String) = checks[type] ?: false

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
            { if (shouldCheck("lexeme"))  assertEquals(expectedToken.lexeme, actualToken.lexeme) },
            { if (shouldCheck("literal")) assertEquals(expectedToken.literal, actualToken.literal) },
            { if (shouldCheck("line"))    assertEquals(expectedToken.line, actualToken.line) }
        )
    }
}
