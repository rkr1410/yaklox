package net.rkr1410.yaklox

class Yak {
    companion object {
        // Apparently @JvmStatic is needed by Java if I want to call this prefixed just by Yak,
        // otherwise need Yak.Companion (when unnamed, the companion object goes by default 'Companion' name)
        @JvmStatic
        fun main(args: Array<String>) {
            print("yak")
        }
    }
}