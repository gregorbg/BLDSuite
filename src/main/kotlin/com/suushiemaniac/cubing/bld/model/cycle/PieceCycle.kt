package com.suushiemaniac.cubing.bld.model.cycle

interface PieceCycle {
    val buffer: Int
    val targetCount: Int

    fun getAllTargets(): List<Int>
}