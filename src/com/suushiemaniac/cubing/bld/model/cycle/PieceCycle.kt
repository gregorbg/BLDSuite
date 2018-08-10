package com.suushiemaniac.cubing.bld.model.cycle

interface PieceCycle {
    val buffer: Int

    fun getAllTargets(): List<Int>
}