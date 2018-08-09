package com.suushiemaniac.cubing.bld.model.enumeration.piece

import com.suushiemaniac.cubing.alglib.lang.MegaminxAlgorithmReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader

enum class DodecahedronPieceType(override val targetsPerPiece: Int, override val numPieces: Int, override val numAlgs: Int, override val humanName: String, override val mnemonic: String) : PieceType {
    CORNER(3, 20, 3078, "Corner", "C"),
    EDGE(2, 30, 3248, "Edge", "E"),
    CENTER(1, 12, 0, "Center", "Ce");

    override val reader: NotationReader
        get() = DodecahedronPieceType.READER

    companion object {
        val READER: NotationReader = MegaminxAlgorithmReader()
    }
}
