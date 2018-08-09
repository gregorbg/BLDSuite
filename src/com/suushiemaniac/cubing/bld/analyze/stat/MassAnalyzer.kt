package com.suushiemaniac.cubing.bld.analyze.stat

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType

import com.suushiemaniac.cubing.bld.util.MapUtil.increment
import com.suushiemaniac.cubing.bld.util.MapUtil.sortedPrint
import com.suushiemaniac.cubing.bld.util.MapUtil.freqAverage

class MassAnalyzer(protected var analyze: BldPuzzle) {
    fun analyzeProperties(scrambles: List<Algorithm>) {
        val parityCounts = mutableMapOf<PieceType, Int>()

        val solvedBufferCounts = mutableMapOf<PieceType, Int>()

        val targets = mutableMapOf<PieceType, MutableMap<Int, Int>>()
        val breakIns = mutableMapOf<PieceType, MutableMap<Int, Int>>()
        val preSolved = mutableMapOf<PieceType, MutableMap<Int, Int>>()
        val misOriented = mutableMapOf<PieceType, MutableMap<Int, Int>>()

        for (scramble in scrambles) {
            this.analyze.parseScramble(scramble)

            for (type in this.analyze.pieceTypes) {
                if (this.analyze.hasParity(type)) {
                    parityCounts.increment(type)
                }

                if (this.analyze.isBufferSolved(type)) {
                    solvedBufferCounts.increment(type)
                }

                targets.computeIfAbsent(type) { mutableMapOf() }.increment(this.analyze.getStatLength(type))
                breakIns.computeIfAbsent(type) { mutableMapOf() }.increment(this.analyze.getBreakInCount(type))
                preSolved.computeIfAbsent(type) { mutableMapOf() }.increment(this.analyze.getPreSolvedCount(type))
                misOriented.computeIfAbsent(type) { mutableMapOf() }.increment(this.analyze.getMisOrientedCount(type))
            }
        }

        val numCubes = scrambles.size

        println("Total scrambles: $numCubes")

        for (type in this.analyze.pieceTypes) {
            println()
            println("Parity: " + parityCounts[type])
            println("Average: " + parityCounts[type]!! / numCubes.toFloat())

            println()
            println("Buffer preSolved: " + solvedBufferCounts[type])
            println("Average: " + solvedBufferCounts[type]!! / numCubes.toFloat())

            println()
            println(type.humanName + " targets")
            targets[type]?.sortedPrint()
            println("Average: " + targets[type]?.freqAverage())

            println()
            println(type.humanName + " break-ins")
            breakIns[type]?.sortedPrint()
            println("Average: " + breakIns[type]?.freqAverage())

            println()
            println(type.humanName + " pre-solved")
            preSolved[type]?.sortedPrint()
            println("Average: " + preSolved[type]?.freqAverage())

            println()
            println(type.humanName + " mis-oriented")
            misOriented[type]?.sortedPrint()
            println("Average: " + misOriented[type]?.freqAverage())
        }
    }

    fun analyzeProperties(numCubes: Int) {
        this.analyzeProperties(this.generateRandom(numCubes))
    }

    fun analyzeScrambleDist(scrambles: List<Algorithm>) {
        val pieceTypeMap = mutableMapOf<PieceType, MutableMap<String, Int>>()

        val overall = mutableMapOf<String, Int>()

        for (scramble in scrambles) {
            this.analyze.parseScramble(scramble)

            for (type in this.analyze.pieceTypes) {
                pieceTypeMap.computeIfAbsent(type) { mutableMapOf() }.increment(this.analyze.getStatString(type))
            }

            overall.increment(this.analyze.statString)
        }

        for (type in this.analyze.pieceTypes) {
            println()
            println(type.humanName)

            pieceTypeMap[type]?.sortedPrint()
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
            this.analyze.parseScramble(scramble)

            for (type in this.analyze.pieceTypes) {
                if (this.analyze.getStatLength(type) > 0) {
                    val cornerPairs = this.analyze.getSolutionPairs(type)
                            .replace((if (singleLetter) "\\s+?" else "$.").toRegex(), "")
                            .split((if (singleLetter) "" else "\\s+?").toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()

                    for (pair in cornerPairs) {
                        pieceTypeMap.computeIfAbsent(type) { mutableMapOf() }.increment(pair)
                    }
                }
            }
        }

        for (type in this.analyze.pieceTypes) {
            println()
            println(type.humanName)

            pieceTypeMap[type]?.sortedPrint()
        }
    }

    fun analyzeLetterPairs(numCubes: Int, singleLetter: Boolean) {
        this.analyzeLetterPairs(this.generateRandom(numCubes), singleLetter)
    }

    fun analyzeLetterPairs(numCubes: Int) {
        this.analyzeLetterPairs(numCubes, false)
    }

    fun generateRandom(numCubes: Int): List<Algorithm> {
        val tNoodle = this.analyze.model.scramblingPuzzle
        val reader = this.analyze.model.reader

        val scrambles = ArrayList<Algorithm>()

        for (i in 0 until numCubes) {
            if (i % (numCubes / Math.min(100, numCubes)) == 0) {
                println("Cube $i")
            }

            val rawScramble = tNoodle.generateScramble()
            val scramble = reader.parse(rawScramble)

            scrambles.add(scramble)
        }

        return scrambles
    }
}
