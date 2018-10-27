package com.suushiemaniac.cubing.bld.filter

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition
import com.suushiemaniac.cubing.bld.model.cycle.ThreeCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.source.AlgSource
import com.suushiemaniac.cubing.bld.util.SpeffzUtil
import com.suushiemaniac.cubing.bld.util.BruteForceUtil.permuteStr
import com.suushiemaniac.cubing.bld.util.StringUtil.guessRegExpRange
import com.suushiemaniac.cubing.bld.util.StringUtil.toCharStrings

class ConditionsBundle(val pieceType: PieceType) {
    var targets: IntCondition = IntCondition.ANY()
    var breakIns: IntCondition = IntCondition.ANY()
    var preSolved: IntCondition = IntCondition.ANY()
    var misOriented: IntCondition = IntCondition.ANY()

    var parity: BooleanCondition = BooleanCondition.MAYBE()
    var bufferSolved: BooleanCondition = BooleanCondition.MAYBE()

    constructor(pieceType: PieceType,
                targets: IntCondition = IntCondition.ANY(),
                breakIns: IntCondition = IntCondition.ANY(),
                preSolved: IntCondition = IntCondition.ANY(),
                misOriented: IntCondition = IntCondition.ANY(),
                parity: BooleanCondition = BooleanCondition.MAYBE(),
                bufferSolved: BooleanCondition = BooleanCondition.MAYBE()): this(pieceType) {
        this.targets = targets
        this.breakIns = breakIns
        this.preSolved = preSolved
        this.misOriented = misOriented
        this.parity = parity
        this.bufferSolved = bufferSolved

        this.balanceProperties()
    }

    var isAllowTwistedBuffer: Boolean = ALLOW_TWISTED_BUFFER

    protected var memoRegex: String = REGEX_UNIV
    protected var predicateRegex: String = REGEX_UNIV
    protected var letterPairRegex: String = REGEX_UNIV

    protected var statisticalPredicate: (BldPuzzle) -> Boolean = { true }

    val statString: String
        get() = this.pieceType.mnemonic + ": " + (if (this.parity.positive) "_" else "") +
                (if (this.parity.isImportant) "! " else "? ") +
                this.targets.toString() +
                " " +
                (if (this.bufferSolved.positive) if (this.isAllowTwistedBuffer) "**" else "*" else "") +
                (if (this.bufferSolved.isImportant) "! " else "? ") +
                this.breakIns.toString("#") +
                " " +
                this.misOriented.toString("~") +
                " " +
                this.preSolved.toString("+")

    private fun balanceTargets() {
        this.targets.capMin(0)
        // C=10 E=16 W=34 XC=34 TC=34
        this.targets.capMax(this.pieceType.numPiecesNoBuffer / 2 * 3 + this.pieceType.numPiecesNoBuffer % 2)

        // pre-solved
        // mis-orient
        // parity
    }

    private fun balanceBreakIns() {
        // C=7 E=11 W=23 XC=23 TC=23
        this.breakIns.capMin(Math.max(if (this.bufferSolved.negative) 1 else 0, this.targets.getMin() - this.pieceType.numPiecesNoBuffer))
        // C=3 E=5 W=11 XC=11 TC=11
        this.breakIns.capMax(this.pieceType.numPiecesNoBuffer / 2)

        // targets
    }

    private fun balanceParity() {
        if (this.targets.isPrecise) {
            this.parity.value = this.targets.getMax() % 2 == 1
        }
    }

    private fun balanceBufferSolved() {
        // break-ins
    }

