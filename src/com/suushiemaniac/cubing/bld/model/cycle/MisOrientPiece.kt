package com.suushiemaniac.cubing.bld.model.cycle

open class MisOrientPiece(val target: Int, override val orientation: Int) : MisOrientCycle {
    override val buffer: Int = this.target

    override fun getAllTargets(): List<Int> {
        return listOf(this.target)
    }
}