package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.lang.ImageStringReader
import com.suushiemaniac.cubing.bld.model.cycle.*
import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.source.AlgSource
import com.suushiemaniac.cubing.bld.util.SpeffzUtil.targetToSticker
import com.suushiemaniac.cubing.bld.util.ArrayUtil.countingArray
import com.suushiemaniac.cubing.bld.util.ArrayUtil.filledArray
import com.suushiemaniac.cubing.bld.util.CollectionUtil.randomOrNull

import kotlin.math.max
import kotlin.math.pow

class BldAnalysis(origin: GPuzzle,
                  val scramble: Algorithm,
                  val orientationPreMoves: Algorithm, val solutionCycles: Map<PieceType, List<PieceCycle>>,
                  val letterSchemes: Map<PieceType, Array<String>>,
                  val algSource: AlgSource? = null) {
    val pieceTypes = this.solutionCycles.keys // FIXME iteration / solving order?

    val breakInCount = this.pieceTypes.associateWith { 0 }
    val parities = this.pieceTypes.associateWith { false }

    val prePermutedPieces = this.pieceTypes.associateWith { it.numPieces.filledArray(false) }
    val preSolvedPieces = this.pieceTypes.associateWith { it.numPieces.filledArray(false) }
    val misOrientedPieces = this.pieceTypes.associateWith { pt -> pt.targetsPerPiece.filledArray { pt.numPieces.filledArray(false) } } // TODO derive

    val bufferFloats = this.pieceTypes.associateWith { mutableMapOf<Int, Int>() }

    open fun groupMisOrients(misOrients: List<MisOrientCycle>): List<ComplexMisOrientCycle> {
        return misOrients
                .map { it as MisOrientPiece }
                .groupBy { it.orientation }
                .map { ComplexMisOrientCycle("Orient ${it.key}", *it.value.toTypedArray()) }
    }

    fun getSolutionTargets(type: PieceType, nice: Boolean = false): String {
        val (currentTwists, currentCycles) = this.solutionCycles.getValue(type).partition { it is MisOrientCycle }

        val letters = this.getLetteringScheme(type)

        val accu = StringBuilder()
        val bufferAccu = mutableListOf<Int>()

        for (cycle in currentCycles) {
            val lastBuffer = bufferAccu.lastOrNull() ?: -1

            if (lastBuffer != cycle.buffer) {
                val mainBuffer = bufferAccu.toSet().size > 1
                bufferAccu.add(cycle.buffer)

                if (!mainBuffer) {
                    val position = cycle.buffer.targetToSticker(type)
                    accu.append(if (nice) "(float $position) " else "($position)")
                }
            }

            if (cycle is ParityCycle) {
                if (nice) accu.append("Parity: ")
                accu.append(letters[cycle.target])
            } else if (cycle is ThreeCycle) {
                accu.append(letters[cycle.first])
                accu.append(letters[cycle.second])
            }

            if (nice) accu.append(" ")
        }

        if (nice) { // TODO what is this even?
            accu.append("\t")

            for (cycle in this.groupMisOrients(currentTwists.map { it as MisOrientCycle })) {
                accu.append("${cycle.description}: ")
                accu.append(cycle.getAllTargets().joinToString("") { letters[it] })
                accu.append("\t")
            }
        } else {
            for ((orientation, cycles) in currentTwists.map { it as MisOrientPiece }.groupBy { it.orientation }) {
                accu.append("[$orientation]")
                accu.append(cycles.joinToString("") { letters[it.target] })
            }
        }

        return accu.toString().trim()
    }

    fun getSolutionAlgorithms(type: PieceType, short: Boolean = false): List<Algorithm> {
        if (this.algSource == null || !this.algSource.isReadable) {
            return listOf()
        }

        return this.solutionCycles.getValue(type).map {
            val lettering = this.getLetteringScheme(type)
            val letterTargets = it.getAllTargets().joinToString("-") { t -> lettering[t] }
            val bufferSticker = it.buffer.targetToSticker(type)

            val backup = if (short) "[]" else "Not found: $bufferSticker>$letterTargets"

            this.algSource.getAlgorithms(type, it).toList().randomOrNull()
                    ?: ImageStringReader().parse(backup)
        }
    }

    fun getRawSolutionAlgorithm(type: PieceType): Algorithm {
        return this.getSolutionAlgorithms(type).reduce(Algorithm::merge)
    }

    fun getExplanationString(withRotation: Boolean = false, explain: (PieceType) -> String): String {
        val explanations = mutableListOf<String>()

        if (withRotation) {
            val formatRotations = this.getRotations().toFormatString()
            explanations.add("Rotations: ${if (formatRotations.isNotBlank()) formatRotations else "/"}")
        }

        explanations += this.pieceTypes.associateWith(explain)
                .map { "${it.key.humanName}: ${it.value}" }

        return explanations.joinToString("\n")
    }

    fun getSolutionPairs(withRotation: Boolean = false): String {
        return this.getExplanationString(withRotation) { this.getSolutionTargets(it, true) }
    }

    fun getSolutionTargets(withRotation: Boolean = false): String {
        return this.getExplanationString(withRotation) { this.getSolutionTargets(it, false) }
    }

    fun getSolutionAlgorithms(withRotation: Boolean = false): String {
        return this.getExplanationString(withRotation) { this.getSolutionAlgorithms(it).joinToString("\n", transform = Algorithm::toFormatString) }
    }

    fun getRawSolutionAlgorithm(withRotation: Boolean = false): Algorithm {
        val basis = if (withRotation) this.getRotations() else SimpleAlg()

        val fullSolution = this.pieceTypes
                .map(this::getRawSolutionAlgorithm)
                .reduce(Algorithm::merge)

        return basis.merge(fullSolution)
    }

    fun getPrePermutedCount(type: PieceType): Int {
        return this.prePermutedPieces.getValue(type)
                .drop(1) // FIXME we're not interested in the main buffer itself
                .count { it }
    }

    fun getPreSolvedCount(type: PieceType): Int {
        return this.preSolvedPieces.getValue(type)
                .drop(1) // FIXME we're not interested in the main buffer itself
                .count { it }
    }

    fun getMisOrientedPieces(type: PieceType, orientation: Int): List<Int> {
        val orientations = this.misOrientedPieces.getValue(type)[orientation]

        return orientations.indices.drop(1)
                .filter { orientations[it] }
                .map { it + 1 } // FIXME nasty hack
    }

    fun getMisOrientedCount(type: PieceType, orientation: Int? = null): Int {
        if (orientation == null) {
            return type.targetsPerPiece.countingArray()
                    .sumBy { this.getMisOrientedCount(type, it) }
        }

        return this.getMisOrientedPieces(type, orientation).size
    }

    fun getLetteringScheme(type: PieceType): Array<String> {
        return if (type is LetterPairImage) {
            this.pieceTypes
                    .flatMap { this.getLetteringScheme(it).asIterable() }
                    .toSortedSet()
                    .toTypedArray()
        } else this.letterSchemes.getValue(type).copyOf()
    }

    fun getPieceOrientation(type: PieceType, piece: Int): Int { // FIXME
        val misOrients = this.misOrientedPieces.getValue(type)
        return misOrients.indices.find { misOrients[it][piece] } ?: -1
    }

    fun getScrambleScore(type: PieceType? = null): Float { // TODO refine
        if (type == null) {
            val score = this.pieceTypes.sumByDouble { it.numPieces * this.getScrambleScore(it).toDouble() }
            val weight = this.pieceTypes.sumBy(PieceType::numPieces)

            return score.toFloat() / weight
        }

        val scoreBase = mutableListOf<Float>()

        scoreBase.add(type.numPieces.toFloat().pow(2))

        scoreBase.add(-this.getTargetCount(type).toFloat())
        scoreBase.add(this.getPreSolvedCount(type).toFloat())
        scoreBase.add(-(this.getMisOrientedCount(type) * type.targetsPerPiece).toFloat())
        scoreBase.add(-(this.getBreakInCount(type) * type.numPiecesNoBuffer).toFloat())

        val scoreScale = mutableListOf<Float>()

        if (this.hasParity(type)) {
            scoreScale.add(.25f)
        }

        if (this.isBufferSolved(type)) {
            scoreScale.add(.25f)
        }

        return max(0f, (1 - scoreScale.sum()) * scoreBase.sum())
    }

    fun getStatString(type: PieceType? = null): String { // TODO
        if (type == null) {
            return this.pieceTypes
                    .reversed()
                    .joinToString(" | ", transform = this::getStatString)
        }

        val statString = StringBuilder("${type.mnemonic}: ")

        if (this.hasParity(type)) {
            statString.append("_")
        }

        statString.append(this.getTargetCount(type))

        if (this.isBufferSolved(type)) {
            statString.append("*")

            if (this.isBufferSolvedAndMisOriented(type)) {
                statString.append("*")
            }
        }

        val repStatistics = mapOf(
                "\\" to this.getBufferFloatNum(type),
                "#" to this.getBreakInCount(type),
                "~" to this.getMisOrientedCount(type),
                "+" to this.getPreSolvedCount(type)
        )

        for ((sym, num) in repStatistics) {
            statString.append("\t")
            statString.append(sym.repeat(num))
        }

        return statString.toString().trim()
    }

    fun getShortStats(type: PieceType? = null): String {
        if (type == null) {
            return this.pieceTypes
                    .reversed()
                    .joinToString("\n", transform = this::getShortStats)
        }

        return "${type.humanName}: ${this.getTargetCount(type)}@${this.getBreakInCount(type)} w/ ${this.getPreSolvedCount(type)}-${this.getMisOrientedCount(type)}\\${this.getBufferFloatNum(type)} > ${this.hasParity(type)}"
    }

    fun getTargetCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy(this::getTargetCount)
        }

        return this.solutionCycles.getValue(type)
                .filter { it is ThreeCycle } // FIXME what about parity + 1?
                .size * 2
    }

    fun getBreakInCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy(this::getBreakInCount)
        }

        return this.breakInCount.getValue(type)
    }

    fun isSingleCycle(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.pieceTypes.all(this::isSingleCycle)
        }

        return this.getBreakInCount(type) == 0
    }

    fun hasParity(type: PieceType? = null): Boolean { // consider parity dependents
        if (type == null) {
            return this.pieceTypes.any(this::hasParity)
        }

        return this.parities.getOrElse(type) { false }
    }

    fun isBufferSolved(type: PieceType? = null, acceptMisOrient: Boolean = true): Boolean {
        if (type == null) {
            return this.pieceTypes.any { this.isBufferSolved(it, acceptMisOrient) }
        }

        val bufferSolved = this.preSolvedPieces.getValue(type)[0] // FIXME

        return bufferSolved || (acceptMisOrient && this.isBufferSolvedAndMisOriented(type))
    }

    fun isBufferSolvedAndMisOriented(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.pieceTypes.any { this.isBufferSolvedAndMisOriented(it) }
        }

        return this.prePermutedPieces.getValue(type)[0] // FIXME
    }

    fun getBufferFloatNum(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy(this::getBufferFloatNum)
        }

        return this.bufferFloats.getValue(type).size
    }

    fun hasBufferFloat(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.pieceTypes.any(this::hasBufferFloat)
        }

        return this.getBufferFloatNum(type) > 0
    }

    fun getNoahtation(type: PieceType? = null): String {
        if (type == null) {
            return this.pieceTypes
                    .reversed()
                    .joinToString(" / ") { this.getNoahtation(it) }
        }

        return "${type.mnemonic}: ${this.getTargetCount(type)}${"'".repeat(this.getMisOrientedCount(type))}"
    }

    fun getRotations(): Algorithm {
        return this.orientationPreMoves.copy()
    }

    fun matchesExecution(type: PieceType? = null, filter: (Algorithm) -> Boolean): Boolean {
        if (type == null) {
            return this.pieceTypes.all { this.matchesExecution(it, filter) }
        }

        if (this.algSource == null) {
            return false
        }

        val rawSolution = this.getSolutionTargets(type)

        return if (rawSolution == "Solved") true else this.solutionCycles.getValue(type)
                .filter { it is ThreeCycle }
                .all { this.algSource.getAlgorithms(type, it).any(filter) }
    }
}