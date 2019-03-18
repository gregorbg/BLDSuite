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
import com.suushiemaniac.cubing.bld.util.StickerTarget
import com.suushiemaniac.cubing.bld.util.StringUtil.alignWhitespaces
import com.suushiemaniac.cubing.bld.util.MathUtil.pMod

import kotlin.math.max
import kotlin.math.pow

class BldAnalysis(private val reader: NotationReader,
                  val orientationPreMoves: Algorithm,
                  val solutionTargets: Map<PieceType, List<StickerTarget>>,
                  val letterSchemes: Map<PieceType, Array<String>>,
                  val algSource: AlgSource? = null) {
    val pieceTypes = this.solutionTargets.keys

    // COMPILE CYCLES

    fun compileSolutionCycles(type: PieceType, useCompoundMisOrients: Boolean = true): List<PieceCycle> { // FIXME beautify
        val targets = this.solutionTargets.getValue(type)

        val misOrientMap = this.getGroupedMisOrientPieces(type)
        val permTargets = targets.filter { GPuzzle.targetToPerm(type, it.target) !in misOrientMap.values.flatten() }

        val permCycles = this.groupTargetsToCycles(permTargets)

        val orientCycles = if (useCompoundMisOrients) {
            misOrientMap.mapValues { it.value.map { p -> MisOrientPiece(GPuzzle.pieceToTarget(type, p, it.key), it.key) } }
        } else {
            val foo = misOrientMap.mapValues { targets.filter { t -> GPuzzle.targetToPerm(type, t.target) in it.value } }
            foo.mapValues { it.value.chunked(2).map { p -> SingleMisOrientCycle(it.key, p[0].target, p[1].target) } }
        }

        return permCycles + orientCycles.values.flatten()
    }

    protected fun groupTargetsToCycles(targets: List<StickerTarget>, shiftParityOffset: Boolean = false, step: Int = 2): List<PieceCycle> { // FIXME actually use the last 2 parameters
        val chunks = if (shiftParityOffset)
            targets.reversed().chunked(step).reversed().map { it.reversed() } else
            targets.chunked(step)

        return chunks.map {
            if (it.size == step) { // FIXME step size 1
                ThreeCycle(it[0].buffer, it[0].target, it[1].target)
            } else {
                ParityCycle(it[0].buffer, it[0].target)
            }
        }
    }

    // COMPILE HUMAN SOLUTIONS

    fun compileSolutionTargetString(type: PieceType, nice: Boolean = false): String { // FIXME beautify
        val (currentTwists, currentCycles) = this.compileSolutionCycles(type).partition { it is MisOrientCycle }

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

    open fun groupMisOrients(misOrients: List<MisOrientCycle>): List<ComplexMisOrientCycle> { // TODO incorporate into GPuzzle file format
        return misOrients
                .map { it as MisOrientPiece }
                .groupBy { it.orientation }
                .map { ComplexMisOrientCycle("Orient ${it.key}", *it.value.toTypedArray()) }
    }

    fun getSolutionAlgorithms(type: PieceType, short: Boolean = false): List<Algorithm> {
        if (this.algSource == null || !this.algSource.isReadable) {
            return listOf()
        }

        return this.compileSolutionCycles(type).map {
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

    fun getRawSolutionAlgorithm(withRotation: Boolean = false): Algorithm {
        val basis = if (withRotation) this.getRotations() else SimpleAlg()

        val fullSolution = this.pieceTypes
                .map { this.getRawSolutionAlgorithm(it) }
                .reduce(Algorithm::merge)

        return basis + fullSolution
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
        return this.getExplanationString(withRotation) { this.compileSolutionTargetString(it, true) }
    }

    fun getSolutionTargets(withRotation: Boolean = false): String {
        return this.getExplanationString(withRotation) { this.compileSolutionTargetString(it, false) }
    }

    fun getSolutionAlgorithms(withRotation: Boolean = false): String {
        return this.getExplanationString(withRotation) { this.getSolutionAlgorithms(it).joinToString("\n") }
    }

    // HELPERS

    fun getMainBuffer(type: PieceType): Int {
        return this.solutionTargets.getValue(type).first().buffer
    }

    fun getMainBufferPerm(type: PieceType): Int {
        return GPuzzle.targetToPerm(type, this.getMainBuffer(type))
    }

    fun getLetteringScheme(type: PieceType): Array<String> {
        return this.letterSchemes.getValue(type).copyOf()
    }

    fun getRotations(): Algorithm {
        return this.orientationPreMoves.copy()
    }

    // PIECE HELPERS

    fun getPreSolvedPieces(type: PieceType): List<Int> {
        val targetedPerms = this.solutionTargets.getValue(type).map { GPuzzle.targetToPerm(type, it.target) }

        return type.permutations.countingList() - targetedPerms
    }

    fun getPreSolvedCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy { this.getPrePermutedCount(it) }
        }

        return this.getPreSolvedPieces(type).count { it != this.getMainBufferPerm(type) }
    }

    fun getMisOrientedPieces(type: PieceType, orientation: Int? = null): List<Int> {
        if (orientation == null) {
            return type.orientations.countingList().flatMap { this.getMisOrientedPieces(type, it) }
        }

        val targetWindows = this.solutionTargets.getValue(type).zipWithNext()

        val samePermWindows = targetWindows.filter { (a, b) -> GPuzzle.targetToPerm(type, a.target) == GPuzzle.targetToPerm(type, b.target) }
        val orientWindows = samePermWindows.filter { (a, b) -> (GPuzzle.targetToOrient(type, a.target) - GPuzzle.targetToOrient(type, b.target)) pMod type.orientations == orientation }

        return orientWindows.map { GPuzzle.targetToPerm(type, it.first.target) }
    }

    fun getGroupedMisOrientPieces(type: PieceType) = type.orientations.countingList().associateWith { this.getMisOrientedPieces(type, it) }

    fun getMisOrientedCount(type: PieceType? = null, orientation: Int? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy { this.getMisOrientedCount(it, orientation) }
        }

        return this.getMisOrientedPieces(type, orientation).count { it != this.getMainBufferPerm(type) }
    }

    fun getPrePermutedPieces(type: PieceType): List<Int> {
        return this.getPreSolvedPieces(type) + this.getMisOrientedPieces(type)
    }

    fun getPrePermutedCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy { this.getPreSolvedCount(it) }
        }

        return this.getPrePermutedPieces(type).count { it != this.getMainBufferPerm(type) }
    }

    // STATISTICS

    fun getTargetCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy(this::getTargetCount)
        }

        return this.solutionTargets.getValue(type).size - 2 * this.getMisOrientedCount(type)
    }

    fun getBreakInCount(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy(this::getBreakInCount)
        }

        return this.solutionTargets.getValue(type).count { it.isCycleBreak } - this.getMisOrientedCount(type)
    }

    fun isSingleCycle(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.pieceTypes.all(this::isSingleCycle)
        }

        return this.getBreakInCount(type) == 0
    }

    fun hasParity(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.pieceTypes.any(this::hasParity)
        }

        return this.getTargetCount(type) % 2 != 0
    }

    fun isBufferSolved(type: PieceType? = null, acceptMisOrient: Boolean = true): Boolean {
        if (type == null) {
            return this.pieceTypes.any { this.isBufferSolved(it, acceptMisOrient) }
        }

        val bufferSolved = this.solutionTargets.getValue(type).firstOrNull()?.isCycleBreak ?: true

        return bufferSolved && (acceptMisOrient || !this.isBufferSolvedAndMisOriented(type))
    }

    fun isBufferSolvedAndMisOriented(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.pieceTypes.any { this.isBufferSolvedAndMisOriented(it) }
        }

        return this.getGroupedMisOrientPieces(type).map { it.key * it.value.size }.sum() % type.orientations != 0 // FIXME account for re-closed break-ins
    }

    fun getBufferFloatNum(type: PieceType? = null): Int {
        if (type == null) {
            return this.pieceTypes.sumBy(this::getBufferFloatNum)
        }

        return this.solutionTargets.getValue(type).map { it.buffer }.distinct().size - 1
    }

    fun hasBufferFloat(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.pieceTypes.any(this::hasBufferFloat)
        }

        return this.getBufferFloatNum(type) > 0
    }

    // NICE 4 HOOMANZ

    fun getNoahtation(type: PieceType? = null): String {
        if (type == null) {
            return this.pieceTypes
                    .reversed()
                    .joinToString(" / ") { this.getNoahtation(it) }
        }

        return "${this.pieceTypes.mnemonic(type) { name }}: ${this.getTargetCount(type)}${"'".repeat(this.getMisOrientedCount(type))}"
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

    fun matchesExecution(type: PieceType? = null, filter: (Algorithm) -> Boolean): Boolean {
        if (type == null) {
            return this.pieceTypes.all { this.matchesExecution(it, filter) }
        }

        if (this.algSource == null || !this.algSource.isReadable) {
            return false
        }

        val rawSolution = this.compileSolutionTargetString(type)

        return if (rawSolution == "Solved") true else this.compileSolutionCycles(type)
                .filter { it is ThreeCycle }
                .all { this.algSource.getAlgorithms(type, this.reader, it).any(filter) }
    }
}