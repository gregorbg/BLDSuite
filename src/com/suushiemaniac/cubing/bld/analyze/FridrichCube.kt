package com.suushiemaniac.cubing.bld.analyze

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import com.suushiemaniac.cubing.bld.util.BruteForceUtil.permute

class FridrichCube : BldCube {
    val isCrossSolved: Boolean
        get() {
            val sidesMatch = IntRange(1, 5)
                    .all { i -> this.state.getValue(EDGE)[4 * i + 2] / 4 == this.state.getValue(CENTER)[i] }

            val bottomMatches = IntRange(20, 24)
                    .all { i -> this.state.getValue(EDGE)[i] / 4 == this.state.getValue(CENTER)[5] }

            return sidesMatch && bottomMatches
        }

    val isOLLSolved: Boolean
        get() = IntRange(0, 4).all { i ->
            val cornerOkay = this.state.getValue(CORNER)[i] / 4 == this.state.getValue(CENTER)[0]
            val edgeOkay = this.state.getValue(EDGE)[i] / 4 == this.state.getValue(CENTER)[0]

            cornerOkay && edgeOkay
        }

    val isPLLSolved: Boolean
        get() = IntRange(0, 4).all {
            val cornerTopOkay = this.state.getValue(CORNER)[it] / 4 == this.state.getValue(CENTER)[0]

            val cornerFirst = this.state.getValue(CORNER)[it * 4] / 4
            val cornerSecond = this.state.getValue(CORNER)[it * 4 + 1] / 4

            val cornerHeadlightsOkay = cornerFirst == cornerSecond

            val cornerOkay = cornerTopOkay && cornerHeadlightsOkay

            val edgeTopOkay = this.state.getValue(EDGE)[it] / 4 == this.state.getValue(CENTER)[0]

            val edgeSide = this.state.getValue(EDGE)[it * 4] / 4

            val edgeSideOkay = edgeSide == cornerFirst && edgeSide == cornerSecond

            val edgeOkay = edgeTopOkay && edgeSideOkay

            cornerOkay && edgeOkay
        }

    val isFullySolved: Boolean
        get() = IntRange(0, 6).all { i ->
            val cornerOkay = IntRange(0, 4).all { this.state.getValue(CORNER)[i * 4 + it] / 4 == this.state.getValue(CENTER)[i] }
            val edgeOkay = IntRange(0, 4).all { this.state.getValue(EDGE)[i * 4 + it] / 4 == this.state.getValue(CENTER)[i] }

            cornerOkay && edgeOkay
        }

    constructor(model: CubicPuzzle) : super(model)
    constructor(model: CubicPuzzle, scramble: Algorithm) : super(model, scramble)

    override fun parseScramble(scramble: Algorithm) {
        this.parseScramble(scramble, true)
    }

    fun parseScramble(scramble: Algorithm, reset: Boolean) {
        if (reset) {
            this.resetPuzzle()
        }

        this.scramble = scramble
        this.scramblePuzzle(scramble)
    }

    override fun scramblePuzzle(scramble: Algorithm) {
        scramble.filter { this.permutations.keys.contains(it) }.forEach(this::permute)
    }

    fun bruteForceSolve(checkSolved: (FridrichCube) -> Boolean): String {
        if (checkSolved(this)) {
            return "Solved already!"
        }

        val moveSet = this.permutations.keys
                .filter { "UDLRFB".contains(it.plane.toFormatString()) }
                .filter { it.depth == 1 }

        var iter = 1

        val orientation = this.getSolvingOrientationPremoves().inverse()

        while (iter < 6) {
            println("Trying length $iter…")
            val currentPerm = moveSet.permute(iter, false, true)

            for (algorithm in currentPerm) {
                val tempScramble = SimpleAlg(algorithm)
                val insert = SimpleAlg(orientation).merge(tempScramble)

                this.parseScramble(insert, false)

                if (checkSolved(this)) {
                    return tempScramble.toFormatString()
                } else {
                    this.parseScramble(insert.inverse(), false)
                }
            }

            iter++
        }

        return "Not found!"
    }

    fun solveCross(): String {
        return this.bruteForceSolve { it.isCrossSolved }
    }

    fun isF2LPairSolved(slot: Int): Boolean {
        val mappedSlot = -1 * slot + 3
        val bottomSlot = mappedSlot + 20

        val firstSide = mappedSlot + 1
        val secondSide = mappedSlot + 2

        val cornerBottomMatches = this.state.getValue(CORNER)[bottomSlot] / 4 == this.state.getValue(CENTER)[5]
        val cornerFirstMatches = this.state.getValue(CORNER)[firstSide * 4 + 2] / 4 == this.state.getValue(CENTER)[firstSide]
        val cornerSecondMatches = this.state.getValue(CORNER)[secondSide * 4 + 3] / 4 == this.state.getValue(CENTER)[secondSide]

        val cornerMatches = cornerBottomMatches && cornerFirstMatches && cornerSecondMatches

        val edgeFirstMatches = this.state.getValue(EDGE)[firstSide * 4 + 1] / 4 == this.state.getValue(CENTER)[firstSide]
        val edgeSecondMatches = this.state.getValue(EDGE)[secondSide * 4 + 3] / 4 == this.state.getValue(CENTER)[secondSide]

        val edgeMatches = edgeFirstMatches && edgeSecondMatches

        return cornerMatches && edgeMatches
    }
}
