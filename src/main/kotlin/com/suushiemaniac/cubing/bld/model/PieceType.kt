package com.suushiemaniac.cubing.bld.model

data class PieceType(val name: String, val permutations: Int, val orientations: Int) {
    val permutationsNoBuffer: Int
        get() = this.permutations - 1

    val numTargets: Int
        get() = this.permutations * this.orientations

    val humanName: String
        get() = this.name.split("_").joinToString("") { it.toLowerCase().capitalize() }
}
