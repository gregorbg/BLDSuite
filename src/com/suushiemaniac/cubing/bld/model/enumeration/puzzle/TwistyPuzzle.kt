package com.suushiemaniac.cubing.bld.model.enumeration.puzzle

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import net.gnehzr.tnoodle.scrambles.Puzzle

interface TwistyPuzzle {
    val size: Int

    val analyzingPuzzle: BldPuzzle

    val pieceTypes: Array<out PieceType>

    val reader: NotationReader

    val scramblingPuzzle: Puzzle
        get() = this.supplyScramblingPuzzle()()

    val scrambleAnalysis: BldPuzzle
        get() = this.getScrambleAnalysis(this.generateRandomScramble())

    fun supplyScramblingPuzzle(): () -> Puzzle

    fun generateRandomScramble(): Algorithm {
        return this.reader.parse(this.scramblingPuzzle.generateScramble())
    }

    fun getScrambleAnalysis(scramble: Algorithm): BldPuzzle {
        val analysis = this.analyzingPuzzle
        analysis.parseScramble(scramble)

        return analysis
    }
}