    private fun balanceSolvedMisOriented() {
        // C=7 E=11 W=23 XC=23 TC=23
        val leftOverMin = Math.max(0, this.pieceType.numPiecesNoBuffer + this.breakIns.getMax() - this.targets.getMax())
        val leftOverMax = Math.min(this.pieceType.numPiecesNoBuffer, this.pieceType.numPiecesNoBuffer + this.breakIns.getMin() - this.targets.getMin())

        this.preSolved.capMin(0)
        this.misOriented.capMin(0)

        // C=8 E=12 W=? XC=? TC=?
        this.preSolved.capMax(pieceType.numPieces)
        // C=8 E=12 W=? XC=? TC=?
        this.misOriented.capMax(pieceType.numPieces)

        val sumMin = this.preSolved.getMin() + this.misOriented.getMin()
        val sumMax = this.preSolved.getMax() + this.misOriented.getMax()

        if (sumMin > leftOverMax) {
            if (this.preSolved.isPrecise || !this.misOriented.isPrecise) {
                this.misOriented.setMin(this.misOriented.getMin() - sumMin + leftOverMax)
            }

            if (this.misOriented.isPrecise || !this.preSolved.isPrecise) {
                this.preSolved.setMin(this.preSolved.getMin() - sumMin + leftOverMax)
            }
        } else if (sumMax < leftOverMin) {
            if (this.preSolved.isPrecise || !this.misOriented.isPrecise) {
                this.misOriented.setMax(this.misOriented.getMax() + leftOverMin - sumMax)
            }

            if (this.misOriented.isPrecise || !this.preSolved.isPrecise) {
                this.preSolved.setMax(this.misOriented.getMax() + leftOverMin - sumMax)
            }
        }

        // targets
    }

    fun balanceProperties() {
        this.balanceTargets()
        this.balanceBufferSolved()
        this.balanceBreakIns()
        this.balanceParity()
        this.balanceSolvedMisOriented()
    }

    fun setLetterPairRegex(letteringScheme: Array<String>, pairs: List<String>, conjunctive: Boolean = true, allowInverse: Boolean = false) {
        var regexPairs = pairs
        val letters = letteringScheme.joinToString("")
        var row = letters.guessRegExpRange()

        if (row == letters) {
            row = "[" + Regex.escape(row) + "]"
        }

        val anyLP = "$row{2}"
        val kleeneLP = "($anyLP)*"

        if (allowInverse) {
            regexPairs = regexPairs.map { pair -> "([$pair]{2})" }
        }

        val glue = if (conjunctive) kleeneLP else "|"
        val pieces = if (conjunctive) List(regexPairs.size) { "(" + regexPairs.joinToString("|") + ")" } else regexPairs
        var joined = pieces.joinToString(glue)

        if (!conjunctive) {
            joined = "($joined)"
        }

        this.letterPairRegex = kleeneLP + joined + kleeneLP
    }

    fun setPredicateRegex(algSource: AlgSource, filter: (Algorithm) -> Boolean) {
        val matches = sortedSetOf<String>()
        val possPairs = SpeffzUtil.FULL_SPEFFZ.permuteStr(2, false, false)

        for (pair in possPairs) {
            val targetIndices = pair.toCharStrings().map { SpeffzUtil.FULL_SPEFFZ.indexOf(it) }
            val case = ThreeCycle(0, targetIndices[0], targetIndices[1]) // FIXME buffer

            matches.addAll(algSource.getAlgorithms(this.pieceType, case)
                    .filter(filter)
                    .map { pair })
        }

        if (matches.size > 0) {
            this.predicateRegex = "(" + matches.joinToString("|") + ")*" + if (this.parity.positive) "[A-Z]?" else ""
        }
    }

    fun matchingConditions(inCube: BldPuzzle): Boolean {
        return (inCube.getPieceTypes().contains(this.pieceType)
                && this.parity.evaluatePositive(inCube.hasParity(this.pieceType))
                && this.bufferSolved.evaluatePositive(inCube.isBufferSolved(this.pieceType, this.isAllowTwistedBuffer))
                && this.breakIns.evaluate(inCube.getBreakInCount(this.pieceType))
                && this.targets.evaluate(inCube.getCycleLength(this.pieceType))
                && this.preSolved.evaluate(inCube.getPreSolvedCount(this.pieceType))
                && this.misOriented.evaluate(inCube.getMisOrientedCount(this.pieceType))
                && inCube.getSolutionTargets(this.pieceType).matches(this.memoRegex.toRegex()) // TODO have regular expressions comply w/ buffer floats!
                && inCube.getSolutionTargets(this.pieceType).matches(this.letterPairRegex.toRegex())
                && inCube.getSolutionTargets(this.pieceType).matches(this.predicateRegex.toRegex())
                && this.statisticalPredicate(inCube))
    }

    override fun toString(): String {
        return this.statString
    }

    companion object {
        const val REGEX_UNIV = ".*"

        private var ALLOW_TWISTED_BUFFER = true

        fun setGlobalAllowTwistedBuffer(allowTwistedBuffer: Boolean) {
            ConditionsBundle.ALLOW_TWISTED_BUFFER = allowTwistedBuffer
        }
    }
}
