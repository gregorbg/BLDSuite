package com.suushiemaniac.cubing.bld.model.enumeration.piece

import com.suushiemaniac.cubing.alglib.lang.ImageStringReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader

enum class LetterPairImage(private val token: String) : PieceType {
    ANY("<any>"), NOUN("NN"), VERB("VV"), ADJECTIVE("ADJ");

    override val targetsPerPiece: Int
        get() = 1

    override val numPieces: Int
        get() = 26

    override val numAlgs: Int
        get() = 1

    override val reader: NotationReader
        get() = ImageStringReader()

    override val humanName: String
        get() = "LPI"

    override val mnemonic: String
        get() = this.token

    override fun toString(): String {
        return this.mnemonic
    }

    companion object {
        fun fromToken(token: String): LetterPairImage? {
            for (img in values()) {
                if (img.mnemonic == token) {
                    return img
                }
            }

            return null
        }
    }
}
