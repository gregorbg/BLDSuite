package com.suushiemaniac.cubing.bld.analyze

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.TwistyPuzzle

import com.suushiemaniac.cubing.bld.util.MapUtil.increment
import com.suushiemaniac.cubing.bld.util.MapUtil.sortedPrint
import com.suushiemaniac.cubing.bld.util.MapUtil.freqAverage
import java.io.File

class MassAnalyzer(val model: TwistyPuzzle, gConfig: File) {
    val analyzer = model.gPuzzle(gConfig)

    fun analyzeProperties(scrambles: List<Algorithm>) {
        val parityCounts = mutableMapOf<PieceType, Int>()
        val solvedBufferCounts = mutableMapOf<PieceType, Int>()

        val targets = mutableMapOf<PieceType, MutableMap<Int, Int>>()
        val breakIns = mutableMapOf<PieceType, MutableMap<Int, Int>>()
        val preSolved = mutableMapOf<PieceType, MutableMap<Int, Int>>()
        val misOriented = mutableMapOf<PieceType, MutableMap<Int, Int>>()

        for (scramble in scrambles) {
            val analyze = this.analyzer.getAnalysis(scramble)

            for (type in analyze.pieceTypes) {
                if (analyze.hasParity(type)) {
                    parityCounts.increment(type)
                }

                if (analyze.isBufferSolved(type)) {
                    solvedBufferCounts.increment(type)
                }

                targets.getOrPut(type) { mutableMapOf() }.increment(analyze.getTargetCount(type))
                breakIns.getOrPut(type) { mutableMapOf() }.increment(analyze.getBreakInCount(type))
                preSolved.getOrPut(type) { mutableMapOf() }.increment(analyze.getPreSolvedCount(type))
                misOriented.getOrPut(type) { mutableMapOf() }.increment(analyze.getMisOrientedCount(type))
            }
        }

        val numCubes = scrambles.size

        println("Total scrambles: $numCubes")

        for (type in this.model.pieceTypes) {
            println()
            println("Parity: " + parityCounts[type])
            println("Average: " + parityCounts.getValue(type) / numCubes.toFloat())

            println()
            println("Buffer preSolved: " + solvedBufferCounts[type])
            println("Average: " + solvedBufferCounts.getValue(type) / numCubes.toFloat())

            println()
            println(type.humanName + " targets")
            targets.getValue(type).sortedPrint()
            println("Average: " + targets.getValue(type).freqAverage())

            println()
            println(type.humanName + " break-ins")
            breakIns.getValue(type).sortedPrint()
            println("Average: " + breakIns.getValue(type).freqAverage())

            println()
            println(type.humanName + " pre-solved")
            preSolved.getValue(type).sortedPrint()
            println("Average: " + preSolved.getValue(type).freqAverage())

            println()
            println(type.humanName + " mis-oriented")
            misOriented.getValue(type).sortedPrint()
            println("Average: " + misOriented.getValue(type).freqAverage())
        }
    }

    fun analyzeProperties(numCubes: Int) {
        this.analyzeProperties(this.generateRandom(numCubes))
    }

    fun analyzeScrambleDist(scrambles: List<Algorithm>) {
        val pieceTypeMap = mutableMapOf<PieceType, MutableMap<String, Int>>()

        val overall = mutableMapOf<String, Int>()

        for (scramble in scrambles) {
            val analyze = this.analyzer.getAnalysis(scramble)

            for (type in analyze.pieceTypes) {
                pieceTypeMap.getOrPut(type) { mutableMapOf() }.increment(analyze.getStatString(type))
            }

            overall.increment(analyze.getStatString())
        }

        for ((type, subMap) in pieceTypeMap.entries) {
            println()
            println(type.humanName)

            subMap.sortedPrint()
        }

        println()
        println("Overall")
        overall.sortedPrint()
    }

    fun analyzeScrambleDist(numCubes: Int) {
        this.analyzeScrambleDist(this.generateRandom(numCubes))
    }

    fun analyzeLetterPairs(scrambles: List<Algorithm>, singleLetter: Boolean) {
        val pieceTypeMap = mutableMapOf<PieceType, MutableMap<String, Int>>()

        for (scramble in scrambles) {
            val analyze = this.analyzer.getAnalysis(scramble)

            for (type in analyze.pieceTypes) {
                if (analyze.getTargetCount(type) > 0) {
                    val solutionPairs = analyze.getSolutionTargets(type) // TODO
                            .replace((if (singleLetter) "\\s+?" else "$.").toRegex(), "")
                            .split((if (singleLetter) "" else "\\s+?").toRegex())
                            .dropLastWhile { it.isEmpty() }

                    for (pair in solutionPairs) {
                        pieceTypeMap.getOrPut(type) { mutableMapOf() }.increment(pair)
                    }
                }
            }
        }

        for ((type, subMap) in pieceTypeMap.entries) {
            println()
            println(type.humanName)

            subMap.sortedPrint()
        }
    }

    fun analyzeLetterPairs(numCubes: Int, singleLetter: Boolean = false) {
        this.analyzeLetterPairs(this.generateRandom(numCubes), singleLetter)
    }

    fun generateRandom(numCubes: Int): List<Algorithm> {
        return List(numCubes) { this.model.randomScramble }
    }
}
