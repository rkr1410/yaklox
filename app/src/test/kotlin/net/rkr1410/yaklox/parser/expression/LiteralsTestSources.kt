package net.rkr1410.yaklox.parser.expression

import org.junit.jupiter.params.provider.Arguments

class LiteralsTestSources {
    companion object {
        @JvmStatic
        fun simpleNumberSource() = listOf(
            Arguments.of("1", 1.0),
            Arguments.of("2", 2.0),
            Arguments.of("3", 3.0),
            Arguments.of("4", 4.0),
            Arguments.of("5", 5.0),
            Arguments.of("6", 6.0),
            Arguments.of("7", 7.0),
            Arguments.of("8", 8.0),
            Arguments.of("9", 9.0),
            Arguments.of("0", 0.0),
            Arguments.of("12", 12.0),
            Arguments.of("99", 99.0),
            Arguments.of("667", 667.0),
            Arguments.of("1000000", 1_000_000.0),
            Arguments.of("299792458", 299_792_458.0),
            Arguments.of("1234", 1234.0),
            Arguments.of("65539", 65539.0),
            Arguments.of("2147483647", 2147483647.0),
        )

        @JvmStatic
        fun decimalNumberSource() = listOf(
            Arguments.of("0.00000000", 0.0),
            Arguments.of("0.999999999999999999999999999", 0.999999999999999999999999999),
            Arguments.of("1", 0.999999999999999999999999999),
            Arguments.of("0.999999999999999999999999999", 1),
            Arguments.of("12.999", 12.999),
            Arguments.of("23.001", 23.001),
            Arguments.of("456789.33", 456789.33),
            Arguments.of("999999.55555", 999999.55555),
            Arguments.of("34.0101010101010101010101010101010101", 34.01010101010101),
            Arguments.of("34.0101010101010101010101010101010101", 34.0101010101010101010101010101010101),
        )

        @JvmStatic
        fun stringsSource() = listOf(
            Arguments.of("\"\"", ""),
            Arguments.of("\"\"", ""),
            Arguments.of("\" \"", " "),
            Arguments.of("\"     \"", "     "),
            Arguments.of("\"a\"", "a"),
            Arguments.of("\"abc\"", "abc"),
            Arguments.of("\"this is an email: sbdt@corp.info\"", "this is an email: sbdt@corp.info"),
            Arguments.of("\"That one\nis a bit\nlonger,\nwith some line breaks\talong the\nway\"",
                "That one\nis a bit\nlonger,\nwith some line breaks\talong the\nway"),
            Arguments.of("\"\uD83D\uDE00\"", "\uD83D\uDE00"),
        )


    }
}