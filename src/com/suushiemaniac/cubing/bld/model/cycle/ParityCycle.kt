package com.suushiemaniac.cubing.bld.model.cycle

class ParityCycle(override val buffer: Int, val target: Int) : PieceCycle {
    override fun getAllTargets(): List<Int> {
        return listOf(this.target)
    }
}