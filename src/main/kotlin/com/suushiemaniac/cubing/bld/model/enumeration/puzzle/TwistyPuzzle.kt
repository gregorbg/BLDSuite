package com.suushiemaniac.cubing.bld.model.enumeration.puzzle

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import net.gnehzr.tnoodle.scrambles.Puzzle
import java.io.File

interface TwistyPuzzle {
    val size: Int

    val pieceTypes: Array<out PieceType>

    val kPuzzle: KPuzzle

    fun gPuzzle(gConfig: File) = this.kPuzzle.g(gConfig)

    val reader: NotationReader

    val scramblingPuzzle: Puzzle

    fun independentScrambleSupplier(): Sequence<Algorithm>

    val randomScramble: Algorithm
        get() = this.reader.parse(this.scramblingPuzzle.generateScramble())
}
