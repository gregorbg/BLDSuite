package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.bld.util.CollectionUtil.zip

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

    fun String.splitLines(): List<String> {
        return this.split("\n")
    }

    fun String.contentSetEquals(that: String): Boolean {
        return this.toCharStrings().containsAll(that.toCharStrings())
                && that.toCharStrings().containsAll(this.toCharStrings())
    }

    fun String.alignWhitespaces(delimiter: String = "\t"): String {
        val lines = this.split("\n")
        val cells = lines.map { it.split(delimiter) }

        val lineLengths = cells.map { it.map(String::length) }

        val maxLengthPerColumn = zip(*lineLengths.toTypedArray()).map { it.max() ?: 0 }

        val paddedLines = cells.map {
            it.mapIndexed { i, str -> str.padEnd(maxLengthPerColumn[i], ' ') }.joinToString("")
        }

        return paddedLines.joinToString("\n")
    }
}
