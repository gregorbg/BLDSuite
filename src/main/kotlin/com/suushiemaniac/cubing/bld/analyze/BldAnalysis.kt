package com.suushiemaniac.cubing.bld.analyze

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.lang.ImageStringReader
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.cycle.*
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.AlgSource
import com.suushiemaniac.cubing.bld.util.CollectionUtil.countingList
import com.suushiemaniac.cubing.bld.util.CollectionUtil.randomOrNull
import com.suushiemaniac.cubing.bld.util.CollectionUtil.mnemonic
import com.suushiemaniac.cubing.bld.util.StringUtil.alignWhitespaces

import kotlin.math.max
import kotlin.math.pow

class BldAnalysis(private val reader: NotationReader,
                  val orientationPreMoves: Algorithm,
                  val solutionCycles: Map<PieceType, List<PieceCycle>>,
                  val mainBuffers: Map<PieceType, List<Int>>,
                  val letterSchemes: Map<PieceType, Array<String>>,
                  val algSource: AlgSource? = null) {
    val pieceTypes = this.solutionCycles.keys

    open fun groupMisOrients(misOrients: List<MisOrientCycle>): List<ComplexMisOrientCycle> { // TODO incorporate into file format
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
                val mainBuffer = bufferAccu.toSet().isEmpty()
                bufferAccu.add(cycle.buffer)

                if (!mainBuffer) {
                    val lettering = this.getLetteringScheme(type)
                    val position = lettering[cycle.buffer] // FIXME posNotation
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
            val bufferSticker = lettering[it.buffer] // FIXME posNotation

            val backup = if (short) "[]" else "Not found: $bufferSticker>$letterTargets"

            this.algSource.getAlgorithms(type, this.reader, it).toList().randomOrNull()
                    ?: ImageStringReader().parse(backup)
        }
    }

    fun getRawSolutionAlgorithm(type: PieceType): Algorithm {
        return this.getSolutionAlgorithms(type).reduce(Algorithm::merge)
    }

    fun getExplanationString(withRotation: Boolean = false, explain: (PieceType) -> String): String {
        val explanations = mutableListOf<String>()

        if (withRotation) {
            val formatRotations = this.getRotations().toString()
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
        return this.getExplanationString(withRotation) { this.getSolutionAlgorithms(it).joinToString("\n") }
    }

    fun getRawSolutionAlgorithm(withRotation: Boolean = false): Algorithm {
        val basis = if (withRotation) this.getRotations() else SimpleAlg()

        val fullSolution = this.pieceTypes
                .map { this.getRawSolutionAlgorithm(it) }
                .reduce(Algorithm::merge)

        return basis.merge(fullSolution)
    }

    fun getMainBuffer(type: PieceType): Int {
        return this.mainBuffers.getValue(type).first() // FIXME
    }

    fun getMainBufferPerm(type: PieceType): Int {
        return GPuzzle.targetToPerm(type, this.getMainBuffer(type))
    }

    fun getPrePermutedPieces(type: PieceType): List<Int> {
        val relevantCycles = this.solutionCycles.getValue(type).filter { it !is MisOrientCycle }
        val targetedPerms = relevantCycles.flatMap { it.getAllTargets() }.map { GPuzzle.targetToPerm(type, it) }

        return type.permutations.countingList() - targetedPerms
    }

    fun getPreSolvedPieces(type: PieceType): List<Int> {
        val targetedPerms = this.solutionCycles.getValue(type).flatMap { it.getAllTargets() }.map { GPuzzle.targetToPerm(type, it) }

        return type.permutations.countingList() - targetedPerms
    }

    fun getMisOrientedPieces(type: PieceType, orientation: Int?): List<Int> {
        if (orientation == null) {
            return type.orientations.countingList().flatMap { this.getMisOrientedPieces(type, it) }
        }

        val relevantCycles = this.solutionCycles.getValue(type).filter { it is MisOrientCycle }.map { it as MisOrientCycle }
        val relevantTargets = relevantCycles.filter { it.orientation == orientation }.flatMap { it.getAllTargets() }

        return relevantTargets.map { GPuzzle.targetToPerm(type, it) }
    }

    fun getPrePermutedCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy { this.getPrePermutedCount(it) }
        }

        return this.getPrePermutedPieces(type).toMutableList()
                .also { it.remove(this.getMainBufferPerm(type)) }.size
    }

    fun getPreSolvedCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy { this.getPreSolvedCount(it) }
        }

        return this.getPreSolvedPieces(type).toMutableList()
                .also { it.remove(this.getMainBufferPerm(type)) }.size
    }

    fun getMisOrientedCount(type: PieceType? = null, orientation: Int? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy { this.getMisOrientedCount(it, orientation) }
        }

        return this.getMisOrientedPieces(type, orientation).toMutableList()
                .also { it.remove(this.getMainBufferPerm(type)) }.size
    }

    fun getLetteringScheme(type: PieceType): Array<String> {
        return this.letterSchemes.getValue(type).copyOf()
    }

    fun getScrambleScore(type: PieceType? = null): Float { // TODO refine
        if (type == null) {
            val score = this.pieceTypes.sumByDouble { it.permutations * this.getScrambleScore(it).toDouble() }
            val weight = this.pieceTypes.sumBy(PieceType::permutations)

            return score.toFloat() / weight
        }

        val scoreBase = mutableListOf<Float>()

        scoreBase.add(type.permutations.toFloat().pow(2))

        scoreBase.add(-this.getTargetCount(type).toFloat())
        scoreBase.add(this.getPreSolvedCount(type).toFloat())
        scoreBase.add(-(this.getMisOrientedCount(type) * type.orientations).toFloat())
        scoreBase.add(-(this.getBreakInCount(type) * type.orientations).toFloat())

        val scoreScale = mutableListOf<Float>()

        if (this.hasParity(type)) {
            scoreScale.add(.25f)
        }

        if (this.isBufferSolved(type)) {
            scoreScale.add(.25f)
        }

        return max(0f, (1 - scoreScale.sum()) * scoreBase.sum())
    }

    fun getStatString(type: PieceType? = null): String {
        if (type == null) {
            return this.pieceTypes
                    .reversed()
                    .joinToString(" | ", transform = this::getStatString)
                    .alignWhitespaces("\t", " | ")
        }

        val statString = StringBuilder("${this.pieceTypes.mnemonic(type) { name }}: ")

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

        return this.solutionCycles.getValue(type).sumBy { it.targetCount }
    }

    fun getBreakInCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy(this::getBreakInCount)
        }

        val relevantCycles = this.solutionCycles.getValue(type).filter { it !is MisOrientCycle }
        val targetedPerms = relevantCycles.flatMap { it.getAllTargets() }.map { GPuzzle.targetToPerm(type, it) }

        return targetedPerms.groupingBy { it }.eachCount().filterValues { it > 1 }.size
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

        return this.solutionCycles.getValue(type).any { it is ParityCycle }
    }

    fun isBufferSolved(type: PieceType? = null, acceptMisOrient: Boolean = true): Boolean {
        if (type == null) {
            return this.pieceTypes.any { this.isBufferSolved(it, acceptMisOrient) }
        }

        val bufferSolved = this.getMainBufferPerm(type) in this.getPreSolvedPieces(type)

        return bufferSolved || (acceptMisOrient && this.isBufferSolvedAndMisOriented(type))
    }

    fun isBufferSolvedAndMisOriented(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.pieceTypes.any { this.isBufferSolvedAndMisOriented(it) }
        }

        return this.getMainBufferPerm(type) in this.getPrePermutedPieces(type)
    }

    fun getBufferFloatNum(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy(this::getBufferFloatNum)
        }

        return 0 // TODO
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

        return "${this.pieceTypes.mnemonic(type) { name }}: ${this.getTargetCount(type)}${"'".repeat(this.getMisOrientedCount(type))}"
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
                .all { this.algSource.getAlgorithms(type, this.reader, it).any(filter) }
    }

    fun getRotations(): Algorithm {
        return this.orientationPreMoves.copy()
    }
}