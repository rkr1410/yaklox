package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.Expression
import net.rkr1410.yaklox.Token
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertIs

/*
    I think literals are as far as I'm going to go with tests using visitors.
    It's tests - so the point is to test, not to hold on tightly to 'best practices',
    so I think I'm just going to use reflection as soon as I'm done with testing literals.
 */
class LiteralExpressionTest : ExpressionTestBase() {

    @ParameterizedTest(name = "Simple number expression parses successfully: {0} -> {1}")
    @DisplayName(value = "Simple number expression parses successfully")
    @MethodSource("net.rkr1410.yaklox.parser.expression.LiteralsTestSources#simpleNumberSource")
    fun testSimpleNumbers(numSource: String, expectedLiteral: Double) {
        val expr = expression(numSource)

        assertIs<Expression.Literal>(expr)
        assertEquals(expectedLiteral, expr.accept(literalValueVisitor))
    }

    @ParameterizedTest(name = "Number expression with decimal places parses successfully: {0} -> {1}")
    @DisplayName(value = "Number expression with decimal places parses successfully")
    @MethodSource("net.rkr1410.yaklox.parser.expression.LiteralsTestSources#decimalNumberSource")
    fun testDecimalNumbers(numSource: String, expectedLiteral: Double) {
        val expr = expression(numSource)

        assertIs<Expression.Literal>(expr)
        assertEquals(expectedLiteral, expr.accept(literalValueVisitor))
    }

    @ParameterizedTest(name = "String expression parses successfully: {0}")
    @DisplayName(value = "String expression parses successfully")
    @MethodSource("net.rkr1410.yaklox.parser.expression.LiteralsTestSources#stringsSource")
    fun testStrings(stringSource: String, stringLiteral: String) {
        val expr = expression(stringSource)

        assertIs<Expression.Literal>(expr)
        assertEquals(stringLiteral, expr.accept(literalValueVisitor))
    }

    @Test
    fun `false is a literal`() {
        val expr = expression("false")
        assertIs<Expression.Literal>(expr)
        assertEquals(false, expr.accept(literalValueVisitor))
    }

    @Test
    fun `true is a literal`() {
        val expr = expression("true")
        assertIs<Expression.Literal>(expr)
        assertEquals(true, expr.accept(literalValueVisitor))
    }

    @Test
    fun `nil is a NoLiteral literal`() {
        val expr = expression("nil")
        assertIs<Expression.Literal>(expr)
        assertEquals(Token.NoLiteral, expr.accept(literalValueVisitor))
    }
}