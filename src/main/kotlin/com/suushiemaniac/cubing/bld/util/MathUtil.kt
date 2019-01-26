package com.suushiemaniac.cubing.bld.util

object MathUtil {
    infix fun Int.pMod(other: Int): Int {
        return ((this % other) + other) % other
    }
}