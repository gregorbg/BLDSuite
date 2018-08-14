package com.suushiemaniac.cubing.bld.util

object MapUtil {
    fun <K : Comparable<K>, V> Map<K, V>.sortedPrint() {
        for (i in this.keys.sorted()) {
            println("$i: ${this[i]}")
        }
    }

    fun <T : Number> Map<T, Int>.freqAverage(): Double {
        val criteriaHit = this.values.sum()
        val sum = this.entries.sumByDouble { it.key.toDouble() * it.value }

        return sum / criteriaHit
    }

    infix fun <K, V> Collection<K>.allTo(value: (K) -> V): Map<K, V> {
        return this.map { it to value(it) }.toMap()
    }

    infix fun <K, V> Collection<K>.alwaysTo(value: () -> V): Map<K, V> {
        return this.map { it to value() }.toMap()
    }

    infix fun <K, V> Collection<K>.alwaysTo(value: V): Map<K, V> {
        return this.map { it to value }.toMap()
    }

    fun <K, V> MutableMap<K, V>.reset(action: (Map.Entry<K, V>) -> V) {
        for (entry in this) {
            this[entry.key] = action(entry)
        }
    }

    fun <K> MutableMap<K, Int>.increment(key: K) {
        this[key] = this.getOrDefault(key, 0) + 1
    }

    fun <K, V> Map<K, V?>.denullify(): Map<K, V> {
        return this.mapNotNull { (key, nullableVal) ->
            nullableVal?.let { key to it }
        }.toMap()
    }
}
