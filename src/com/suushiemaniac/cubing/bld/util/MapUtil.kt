package com.suushiemaniac.cubing.bld.util

object MapUtil {
    fun <K : Comparable<K>, V> Map<K, V>.sortedPrint() {
        for (i in this.keys.sorted()) {
            println("""$i: ${this[i]}""")
        }
    }

    fun <T : Number> Map<Int, T>.freqAverage(): Float {
        val criteriaHit = this.values.map { it.toInt() }.sum()
        val sum = this.entries.fold(0f) {acc, entry -> acc + (entry.key * entry.value.toFloat()) }

        return sum / criteriaHit
    }

    infix fun <K, V> Collection<K>.constantlyTo(value: () -> V): Map<K, V> {
        return mapOf(*this.map { it to value() }.toTypedArray())
    }

    fun <K> MutableMap<K, Int>.increment(key: K) {
        this[key] = this.getOrDefault(key, 0) + 1
    }
}
