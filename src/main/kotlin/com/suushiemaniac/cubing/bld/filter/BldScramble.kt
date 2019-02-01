package com.suushiemaniac.cubing.bld.filter

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.analyze.BldAnalysis
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.Companion.MAYBE
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.Companion.NO
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.Companion.YES
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition.Companion.EXACT
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition.Companion.MAX
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition.Companion.MIN
import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class BldScramble(val puzzle: TwistyPuzzle, config: File, vararg val conditions: ConditionsBundle) {
    val analyzer = this.puzzle.gPuzzle(config)

    val statString: String
        get() = this.conditions.joinToString(" | ") { it.statString }

    val scrambleSupplier: Sequence<Algorithm>
        get() = generateSequence(this.puzzle::randomScramble)

    fun balanceConditions() {
        this.conditions.forEach(ConditionsBundle::balanceProperties)
    }

    fun findScrambleOnThread(): Algorithm {
        this.balanceConditions()

        return this.scrambleSupplier.find(this::matchingConditions)!!
    }

    fun findScramblesOnThread(numScrambles: Int): List<Algorithm> {
        return List(numScrambles) { this.findScrambleOnThread() }
    }

    fun findScramblesThreadModel(numScrambles: Int): List<Algorithm> {
        val numThreads = Runtime.getRuntime().availableProcessors() + 1
        val scrambleQueue = Channel<Algorithm>(Channel.UNLIMITED)

        for (i in 0 until numThreads) {
            GlobalScope.launch {
                for (scramble in scrambleSupplier) {
                    scrambleQueue.send(scramble)
                }
            }
        }

        this.balanceConditions()

        return runBlocking {
            scrambleQueue.filter { matchingConditions(it) }.take(numScrambles).toList()
        }
    }

    fun matchingConditions(scramble: Algorithm): Boolean {
        val analysis = this.analyzer.getAnalysis(scramble)

        for (bundle in this.conditions) {
            if (!bundle.matchingConditions(analysis)) {
                if (SHOW_DISCARDED) {
                    println("Discarded " + analysis.getStatString())
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
        private var SHOW_DISCARDED = false

        fun setShowDiscarded(showDiscarded: Boolean) {
            BldScramble.SHOW_DISCARDED = showDiscarded
        }

        // TODO for the below two cloning methods: cleverly "infer" where this gConfig comes from?

        fun cloneFrom(analysis: BldAnalysis, puzzle: TwistyPuzzle, config: File, isStrict: Boolean = false): BldScramble {
            val conditions = mutableListOf<ConditionsBundle>()

            for (type in analysis.pieceTypes) {
                val condition = ConditionsBundle(type)

                condition.targets = if (isStrict) EXACT(analysis.getTargetCount(type)) else MAX(analysis.getTargetCount(type))
                condition.breakIns = if (isStrict) EXACT(analysis.getBreakInCount(type)) else MAX(analysis.getBreakInCount(type))
                condition.parity = if (analysis.hasParity(type)) if (isStrict) YES() else MAYBE() else NO()
                condition.preSolved = if (isStrict) EXACT(analysis.getPreSolvedCount(type)) else MIN(analysis.getPreSolvedCount(type))
                condition.misOriented = if (isStrict) EXACT(analysis.getMisOrientedCount(type)) else MAX(analysis.getMisOrientedCount(type))
                condition.bufferSolved = if (analysis.isBufferSolved(type)) if (isStrict) YES() else MAYBE() else NO()

                condition.isAllowTwistedBuffer = !isStrict || analysis.isBufferSolvedAndMisOriented(type)

                conditions.add(condition)
            }

            return BldScramble(puzzle, config, *conditions.toTypedArray())
        }

        fun fromStatString(statString: String, puzzle: TwistyPuzzle, config: File, isStrict: Boolean = false): BldScramble {
            val conditions = mutableListOf<ConditionsBundle>()

            for (pieceStatString in statString.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }) {
                "([A-Za-z]+?):(_?)(0|[1-9][0-9]*)(\\*?)(#*)(~*)(\\+*)".toRegex()
                        .matchEntire(pieceStatString.replace("\\s".toRegex(), ""))?.let {
                    val mnemonic = it.groupValues[1]
                    val type = puzzle.kPuzzle.pieceTypes.find { type -> type.humanName.equals(mnemonic, true) }

                    val condition = ConditionsBundle(type!!)

                    val targets = it.groupValues[3].toInt()
                    val breakIns = it.groupValues[5].length
                    val hasParity = it.groupValues[2].isNotEmpty()
                    val preSolved = it.groupValues[7].length
                    val misOriented = it.groupValues[6].length
                    val bufferSolved = it.groupValues[4].isNotEmpty()

                    condition.targets = if (isStrict) EXACT(targets) else MAX(targets)
                    condition.breakIns = if (isStrict) EXACT(breakIns) else MAX(breakIns)
                    condition.parity = if (hasParity) if (isStrict) YES() else MAYBE() else NO()
                    condition.preSolved = if (isStrict) EXACT(preSolved) else MIN(preSolved)
                    condition.misOriented = if (isStrict) EXACT(misOriented) else MAX(misOriented)
                    condition.bufferSolved = if (bufferSolved) if (isStrict) YES() else MAYBE() else NO()

                    conditions.add(condition)
                }
            }

            return BldScramble(puzzle, config, *conditions.toTypedArray())
        }
    }
}