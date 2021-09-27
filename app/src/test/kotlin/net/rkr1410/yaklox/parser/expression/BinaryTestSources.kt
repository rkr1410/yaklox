package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.TokenType.*
import org.junit.jupiter.params.provider.Arguments

class BinaryTestSources {
    companion object {
        @JvmStatic
        fun associativitySource() = listOf(
            Arguments.of("1 + 2 + 3", 1.0, 2.0, 3.0, PLUS, "Addition"),
            Arguments.of("4 - 5 - 6", 4.0, 5.0, 6.0, MINUS, "Subtraction"),
            Arguments.of("7 * 8 * 9", 7.0, 8.0, 9.0, STAR, "Multiplication"),
            Arguments.of("10 / 11 / 12", 10.0, 11.0, 12.0, SLASH, "Division"),
            Arguments.of("13 < 14 < 15", 13.0, 14.0, 15.0, LESS, "Lesser-than"),
            Arguments.of("16 <= 17 <= 18", 16.0, 17.0, 18.0, LESS_EQUAL, "Lesser-than-or-equal"),
            Arguments.of("19 > 20 > 21", 19.0, 20.0, 21.0, GREATER, "Greater-than"),
            Arguments.of("22 >= 23 >= 24", 22.0, 23.0, 24.0, GREATER_EQUAL, "Greater-than-or-equal"),
            Arguments.of("true == false == 2", true, false, 2.0, EQUAL_EQUAL, "Equal comparison"),
            Arguments.of("true != false != 2", true, false, 2.0, BANG_EQUAL, "Not-equals comparison"),
        )

        @JvmStatic
        fun strictlyBinaryOperatorSources() = listOf(
            "==",
            "!=",
            ">",
            ">=",
            "<",
            "<=",
            "+",
            "/",
            "*"
        ).map{ tt -> Arguments.of(tt) }
    }
}