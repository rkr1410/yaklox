package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.TokenType
import net.rkr1410.yaklox.TokenType.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class BinaryExpressionTest : ExpressionTestBase() {
    @ParameterizedTest(name = "{5} is left-associative ({0})")
    @MethodSource("net.rkr1410.yaklox.parser.expression.BinaryTestSources#associativitySource")
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

    @ParameterizedTest(name = "Source \"{0} 42\" gives an error: '[1:1] Missing left-hand side'")
    @MethodSource("net.rkr1410.yaklox.parser.expression.BinaryTestSources#strictlyBinaryOperatorSources")
    fun `Operators without unary equivalent cannot be used as such`(operator: String) {
        assertErrorFor("$operator 42", "[1:1] Missing left-hand side")
    }
}