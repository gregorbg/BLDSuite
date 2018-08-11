package com.suushiemaniac.cubing.bld.model.cycle

import com.suushiemaniac.cubing.bld.util.CollectionUtil.random

class ComplexMisOrientCycle(val description: String, vararg val pieces: MisOrientPiece) : MisOrientCycle {
    override val buffer: Int
        get() = this.pieces.asList().random()?.buffer ?: -1

    override val orientation: Int
        get() = this.pieces.asList().random()?.orientation ?: -1

    override fun getAllTargets(): List<Int> {
        return this.pieces.flatMap { it.getAllTargets() }
    }
}