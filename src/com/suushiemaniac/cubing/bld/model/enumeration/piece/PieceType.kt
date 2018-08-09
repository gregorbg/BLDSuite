package com.suushiemaniac.cubing.bld.model.enumeration.piece

import com.suushiemaniac.cubing.alglib.lang.NotationReader

interface PieceType {
    val targetsPerPiece: Int

    val numPieces: Int

    val numPiecesNoBuffer: Int
        get() = this.numPieces - 1

    val numAlgs: Int

    val reader: NotationReader

    val name: String

    val humanName: String

    val mnemonic: String
}
