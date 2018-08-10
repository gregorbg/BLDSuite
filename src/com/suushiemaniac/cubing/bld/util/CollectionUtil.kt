package com.suushiemaniac.cubing.bld.util

import java.util.*

object CollectionUtil {
    fun <T> List<T>.random(): T? {
        if (this.isEmpty()) return null

        return this[Random().nextInt(this.size)]
    }
}