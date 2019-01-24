package com.suushiemaniac.cubing.bld.model.enumeration.puzzle

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.MegaminxAlgorithmReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import net.gnehzr.tnoodle.scrambles.Puzzle
import puzzle.MegaminxPuzzle

import com.suushiemaniac.cubing.bld.model.enumeration.piece.DodecahedronPieceType.*
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import puzzle.CubePuzzle
import java.io.File

enum class DodecahedronPuzzle(override val size: Int, private val scramblingPuzzleGen: () -> Puzzle, vararg types: PieceType) : TwistyPuzzle { //TODO correct puzzle implementations
    KILO(2, ::MegaminxPuzzle, CORNER),
    MEGA(3, { CubePuzzle(2) }, CORNER, CENTER, EDGE),
    MASTERKILO(4, { CubePuzzle(2) }, CORNER, CENTER, EDGE),
    GIGA(5, { CubePuzzle(2) }, CORNER, CENTER, EDGE);

    override val pieceTypes: Array<out PieceType> = types

    override val reader: NotationReader
        get() = READER_INST

    override val kPuzzle: KPuzzle = KPuzzle(File("TODO"))

    override val scramblingPuzzle: Puzzle = scramblingPuzzleGen()

    override fun independentScrambleSupplier(): Sequence<Algorithm> {
        val independentTNoodle = this.scramblingPuzzleGen()
        val independentReader = getReader()

        return generateSequence {
            independentReader.parse(independentTNoodle.generateScramble())
        }
    }

    companion object {
        fun fromSize(size: Int): DodecahedronPuzzle {
            for (puzzle in values()) {
                if (puzzle.size == size) {
                    return puzzle
                }
            }

            return MEGA
        }

        fun getReader(): NotationReader = MegaminxAlgorithmReader()

        private val READER_INST = getReader()
    }
}
