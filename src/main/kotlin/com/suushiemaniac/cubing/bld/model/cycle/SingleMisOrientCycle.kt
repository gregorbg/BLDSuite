package com.suushiemaniac.cubing.bld.model.cycle

import com.suushiemaniac.cubing.bld.util.CollectionUtil.randomOrNull

class SingleMisOrientCycle(override val orientation: Int, val first: Int, val second: Int) : MisOrientCycle {
    override val buffer: Int
        get() = this.getAllTargets().randomOrNull() ?: -1

    override val targetCount: Int = 2

    override fun getAllTargets(): List<Int> {
        return listOf(this.first, this.second)
    }
}