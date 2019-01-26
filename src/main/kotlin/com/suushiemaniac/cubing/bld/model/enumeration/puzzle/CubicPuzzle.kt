package com.suushiemaniac.cubing.bld.model.enumeration.puzzle

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.util.StringUtil.repeatWithGap
import net.gnehzr.tnoodle.scrambles.Puzzle
import puzzle.*

import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*
import java.io.File

enum class CubicPuzzle(override val size: Int, val scramblingPuzzleGen: () -> Puzzle, vararg types: PieceType) : TwistyPuzzle {
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

    constructor(size: Int, scramblingPuzzleGen: (Int) -> Puzzle, vararg types: PieceType) : this(size, { -> scramblingPuzzleGen(size) }, *types)
    constructor(scramblingPuzzleGen: () -> Puzzle, parent: CubicPuzzle) : this(parent.size, scramblingPuzzleGen, *parent.pieceTypes)

    override val pieceTypes: Array<out PieceType> = types

    override val reader: NotationReader
        get() = READER_INST

    override val kPuzzle: KPuzzle // FIXME not load a new kPuzzle every time at get
        get() = KPuzzle(File(this.javaClass.getResource("permutations/${this.size.toString().repeat(3)}.def").toURI()))

    override val scramblingPuzzle: Puzzle = this.scramblingPuzzleGen()

    override fun independentScrambleSupplier(): Sequence<Algorithm> {
        val independentTNoodle = this.scramblingPuzzleGen()
        val independentReader = getReader()

        return generateSequence {
            independentReader.parse(independentTNoodle.generateScramble())
        }
    }

    override fun toString(): String {
        return this.size.toString().repeatWithGap(3, "x")
    }

    companion object {
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

        fun getReader(): NotationReader {
            return CubicAlgorithmReader()
        }

        private val READER_INST = getReader()
    }
}
