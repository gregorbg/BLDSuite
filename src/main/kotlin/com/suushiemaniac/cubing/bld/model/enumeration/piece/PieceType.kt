package com.suushiemaniac.cubing.bld.model.enumeration.piece

import com.suushiemaniac.cubing.alglib.lang.NotationReader

interface PieceType {
    val targetsPerPiece: Int

    val numPieces: Int

    val numPiecesNoBuffer: Int
        get() = this.numPieces - 1

    val numTargets: Int
        get() = this.numPieces * this.targetsPerPiece

    val maxTargets: Int
        get() = this.numPiecesNoBuffer / 2 * 3 + this.numPiecesNoBuffer % 2

    val permutations: Int
        get() = this.numPieces

    val orientations: Int
        get() = this.targetsPerPiece

    val numAlgs: Int

    val reader: NotationReader

    val name: String

    val humanName: String

    val mnemonic: String
}
