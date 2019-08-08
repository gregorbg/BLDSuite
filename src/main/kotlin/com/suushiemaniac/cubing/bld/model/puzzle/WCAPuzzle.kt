package com.suushiemaniac.cubing.bld.model.puzzle

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import net.gnehzr.tnoodle.scrambles.Puzzle
import net.gnehzr.tnoodle.puzzle.*

enum class WCAPuzzle(override val kTag: String, val parser: () -> NotationReader, val scrambler: () -> Puzzle) : TwistyPuzzle {
    TWO("222", ::CubicAlgorithmReader, ::TwoByTwoCubePuzzle),
    THREE("333", ::CubicAlgorithmReader, ::ThreeByThreeCubePuzzle),
    THREE_BLD(THREE, ::NoInspectionThreeByThreeCubePuzzle),
    THREE_FMC(THREE, ::ThreeByThreeCubeFewestMovesPuzzle),
    FOUR("444", ::CubicAlgorithmReader, ::FourByFourCubePuzzle),
    FOUR_BLD(FOUR, ::NoInspectionFourByFourCubePuzzle),
    FOUR_RANDOM(FOUR, ::FourByFourRandomTurnsCubePuzzle),
    FIVE("555", ::CubicAlgorithmReader, { CubePuzzle(5) }),
    FIVE_BLD(FIVE, ::NoInspectionFiveByFiveCubePuzzle),
    SIX("666", ::CubicAlgorithmReader, { CubePuzzle(6) }),
    SEVEN("777", ::CubicAlgorithmReader, { CubePuzzle(7) });

    constructor(parent: WCAPuzzle, scrambler: () -> Puzzle) : this(parent.kTag, parent.parser, scrambler)

    override val tPuzzleSupply = this.scrambler
    override val tPuzzle = this.scrambler()

    override val antlrReaderSupply = this.parser
    override val antlrReader = this.parser()
}