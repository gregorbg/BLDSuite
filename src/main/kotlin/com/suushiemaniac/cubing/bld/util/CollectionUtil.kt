package com.suushiemaniac.cubing.bld.util

import kotlin.random.Random

object CollectionUtil {
    fun <T> List<T>.randomOrNull(): T? {
        return this.takeUnless { it.isEmpty() }
                ?.get(Random.nextInt(this.size))
    }

    fun <T> List<List<T>>.zip(): List<List<T>> {
        val (head, tail) = this.sortedByDescending { it.size }.headOrNullWithTail()
        val foldBase = listOf(head ?: listOf())

        return tail.fold(foldBase) { acc, list ->
            acc.zip(list) { a, b ->
                a + b
            }
        }
    }

    fun Int.countingList(offset: Int = 0): List<Int> {
        return this.filledList { it + offset }
    }

    inline fun <reified T> Int.filledList(value: T): List<T> {
        return this.filledList { value }
    }

    inline fun <reified T> Int.filledList(value: (Int) -> T): List<T> {
        return List(this) { value(it) }
    }

    fun <T> List<T>.countOf(elem: T): Int {
        return this.count { it == elem }
    }

    fun <T> List<T>.takeWithTail(n: Int): Pair<List<T>, List<T>> {
        return this.take(n) to this.drop(n)
    }

    fun <T> List<T>.headWithTail(): Pair<T, List<T>> {
        return this.first() to this.drop(1)
    }

    fun <T> List<T>.headOrNullWithTail(): Pair<T?, List<T>> {
        return this.firstOrNull() to this.drop(1)
    }

    fun <T> Collection<T>.findByMnemonic(mnemonic: String): List<T> {
        return this.findByMnemonic(mnemonic) { toString() }
    }

    fun <T> Collection<T>.findByMnemonic(mnemonic: String, mapping: T.() -> String): List<T> {
        return this.filter { it.mapping().startsWith(mnemonic) }
    }

    fun <T> Collection<T>.mnemonic(value: T): String {
        return this.mnemonic(value) { toString() }
    }

    fun <T> Collection<T>.mnemonic(value: T, mapping: T.() -> String): String {
        val representation = value.mapping()

        for (i in 1 until representation.length) {
            val candidatePrefix = representation.substring(0, i)

            if (this.findByMnemonic(candidatePrefix, mapping).size == 1) {
                return candidatePrefix
            }
        }

        return representation
    }

    infix fun <T, V> Collection<T>.allTo(v: V): List<Pair<T, V>> {
        return this.map { it to v }
    }

    infix fun <T, V> T.toEach(collect: Collection<V>): List<Pair<T, V>> {
        return collect.map { this to it }
    }

    // TODO use library instead?
    fun <T> Collection<T>.powerset(): Set<Set<T>> = powerset(this, setOf(setOf()))

    private tailrec fun <T> powerset(left: Collection<T>, acc: Set<Set<T>>): Set<Set<T>> = when {
        left.isEmpty() -> acc
        else -> powerset(left.drop(1), acc + acc.map { it + left.first() })
    }

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

    fun <T> Map<T, Set<T>>.topologicalSort(): List<T> {
        // remove self-dependencies
        val data = this.mapValues { it.value - it.key }.toMutableMap()

        // explicitly add empty sets (no dependencies) for all items that only occur as passive dependencies
        val extraItemsInDeps = data.values.flatten() - data.keys
        data += extraItemsInDeps.associateWith { emptySet<T>() }

        // result accumulator
        val resGroups = mutableListOf<T>()

        while (data.values.any { it.isEmpty() }) {
            val noRemainingDependencies = data.filterValues { it.isEmpty() }.keys

            resGroups += noRemainingDependencies

            data.keys.removeAll(noRemainingDependencies)
            data.entries.forEach { it.setValue(it.value - noRemainingDependencies) }
        }

        if (data.isNotEmpty()) {
            throw Exception("A cyclic dependency exists amongst: ${data.toList().joinToString()}")
        }

        return resGroups
    }
}