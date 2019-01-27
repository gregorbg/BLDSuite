package com.suushiemaniac.cubing.bld.model

data class PieceType(val name: String, val permutations: Int, val orientations: Int) {
    val permutationsNoBuffer: Int
        get() = this.permutations - 1

    val numTargets: Int
        get() = this.permutations * this.orientations

    val maxTargets: Int
        get() = this.permutationsNoBuffer / 2 * 3 + this.permutationsNoBuffer % 2

    val humanName: String
        get() = this.name.capitalize() // TODO clever uppercasing, InnerWing is better than Innerwing
}
