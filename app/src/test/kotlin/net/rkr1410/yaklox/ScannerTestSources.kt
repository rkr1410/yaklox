package net.rkr1410.yaklox

import org.junit.jupiter.params.provider.Arguments
import net.rkr1410.yaklox.TokenType.*

class ScannerTestSources {
    companion object {
        // lexemes and tokens they are expected to produce
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
            Arguments.of("?", QUESTION_MARK),
            Arguments.of(":", COLON),
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
}