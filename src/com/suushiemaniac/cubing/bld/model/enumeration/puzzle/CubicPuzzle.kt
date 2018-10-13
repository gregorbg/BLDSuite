package com.suushiemaniac.cubing.bld.model.enumeration.puzzle

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.analyze.*
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import net.gnehzr.tnoodle.scrambles.Puzzle
import puzzle.*

import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*

enum class CubicPuzzle(override val size: Int, private val scramblingPuzzleGen: () -> Puzzle, vararg types: PieceType) : TwistyPuzzle {
    TWO(2, ::TwoByTwoCubePuzzle, CORNER),
    THREE(3, ::ThreeByThreeCubePuzzle, CORNER, CENTER, EDGE),
    THREE_BLD(::NoInspectionThreeByThreeCubePuzzle, THREE),
    THREE_FMC(::ThreeByThreeCubeFewestMovesPuzzle, THREE),
    FOUR(4, ::FourByFourCubePuzzle, CORNER, XCENTER, WING),
    FOUR_RAND(::FourByFourRandomTurnsCubePuzzle, FOUR),
    FOUR_BLD(::NoInspectionFourByFourCubePuzzle, FOUR),
    FIVE(5, ::CubePuzzle, CORNER, CENTER, EDGE, XCENTER, TCENTER, WING),
    FIVE_BLD(::NoInspectionFiveByFiveCubePuzzle, FIVE),
    SIX(6, ::CubePuzzle, CORNER, XCENTER, WING, INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE),
    SEVEN(7, ::CubePuzzle, CORNER, CENTER, EDGE, XCENTER, TCENTER, WING, INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE, INNERTCENTER);

    override val pieceTypes: Array<out PieceType> = types

    override val analyzingPuzzle: BldPuzzle
        get() = BldCube(this)

    override val reader: NotationReader
        get() = READER_INST

    constructor(size: Int, scramblingPuzzleGen: (Int) -> Puzzle, vararg types: PieceType) : this(size, { -> scramblingPuzzleGen(size) }, *types)

    constructor(scramblingPuzzleGen: () -> Puzzle, parent: CubicPuzzle) : this(parent.size, scramblingPuzzleGen, *parent.pieceTypes)

    override fun supplyScramblingPuzzle(): () -> Puzzle {
        return this.scramblingPuzzleGen
    }

    override fun toString(): String {
        return List(3) { this.size.toString() }.joinToString("x")
    }

    companion object {
        @JvmOverloads
        fun fromSize(size: Int, flag: String = ""): CubicPuzzle {
            for (puzzle in values()) {
                if (puzzle.size == size) {
                    if (flag.isEmpty()) {
                        if (!puzzle.name.contains("_")) {
                            return puzzle
                        }
                    } else if (puzzle.name.toUpperCase().endsWith(flag.toUpperCase())) {
                        return puzzle
                    }
                }
            }

            return if (size in 2..7) {
                fromSize(size)
            } else THREE

        }

        private val READER_INST = CubicAlgorithmReader()
    }
}
