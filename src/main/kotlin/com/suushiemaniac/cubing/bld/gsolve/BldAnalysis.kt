package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.lang.ImageStringReader
import com.suushiemaniac.cubing.bld.analyze.BldCube
import com.suushiemaniac.cubing.bld.model.cycle.*
import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import com.suushiemaniac.cubing.bld.optim.BreakInOptim
import com.suushiemaniac.cubing.bld.util.ArrayUtil.countingArray
import com.suushiemaniac.cubing.bld.util.SpeffzUtil
import com.suushiemaniac.cubing.bld.util.SpeffzUtil.targetToSticker
import com.suushiemaniac.cubing.bld.util.StringUtil.trySpace
import kotlin.math.max
import kotlin.math.pow

class BldAnalysis {
    /*val scrambledState = initState().toMutableMap()

    val cycles = this.getPieceTypes() alwaysTo { mutableListOf<Int>() }
    val cycleCount = (this.getPieceTypes() alwaysTo 0).toMutableMap()

    val parities = this.getPieceTypes().associateWith { false }.toMutableMap()

    val solvedPieces = this.getPieceTypes() allTo { it.numPieces.filledArray(false) }
    val preSolvedPieces = this.getPieceTypes() allTo { it.numPieces.filledArray(false) }
    val misOrientedPieces = this.getPieceTypes() allTo { pt -> pt.targetsPerPiece.filledArray { pt.numPieces.filledArray(false) } }

    var scramble: Algorithm = SimpleAlg()
    var scrambleOrientationPreMoves: Algorithm = SimpleAlg()

    val letterSchemes = (this.getPieceTypes() alwaysTo SpeffzUtil.FULL_SPEFFZ).toMutableMap()

    val mainBuffers = (this.getPieceTypes() allTo { this.getDefaultCubies().getValue(it)[0][0] }).toMutableMap()
    val backupBuffers = this.getPieceTypes() alwaysTo { mutableListOf<Int>() }
    val bufferFloats = this.getPieceTypes() alwaysTo { mutableMapOf<Int, Int>() }

    protected open fun getBreakInTargetsAfter(type: PieceType, piece: Int): List<Int> {
        if (this.algSource == null || !this.optimizeBreakIns.getValue(type) || this.cycles.getValue(type).size % 2 == 0) {
            return this.cubies.getValue(type).sortedBy { it.min() }.reduce { a, b -> a + b }.toList()
        }

        if (this.optim == null)
            this.optim = BreakInOptim(this.algSource!!, BldCube(this.model as CubicPuzzle), false)

        return this.optim!!.optimizeBreakInTargetsAfter(piece, type)
    }

    protected fun increaseCycleCount(type: PieceType) {
        this.cycleCount.increment(type)
    }

    protected fun getLastTarget(type: PieceType): Int {
        return this.cycles.getValue(type).lastOrNull() ?: -1
    }

    protected open fun compilePermuteSolutionCycles(type: PieceType): List<PieceCycle> {
        val currentCycles = this.cycles.getValue(type)
        val mainBuffer = this.mainBuffers.getValue(type)

        var currentBuffer = mainBuffer
        val bufferFloats = this.bufferFloats.getValue(type)

        val cycles = mutableListOf<PieceCycle>()

        for (c in currentCycles.indices.chunked(2)) {
            if (c[0] in bufferFloats.keys) {
                currentBuffer = bufferFloats.getValue(c[0])
            }

            if (c.size == 2) {
                cycles.add(ThreeCycle(currentBuffer, currentCycles[c[0]], currentCycles[c[1]]))
            } else {
                cycles.add(ParityCycle(currentBuffer, currentCycles[c[0]]))
            }
        }

        return cycles
    }

    fun compileSolutionCycles(type: PieceType): List<PieceCycle> {
        val mainBuffer = this.mainBuffers.getValue(type)

        val cycles = mutableListOf<PieceCycle>()
        cycles.addAll(this.compilePermuteSolutionCycles(type))

        for (i in 1 until type.targetsPerPiece) {
            val misOrients = this.getMisOrientedPieces(type, i)

            when (this.misOrientMethod) {
                KPuzzle.MisOrientMethod.SINGLE_TARGET -> {
                    val cubies = this.cubies.getValue(type)

                    for (piece in misOrients) {
                        val outer = cubies.deepOuterIndex(piece)
                        val inner = cubies.deepInnerIndex(piece)

                        cycles.add(ThreeCycle(mainBuffer, piece, cubies[outer][(inner + i) % type.targetsPerPiece]))
                    }
                }
                KPuzzle.MisOrientMethod.SOLVE_DIRECT ->
                    cycles.addAll(misOrients.map { MisOrientPiece(it, i) })
            }
        }

        return cycles
    }

    open fun groupMisOrients(type: PieceType, misOrients: List<MisOrientCycle>): List<ComplexMisOrientCycle> {
        return misOrients
                .map { it as MisOrientPiece }
                .groupBy { it.orientation }
                .map { ComplexMisOrientCycle("Orient ${it.key}", *it.value.toTypedArray()) }
    }

    fun getSolutionTargets(type: PieceType, nice: Boolean = false): String {
        val (currentTwists, currentCycles) = this.compileSolutionCycles(type).partition { it is MisOrientCycle }

        val letters = this.getLetteringScheme(type)

        val accu = StringBuilder()
        var lastBuffer = -1

        for (cycle in currentCycles) {
            if (lastBuffer != cycle.buffer) {
                val mainBuffer = lastBuffer < 0
                lastBuffer = cycle.buffer

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

        if (nice) {
            accu.trySpace()

            for (cycle in this.groupMisOrients(type, currentTwists.map { it as MisOrientCycle })) {
                accu.append("${cycle.description}: ")
                accu.append(cycle.getAllTargets().joinToString("") { letters[it] })
                accu.trySpace()
            }
        } else {
            for ((orientation, cycles) in currentTwists.map { it as MisOrientPiece }.groupBy { it.orientation }) {
                accu.append("[$orientation]")
                accu.append(cycles.joinToString("") { letters[it.target] })
            }
        }

        return accu.toString().trim()
    }

    fun getSolutionAlgorithms(type: PieceType): List<Algorithm> {
        if (this.algSource == null || !this.algSource!!.isReadable) {
            return listOf()
        }

        return this.compileSolutionCycles(type).map {
            val lettering = this.getLetteringScheme(type)
            val letterTargets = it.getAllTargets().map { t -> lettering[t] }
            val bufferSticker = it.buffer.targetToSticker(type)

            this.algSource!!.getAlgorithms(type, it).toList().random()
                    ?: ImageStringReader().parse("Not found: $bufferSticker>${letterTargets.joinToString("-")}")
        }
    }

    fun getRawSolutionAlgorithm(type: PieceType): Algorithm {
        return this.getSolutionAlgorithms(type).reduce(Algorithm::merge)
    }

    fun getExplanationString(explain: (PieceType) -> String, withRotation: Boolean = false): String {
        val explanations = mutableListOf<String>()

        if (withRotation) {
            explanations.add("Rotations: " + if (this.scrambleOrientationPreMoves.algLength() > 0) this.scrambleOrientationPreMoves.toFormatString() else "/")
        }

        explanations += this.getExplanation(explain)
                .map { "${it.key.humanName}: ${it.value}" }

        return explanations.joinToString("\n")
    }

    fun <T> getExplanation(explain: (PieceType) -> T): Map<PieceType, T> {
        return this.getExecutionOrderPieceTypes()
                .map { it to explain(it) }
                .toMap()
    }

    fun getSolutionPairs(withRotation: Boolean = false): String {
        return this.getExplanationString({ this.getSolutionTargets(it, true) }, withRotation)
    }

    fun getSolutionTargets(withRotation: Boolean = false): String {
        return this.getExplanationString({ this.getSolutionTargets(it, false) }, withRotation)
    }

    fun getSolutionAlgorithms(withRotation: Boolean = false): String {
        return this.getExplanationString({ this.getSolutionAlgorithms(it).joinToString("\n", transform = Algorithm::toFormatString) }, withRotation)
    }

    fun getRawSolutionAlgorithm(withRotation: Boolean = false): Algorithm {
        val basis = if (withRotation) this.scrambleOrientationPreMoves.copy() else SimpleAlg()

        val fullSolution = this.getExplanation(this::getRawSolutionAlgorithm).values
                .reduce(Algorithm::merge)

        return basis.merge(fullSolution)
    }

    fun getPreSolvedCount(type: PieceType): Int {
        return this.preSolvedPieces.getValue(type)
                .drop(1) // we're not interested in the main buffer itself
                .count { it }
    }

    fun getMisOrientedCount(type: PieceType): Int {
        return type.targetsPerPiece.countingArray()
                .sumBy { this.getMisOrientedCount(type, it) }
    }

    fun getMisOrientedCount(type: PieceType, orientation: Int): Int {
        return this.misOrientedPieces.getValue(type)[orientation % type.targetsPerPiece]
                .drop(1) // we're not interested in the main buffer itself
                .count { it }
    }

    protected fun getMisOrientedPieces(type: PieceType, orientation: Int): List<Int> {
        val orientations = this.misOrientedPieces.getValue(type)[orientation]
        val cubies = this.cubies.getValue(type)

        return orientations.indices.drop(1)
                .filter { orientations[it] }
                .map { cubies[it][orientation] }
    }

    protected fun getMisOrientedPieceLetters(type: PieceType, orientation: Int): List<String> {
        val lettering = this.getLetteringScheme(type)
        val pieces = this.getMisOrientedPieces(type, orientation)

        return pieces.map { lettering[it] }
    }

    fun getLetteringScheme(type: PieceType): Array<String> {
        return if (type is LetterPairImage) {
            this.getPieceTypes()
                    .flatMap { this.getLetteringScheme(it).asIterable() }
                    .toSortedSet()
                    .toTypedArray()
        } else this.letterSchemes.getValue(type).copyOf()
    }

    fun getBufferTarget(type: PieceType): String {
        return if (type is LetterPairImage) this.letterPairLanguage else this.getLetteringScheme(type)[this.getBuffer(type)]
    }

    fun getBufferPiece(type: PieceType): Array<Int> {
        if (type is LetterPairImage) {
            return arrayOf()
        }

        return this.cubies.getValue(type)[0].copyOf()
    }

    fun getBufferPieceTargets(type: PieceType): Array<String> {
        if (type is LetterPairImage) {
            return arrayOf(this.letterPairLanguage)
        }

        return this.getBufferPiece(type)
                .map { this.letterSchemes.getValue(type)[it] }
                .toTypedArray()
    }

    protected fun getPieceOrientation(type: PieceType, piece: Int): Int {
        val misOrients = this.misOrientedPieces.getValue(type)
        return misOrients.indices.find { misOrients[it][piece] } ?: -1
    }

    fun getLetterPairCorrespondant(type: PieceType, piece: Int): String {
        val lettering = this.getLetteringScheme(type)
        return this.getCorrespondents(type, piece).joinToString("") { lettering[it] }
    }

    fun getScrambleScore(type: PieceType): Double { // TODO refine
        var scoreBase = type.numPieces.toDouble().pow(2)

        scoreBase -= this.getCycleLength(type).toFloat()
        scoreBase += this.getPreSolvedCount(type).toFloat()
        scoreBase -= (this.getMisOrientedCount(type) * type.targetsPerPiece).toFloat()
        scoreBase -= (this.getBreakInCount(type) * type.numPiecesNoBuffer).toFloat()

        if (this.hasParity(type)) {
            scoreBase -= (0.25 * scoreBase).toFloat()
        }

        if (this.isBufferSolved(type)) {
            scoreBase -= (0.25 * scoreBase).toFloat()
        }

        return max(0.0, scoreBase)
    }

    fun getScrambleScore(): Double {
        val score = this.getPieceTypes().sumByDouble { it.numPieces * this.getScrambleScore(it) }
        val weight = this.getPieceTypes().sumBy { it.numPieces }

        return score / weight
    }

    fun getStatString(type: PieceType, indent: Boolean = false): String {
        val statString = StringBuilder("${type.mnemonic}: ")

        statString.append(if (this.hasParity(type)) "_" else if (indent) " " else "")

        val targets = this.getCycleLength(type)
        statString.append(targets)

        statString.append(if (this.isBufferSolved(type)) "*" else if (indent) " " else "")
        statString.append(if (this.isBufferSolvedAndMisOriented(type)) "*" else if (indent) " " else "")

        val numFloats = this.getBufferFloatNum(type)
        val floatsMax = this.getBackupBuffers(type).size
        statString.append(List(numFloats) {"\\"}.joinToString(""))

        if (indent) {
            statString.append(List(floatsMax - numFloats) {" "}.joinToString(""))
        }

        val maxTargets = type.numPiecesNoBuffer / 2 * 3 + type.numPiecesNoBuffer % 2
        val lenDiff = maxTargets.toString().length - targets.toString().length
        statString.append(List(lenDiff + 1) {" "}.joinToString(""))

        val breakInMax = type.numPiecesNoBuffer / 2
        val breakIns = this.getBreakInCount(type)

        statString.append(List(breakIns) {"#"}.joinToString(""))

        if (indent) {
            statString.append(List(breakInMax - breakIns) {" "}.joinToString(""))
        }

        val misOrientPreSolvedMax = type.numPiecesNoBuffer

        if (type.targetsPerPiece > 1) {
            val misOriented = this.getMisOrientedCount(type)

            if (indent || misOriented > 0) {
                statString.trySpace()
            }

            statString.append(List(misOriented) {"~"}.joinToString(""))

            if (indent) {
                statString.append(List(misOrientPreSolvedMax - misOriented) {" "}.joinToString(""))
            }
        }

        val preSolved = this.getPreSolvedCount(type)

        if (indent || preSolved > 0) {
            statString.trySpace()
        }

        statString.append(List(preSolved) {"+"}.joinToString(""))

        if (indent) {
            statString.append(List(misOrientPreSolvedMax - preSolved) {" "}.joinToString(""))
        }

        return if (indent) statString.toString() else statString.toString().trim()
    }

    fun getStatString(indent: Boolean = false): String {
        return this.getExecutionOrderPieceTypes()
                .reversed()
                .joinToString(" | ") { getStatString(it, indent) }
    }

    fun getShortStats(type: PieceType): String {
        return type.humanName + ": " + this.getCycleLength(type) + "@" + this.getBreakInCount(type) + " w/ " + this.getPreSolvedCount(type) + "-" + this.getMisOrientedCount(type) + "\\" + this.getBufferFloatNum(type) + " > " + this.hasParity(type)
    }

    fun getShortStats(): String {
        return this.getExecutionOrderPieceTypes()
                .reversed()
                .joinToString("\n") { this.getShortStats(it) }
    }

    fun getCycleLength(type: PieceType): Int {
        return this.cycles.getValue(type).size
    }

    fun getBreakInCount(type: PieceType): Int {
        return this.cycleCount.getValue(type)
    }

    fun isSingleCycle(type: PieceType): Boolean {
        return this.getBreakInCount(type) == 0
    }

    fun isSingleCycle(): Boolean {
        return this.getPieceTypes().all(this::isSingleCycle)
    }

    fun hasParity(type: PieceType): Boolean { // consider parity dependents
        return this.parities.getOrElse(type) { false }
    }

    fun hasParity(): Boolean {
        return this.getPieceTypes().any(this::hasParity)
    }

    fun isBufferSolved(type: PieceType, acceptMisOrient: Boolean = true): Boolean {
        val bufferSolved = this.preSolvedPieces.getValue(type)[0]

        return bufferSolved || acceptMisOrient && this.isBufferSolvedAndMisOriented(type)
    }

    fun isBufferSolvedAndMisOriented(type: PieceType): Boolean {
        var bufferTwisted = false

        val reference = this.cubies.getValue(type)
        val state = this.state.getValue(type)

        for (i in 1 until type.targetsPerPiece) {
            var bufferCurrentOrigin = true

            for (j in 0 until type.targetsPerPiece) {
                bufferCurrentOrigin = bufferCurrentOrigin and (state[reference[0][j]] == reference[0][(j + i) % type.targetsPerPiece])
            }

            bufferTwisted = bufferTwisted or bufferCurrentOrigin
        }

        return bufferTwisted
    }

    protected fun getCurrentBufferOrientation(type: PieceType): Int {
        val reference = this.cubies.getValue(type)
        val state = this.state.getValue(type)

        for (i in 1 until type.targetsPerPiece) {
            var bufferCurrentOrigin = true

            for (j in 0 until type.targetsPerPiece) {
                bufferCurrentOrigin = bufferCurrentOrigin and (state[reference[0][j]] == reference[0][(j + i) % type.targetsPerPiece])
            }

            if (bufferCurrentOrigin) {
                return i
            }
        }

        return 0
    }

    fun getBufferFloatNum(type: PieceType): Int {
        return this.bufferFloats.getValue(type).size
    }

    fun getBufferFloatNum(): Int {
        return this.getPieceTypes().sumBy(this::getBufferFloatNum)
    }

    fun hasBufferFloat(type: PieceType): Boolean {
        return this.getBufferFloatNum(type) > 0
    }

    fun hasBufferFloat(): Boolean {
        return this.getPieceTypes().any(this::hasBufferFloat)
    }

    fun getNoahtation(type: PieceType): String {
        val misOriented = "'".repeat(this.getMisOrientedCount(type))
        return "${type.mnemonic}: ${this.getCycleLength(type)}$misOriented"
    }

    fun getNoahtation(): String {
        return this.getExecutionOrderPieceTypes()
                .reversed()
                .joinToString(" / ") { this.getNoahtation(it) }
    }

    protected fun getBackupBuffers(type: PieceType): List<Int> {
        return this.backupBuffers.getValue(type).toList()
    }

    fun getRotations(): Algorithm {
        return this.scrambleOrientationPreMoves.copy()
    }

    fun matchesExecution(type: PieceType, filter: (Algorithm) -> Boolean): Boolean {
        if (this.algSource == null) {
            return false
        }

        val rawSolution = this.getSolutionTargets(type)
        var matches = true

        if (rawSolution == "Solved") {
            return true
        } else {
            for (case in this.compileSolutionCycles(type)) {
                if (case !is ThreeCycle) {
                    continue
                }

                val exists = this.algSource!!.getAlgorithms(type, case).any(filter)
                matches = matches and exists
            }
        }

        return matches
    }

    fun matchesExecution(filter: (Algorithm) -> Boolean): Boolean {
        return this.getPieceTypes().all { this.matchesExecution(it, filter) }
    }*/
}