package com.suushiemaniac.cubing.bld.util

object StringUtil {
    fun String.containsAny(containPoss: Iterable<String>): Boolean {
        return containPoss.any { this.contains(it) }
    }

    fun String.containsAll(containOblig: Iterable<String>): Boolean {
        return containOblig.all { this.contains(it) }
    }

    fun String.toCharStrings(): Iterable<String> {
        return this.toCharArray().map { c -> c.toString() }
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

    fun String.charCount(c: Char): Int {
        return this.count { it == c }
    }

    fun String.contentSetEquals(that: String): Boolean {
        return this.length == that.length
                && this.containsAll(that.toCharStrings())
                && that.containsAny(this.toCharStrings())
    }
}
