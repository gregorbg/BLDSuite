package com.suushiemaniac.cubing.bld.model.enumeration.piece

import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.alglib.lang.PyraminxAlgorithmReader

enum class TetrahedronPieceType(override val targetsPerPiece: Int, override val numPieces: Int, override val numAlgs: Int, override val humanName: String, override val mnemonic: String) : PieceType {
    TIP(3, 4, 0, "Tip", "T"),
    CENTER(3, 4, 0, "Center", "C"),
    EDGE(2, 6, 80, "Edge", "E");

    override val reader: NotationReader
        get() = READER

    companion object {
        val READER: NotationReader = PyraminxAlgorithmReader()
    }
}
