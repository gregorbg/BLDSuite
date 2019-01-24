package com.suushiemaniac.cubing.bld.model.cycle

interface MisOrientCycle : PieceCycle {
    override val targetCount: Int get() = 0

    val orientation: Int
}