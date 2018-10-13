package com.suushiemaniac.cubing.bld.model.enumeration.puzzle

import com.suushiemaniac.cubing.alglib.lang.MegaminxAlgorithmReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.analyze.BldCube
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import net.gnehzr.tnoodle.scrambles.Puzzle
import puzzle.CubePuzzle
import puzzle.MegaminxPuzzle

import com.suushiemaniac.cubing.bld.model.enumeration.piece.DodecahedronPieceType.*
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType

enum class DodecahedronPuzzle(override val size: Int, private val scramblingPuzzleGen: () -> Puzzle, vararg types: PieceType) : TwistyPuzzle { //TODO correct puzzle implementations
    KILO(2, ::MegaminxPuzzle, CORNER),
    MEGA(3, { CubePuzzle(2) }, CORNER, CENTER, EDGE),
    MASTERKILO(4, { CubePuzzle(2) }, CORNER, CENTER, EDGE),
    GIGA(5, { CubePuzzle(2) }, CORNER, CENTER, EDGE);

    override val pieceTypes: Array<out PieceType> = types

    override val analyzingPuzzle: BldPuzzle
        get() = BldCube(CubicPuzzle.THREE) // FIXME

    override val reader: NotationReader
        get() = READER_INST

    override fun supplyScramblingPuzzle(): () -> Puzzle {
        return this.scramblingPuzzleGen
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

        private val READER_INST = MegaminxAlgorithmReader()
    }
}
