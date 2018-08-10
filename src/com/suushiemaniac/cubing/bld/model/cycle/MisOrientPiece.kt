package com.suushiemaniac.cubing.bld.model.cycle

open class MisOrientPiece(val piece: Int, val orientation: Int) : MisOrientCycle {
    override val buffer: Int = this.piece

    override fun getAllTargets(): List<Int> {
        return listOf(this.piece)
    }
}