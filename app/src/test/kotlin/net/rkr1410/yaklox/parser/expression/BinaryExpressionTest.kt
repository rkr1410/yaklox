package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.TokenType
import net.rkr1410.yaklox.TokenType.*
import net.rkr1410.yaklox.tools.ExprPrinter
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BinaryExpressionTest : ExpressionTestBase() {
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
    }

    @ParameterizedTest(name = "{5} is left-associative ({0})")
    @MethodSource("associativitySource")
    @DisplayName("Binary operators are left-associative")
    fun testLeftAssociativity(
        src: String,
        firstLiteral: Any,
        secondLiteral: Any,
        thirdLiteral: Any,
        tokenType: TokenType,
        desc: String
    ) {
        val expr = expression(src)

        IsBinary(expr, tokenType)
            .hasLeftHandSideWhich { lhs ->
                IsBinary(lhs, tokenType)
                    .hasLeftHandSideWhich { lhsInner ->
                        IsLiteral(lhsInner, firstLiteral)
                    }
                    .hasRightHandSideWhich { rhsInner ->
                        IsLiteral(rhsInner, secondLiteral)
                    }
            }
            .hasRightHandSideWhich { rhs ->
                IsLiteral(rhs, thirdLiteral)
            }
            .assert()
    }

/*
    expression  →
    equality    → != ==
    comparison  → > >= < <=
    term        → - +
    factor      → / *
    unary       → ! -
    primary     → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
 */
    @Test
    fun `test order of operations`() {
        val expr = expression("1 == 2 > 3 - 4 * 5")

        IsBinary(expr, EQUAL_EQUAL)
            .hasLeftHandSideWhich { lhsEqEq ->
                IsLiteral(lhsEqEq, 1.0)
            }
            .hasRightHandSideWhich { rhsEqEq ->
                IsBinary(rhsEqEq, GREATER)
                    .hasLeftHandSideWhich { lhsGr ->
                        IsLiteral(lhsGr, 2.0)
                    }
                    .hasRightHandSideWhich { rhsGr ->
                        IsBinary(rhsGr, MINUS)
                            .hasLeftHandSideWhich { lhsMinus ->
                                IsLiteral(lhsMinus, 3.0)
                            }
                            .hasRightHandSideWhich { rhsMinus ->
                                IsBinary(rhsMinus, STAR)
                                    .hasLeftHandSideWhich { lhs -> IsLiteral(lhs, 4.0) }
                                    .hasRightHandSideWhich { rhs -> IsLiteral(rhs, 5.0) }
                            }
                    }
            }
            .assert()
    }

    @Test
    fun `test order of operations with parentheses`() {
        val expr = expression("(1 == 2) > 3")

        IsBinary(expr, GREATER)
            .hasLeftHandSideWhich { lhsGr ->
                IsGrouping(lhsGr).groupsExpressionWhich { grp ->
                    IsBinary(grp, EQUAL_EQUAL)
                        .hasLeftHandSideWhich { lhsEqEq ->
                            IsLiteral(lhsEqEq, 1.0)
                        }
                        .hasRightHandSideWhich { rhsEqEq ->
                            IsLiteral(rhsEqEq, 2.0)
                        }
                }
            }
            .assert()
    }
}