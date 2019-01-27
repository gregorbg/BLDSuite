package com.suushiemaniac.cubing.bld.util

object StringUtil {
    private val WS_REGEX = "\\s+".toRegex()

    fun String.toCharStrings(): List<String> {
        return this.toCharArray().map(Char::toString)
    }

    fun String.guessRegExpRange(): String {
        val init = this.first()

        for (i in 0 until this.length) {
            if ((init.toInt() + i).toChar() != this[i]) {
                return this
            }
        }

        return "[" + this.first() + "-" + this.last() + "]"
    }

    fun String.repeatWithGap(times: Int, gap: String = " "): String {
        return List(times) { this }.joinToString(gap)
    }

    fun String.splitAtWhitespace(): List<String> {
        return this.split(WS_REGEX)
    }

    fun String.contentSetEquals(that: String): Boolean {
        return this.toCharStrings().containsAll(that.toCharStrings())
                && that.toCharStrings().containsAll(this.toCharStrings())
    }
}
