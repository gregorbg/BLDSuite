package com.suushiemaniac.cubing.bld.model.cycle

class ThreeCycle(override val buffer: Int, val first: Int, val second: Int) : PieceCycle {
    override val targetCount: Int = 2

    override fun getAllTargets(): List<Int> {
        return listOf(this.first, this.second)
    }
}