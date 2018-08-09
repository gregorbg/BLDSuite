package com.suushiemaniac.cubing.bld.filter

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.Companion.MAYBE
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.Companion.NO
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.Companion.YES
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition.Companion.EXACT
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition.Companion.MAX
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition.Companion.MIN
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import net.gnehzr.tnoodle.scrambles.Puzzle

import java.util.*

class BldScramble(var analyzingPuzzle: BldPuzzle, var conditions: List<ConditionsBundle>) {
    protected var scramblingSupplier: () -> Puzzle = analyzingPuzzle.model.supplyScramblingPuzzle()

    val statString: String
        get() = this.conditions.joinToString(" | ") { it.statString }

    fun supplyScramblingPuzzle(): () -> Puzzle {
        return this.scramblingSupplier
    }

    fun generateScramblingPuzzle(): Puzzle {
        return this.supplyScramblingPuzzle()()
    }

    fun findScrambleOnThread(): Algorithm {
        val testCube = this.analyzingPuzzle
        val tNoodle = this.generateScramblingPuzzle()
        val reader = CubicAlgorithmReader()

        do {
            val scrString = tNoodle.generateScramble()
            val scramble = reader.parse(scrString)
            testCube.parseScramble(scramble)
        } while (!this.matchingConditions(testCube))

        return testCube.scramble
    }

    fun findScramblesOnThread(num: Int): List<Algorithm> {
        val scrambles = ArrayList<Algorithm>()

        for (i in 0 until num) {
            if (i % (num / Math.min(100, num)) == 0) {
                println(i)
            }

            scrambles.add(this.findScrambleOnThread())
        }

        return scrambles
    }

    fun findScramblesThreadModel(numScrambles: Int, feedbackFunction: (Int) -> Unit = {}): List<Algorithm> {
        val numThreads = Runtime.getRuntime().availableProcessors() + 1
        val scrambleQueue = Channel<Algorithm>(numScrambles * numThreads * numThreads)

        for (i in 0 until numThreads) {
            launch {
                val reader = CubicAlgorithmReader()

                while (true) {
                    val algorithm = reader.parse(generateScramblingPuzzle().generateScramble())
                    scrambleQueue.send(algorithm)
                }
            }
        }

        return runBlocking {
            val algList = mutableListOf<Algorithm>()
            val testCube = analyzingPuzzle.clone()

            do {
                testCube.parseScramble(scrambleQueue.receive())

                if (matchingConditions(testCube)) {
                    algList.add(testCube.scramble)
                    feedbackFunction(algList.size)
                }
            } while (algList.size < numScrambles)

            return@runBlocking algList
        }
    }

    protected fun matchingConditions(inCube: BldPuzzle): Boolean {
        for (bundle in this.conditions) {
            if (!bundle.matchingConditions(inCube)) {
                if (SHOW_DISCARDED) {
                    println("Discarded " + inCube.getStatString(true))
                }

                return false
            }
        }

        return true
    }

    override fun toString(): String {
        return this.statString
    }

    companion object {
        protected var SHOW_DISCARDED = false

        fun setShowDiscarded(showDiscarded: Boolean) {
            BldScramble.SHOW_DISCARDED = showDiscarded
        }

        fun cloneFrom(refCube: BldPuzzle, isStrict: Boolean): BldScramble {
            val conditions = ArrayList<ConditionsBundle>()

            for (type in refCube.pieceTypes) {
                val condition = ConditionsBundle(type)

                condition.targets = if (isStrict) EXACT(refCube.getStatLength(type)) else MAX(refCube.getStatLength(type))
                condition.breakIns = if (isStrict) EXACT(refCube.getBreakInCount(type)) else MAX(refCube.getBreakInCount(type))
                condition.parity = if (refCube.hasParity(type)) if (isStrict) YES() else MAYBE() else NO()
                condition.preSolved = if (isStrict) EXACT(refCube.getPreSolvedCount(type)) else MIN(refCube.getPreSolvedCount(type))
                condition.misOriented = if (isStrict) EXACT(refCube.getMisOrientedCount(type)) else MAX(refCube.getMisOrientedCount(type))
                condition.bufferSolved = if (refCube.isBufferSolved(type)) if (isStrict) YES() else MAYBE() else NO()

                condition.isAllowTwistedBuffer = !isStrict || refCube.isBufferSolvedAndMisOriented(type)
                conditions.add(condition)
            }

            return BldScramble(refCube, conditions)
        }

        fun fromStatString(statString: String, refCube: BldPuzzle, isStrict: Boolean): BldScramble {
            val conditions = ArrayList<ConditionsBundle>()

            for (pieceStatString in statString.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val statPattern = "([A-Za-z]+?):(_?)(0|[1-9][0-9]*)(\\*?)(#*)(~*)(\\+*)".toPattern()
                val statMatcher = statPattern.matcher(pieceStatString.replace("\\s".toRegex(), ""))

                if (statMatcher.find()) {
                    val mnemonic = statMatcher.group(1)
                    val type = findTypeByMnemonic(refCube, mnemonic)

                    val condition = ConditionsBundle(type!!)

                    val targets = Integer.parseInt(statMatcher.group(3))
                    val breakIns = statMatcher.group(5).length
                    val hasParity = statMatcher.group(2).isNotEmpty()
                    val preSolved = statMatcher.group(7).length
                    val misOriented = statMatcher.group(6).length
                    val bufferSolved = statMatcher.group(4).isNotEmpty()

                    condition.targets = if (isStrict) EXACT(targets) else MAX(targets)
                    condition.breakIns = if (isStrict) EXACT(breakIns) else MAX(breakIns)
                    condition.parity = if (hasParity) if (isStrict) YES() else MAYBE() else NO()
                    condition.preSolved = if (isStrict) EXACT(preSolved) else MIN(preSolved)
                    condition.misOriented = if (isStrict) EXACT(misOriented) else MAX(misOriented)
                    condition.bufferSolved = if (bufferSolved) if (isStrict) YES() else MAYBE() else NO()

                    conditions.add(condition)
                }
            }

            return BldScramble(refCube, conditions)
        }

        protected fun findTypeByMnemonic(refCube: BldPuzzle, mnemonic: String): PieceType? {
            for (type in refCube.pieceTypes) {
                if (type.mnemonic.equals(mnemonic, true)) {
                    return type
                }
            }

            return null
        }
    }
}