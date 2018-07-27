package com.suushiemaniac.cubing.bld.util

object BenchmarkUtil {
    fun benchmark(script: () -> Unit): Long {
        val before = System.currentTimeMillis()

        script()

        val after = System.currentTimeMillis()
        return after - before
    }
}
