package com.suushiemaniac.cubing.bld.filter

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.analyze.BldAnalysis
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.Companion.STRICT
import com.suushiemaniac.cubing.bld.filter.condition.IntegerCondition.Companion.STRICT_MAX
import com.suushiemaniac.cubing.bld.filter.condition.IntegerCondition.Companion.STRICT_MIN
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.PieceType.Companion.findByName
import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.util.CollectionUtil.asyncList
import com.suushiemaniac.cubing.bld.util.CollectionUtil.filledList

class BldScramble(val analyzer: GPuzzle, val conditions: List<ConditionsBundle>, val randomScramble: () -> Algorithm) {
    constructor(puzzle: TwistyPuzzle, tag: String, conditions: List<ConditionsBundle>) : this(puzzle.gPuzzle(tag), conditions, puzzle::randomScramble)

    private val scrambleSupplier: Sequence<Algorithm>
        get() = generateSequence(this.randomScramble)

    fun balanceConditions() {
        this.conditions.forEach(ConditionsBundle::balanceProperties)
    }

    fun findScrambleOnThread(): Algorithm {
        this.balanceConditions()

        return this.scrambleSupplier.find(this::matchingConditions)!!
    }

    fun findScramblesOnThread(numScrambles: Int): List<Algorithm> {
        return numScrambles.filledList { this.findScrambleOnThread() }
    }

    fun findScramblesThreadModel(numScrambles: Int): List<Algorithm> {
        return numScrambles.asyncList { this.findScrambleOnThread() }
    }

    fun matchingConditions(scramble: Algorithm): Boolean {
        val analysis = this.analyzer.getAnalysis(scramble)

        return this.conditions.all {
            it.matchingConditions(analysis)
        }
    }

    override fun toString() = this.conditions.joinToString(" | ") { it.statString }

    companion object {
        // TODO for the below two cloning methods: cleverly "infer" where this gConfig comes from?

        fun cloneFrom(analysis: BldAnalysis, puzzle: TwistyPuzzle, isStrict: Boolean = false): BldScramble {
            val conditions = analysis.pieceTypes.map {
                ConditionsBundle(
                        it,
                        STRICT_MAX(analysis.getTargetCount(it), isStrict),
                        STRICT_MAX(analysis.getBreakInCount(it), isStrict),
                        STRICT_MIN(analysis.getPreSolvedCount(it), isStrict),
                        STRICT_MAX(analysis.getMisOrientedCount(it), isStrict),
                        STRICT(analysis.hasParity(it), isStrict),
                        STRICT(analysis.isBufferSolved(it), isStrict),
                        !isStrict || analysis.isBufferSolvedAndMisOriented(it)
                )
            }

            return BldScramble(puzzle, "__POS_NOTATION", conditions)
        }

        fun fromStatString(statString: String, puzzle: TwistyPuzzle, isStrict: Boolean = false): BldScramble {
            val statStringParts = statString.split("|").dropLastWhile { it.isEmpty() }
            val statPattern = "([A-Za-z]+?):(_?)(0|[1-9][0-9]*)(\\*?)(#*)(~*)(\\+*)".toRegex()

            val conditions = statStringParts.mapNotNull { part ->
                val squashed = part.replace("\\s".toRegex(), "")

                statPattern.matchEntire(squashed)?.let {
                    ConditionsBundle(
                            puzzle.kPuzzle.pieceTypes.findByName(it.groupValues[1])!!,
                            STRICT_MAX(it.groupValues[3].toInt(), isStrict),
                            STRICT_MAX(it.groupValues[5].length, isStrict),
                            STRICT_MIN(it.groupValues[7].length, isStrict),
                            STRICT_MAX(it.groupValues[6].length, isStrict),
                            STRICT(it.groupValues[2].isNotEmpty(), isStrict),
                            STRICT(it.groupValues[4].isNotEmpty(), isStrict)
                    )
                }
            }

            return BldScramble(puzzle, "__POS_NOTATION", conditions)
        }
    }
}