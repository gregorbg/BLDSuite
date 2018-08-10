package com.suushiemaniac.cubing.bld.util

object ArrayUtil {
    fun <S, T> Array<T>.applyIndex(element: T, dest: Array<S>): S {
        return dest[this.indexOf(element)]
    }

    fun <T> Array<T>.index(element: T): Int {
        return this.indexOf(element) // FIXME remove when migration fully done
    }

    fun <T> Array<T>.swap(from: Int, to: Int) {
        this[to] = this[from].also { this[from] = this[to] }
    }

    fun <T> Array<T>.cycleLeft() {
        for (i in 0 until (this.size - 1))
            this.swap(i, i + 1)
    }

    fun <T> Array<T>.cycleRight() {
        for (i in (this.size - 1) downTo 1)
            this.swap(i - 1, i)
    }

    fun <T> Array<Array<T>>.deepOuterIndex(searchObject: T): Int {
        for (i in this.indices)
            for (element in this[i])
                if (element == searchObject) return i

        return -1
    }

    fun <T> Array<Array<T>>.deepInnerIndex(searchObject: T): Int {
        for (subArray in this)
            for (i in subArray.indices)
                if (subArray[i] == searchObject) return i

        return -1
    }

    fun Int.countingArray(): Array<Int> {
        return Array(this) { it }
    }

    inline fun <reified T> Int.filledArray(value: T): Array<T> {
        return Array(this) { value }
    }

    inline fun <reified T> Int.filledArray(value: () -> T): Array<T> {
        return Array(this) { value() }
    }

    fun <T> Array<T>.countOf(element: T): Int {
        return this.count { it == element }
    }

    fun <T> Array<Array<T>>.deepCount(element: T): Int {
        return this.map { it.countOf(element) }.sum()
    }
}