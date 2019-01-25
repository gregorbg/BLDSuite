package com.suushiemaniac.cubing.bld.util

import java.security.SecureRandom

object CollectionUtil {
    private val secRandom = SecureRandom()

    fun <T> List<T>.randomOrNull(): T? {
        if (this.isEmpty()) return null

        return this[secRandom.nextInt(this.size)]
    }

    fun <T> zip(vararg lists: List<T>): List<List<T>> {
        return zip(*lists, transform = { it })
    }

    fun <T, V> zip(vararg lists: List<T>, transform: (List<T>) -> V): List<V> {
        val minSize = lists.map { it.size }.min() ?: return emptyList()
        val list = mutableListOf<V>()

        val iterators = lists.map { it.iterator() }

        for (i in 0 until minSize) {
            list.add(transform(iterators.map { it.next() }))
        }

        return list
    }

    fun Int.countingList(): List<Int> {
        return List(this) { it }
    }

    // TODO use library instead?
    fun <T> Collection<T>.powerset(): Set<Set<T>> = powerset(this, setOf(setOf()))

    private tailrec fun <T> powerset(left: Collection<T>, acc: Set<Set<T>>): Set<Set<T>> = when {
        left.isEmpty() -> acc
        else -> powerset(left.drop(1), acc + acc.map { it + left.first() })
    }

    fun <T> Set<T>.permutations(): Set<List<T>> = toList().permutations()

    fun <T> List<T>.permutations(): Set<List<T>> = when {
        isEmpty() -> emptySet()
        size == 1 -> setOf(listOf(this[0]))
        else -> {
            drop(1).permutations()
                    .flatMap { sublist -> (0..sublist.size).map { i -> sublist.plusAt(i, this[0]) } }
                    .toSet()
        }
    }

    internal fun <T> List<T>.plusAt(index: Int, element: T): List<T> = when (index) {
        !in 0..size -> throw Error("Cannot put at index $index because size is $size")
        0 -> listOf(element) + this
        size -> this + element
        else -> dropLast(size - index) + element + drop(index)
    }
}