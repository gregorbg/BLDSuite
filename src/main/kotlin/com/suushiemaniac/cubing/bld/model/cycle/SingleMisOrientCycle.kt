package com.suushiemaniac.cubing.bld.model.cycle

class SingleMisOrientCycle(override val buffer: Int, override val orientation: Int, val first: Int, val second: Int) : MisOrientCycle {
    override val targetCount: Int = 2

    override fun getAllTargets(): List<Int> {
        return listOf(this.first, this.second)
    }
}