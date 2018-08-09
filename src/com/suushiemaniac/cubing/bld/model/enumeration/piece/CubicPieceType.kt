package com.suushiemaniac.cubing.bld.model.enumeration.piece

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader

enum class CubicPieceType(override val targetsPerPiece: Int, override val numPieces: Int, override val numAlgs: Int, override val humanName: String, override val mnemonic: String) : PieceType {
    CENTER(1, 6, 0, "Center", "Ce"),
    CORNER(3, 8, 378, "Corner", "C"),
    EDGE(2, 12, 440, "Edge", "E"),
    XCENTER(1, 24, 460, "X-Center", "XCe"),
    WING(1, 24, 506, "Wing", "Wi"),
    TCENTER(1, 24, 460, "T-Center", "TCe"),
    INNERXCENTER(1, 24, 460, "Inner X-Center", "iXCe"),
    INNERWING(1, 24, 506, "Inner Wing", "iWi"),
    LEFTOBLIQUE(1, 24, 460, "Left Oblique", "LOb"),
    RIGHTOBLIQUE(1, 24, 460, "Right Oblique", "ROb"),
    INNERTCENTER(1, 24, 460, "Inner T-Center", "iTCe");

    override val reader: NotationReader
        get() = READER

    companion object {
        private val READER = CubicAlgorithmReader()
    }
}