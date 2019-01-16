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
}