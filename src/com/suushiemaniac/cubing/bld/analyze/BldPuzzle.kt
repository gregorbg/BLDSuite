package com.suushiemaniac.cubing.bld.analyze

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.lang.ImageStringReader
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.model.cycle.*
import com.suushiemaniac.cubing.bld.model.cycle.ComplexMisOrientCycle
import com.suushiemaniac.cubing.bld.model.cycle.MisOrientCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.model.source.AlgSource
import com.suushiemaniac.cubing.bld.util.ArrayUtil.countingArray
import com.suushiemaniac.cubing.bld.util.ArrayUtil.cycleLeft
import com.suushiemaniac.cubing.bld.util.ArrayUtil.deepInnerIndex
import com.suushiemaniac.cubing.bld.util.ArrayUtil.deepOuterIndex
import com.suushiemaniac.cubing.bld.util.ArrayUtil.filledArray
import com.suushiemaniac.cubing.bld.util.CollectionUtil.random
import com.suushiemaniac.cubing.bld.util.MapUtil.allTo
import com.suushiemaniac.cubing.bld.util.MapUtil.alwaysTo
import com.suushiemaniac.cubing.bld.util.MapUtil.increment
import com.suushiemaniac.cubing.bld.util.MapUtil.reset
import com.suushiemaniac.cubing.bld.util.SpeffzUtil
import com.suushiemaniac.cubing.bld.util.StringUtil.trySpace
import com.suushiemaniac.lang.json.JSON
import kotlin.math.max
import kotlin.math.pow

abstract class BldPuzzle(val model: TwistyPuzzle) : Cloneable {
    constructor(model: TwistyPuzzle, scramble: Algorithm) : this(model) {
        this.parseScramble(scramble)
    }

    val permutations = this.loadPermutations()
    val cubies = this.getDefaultCubies() - this.getOrientationPieceTypes()

    val state = initState()
    val lastScrambledState = initState().toMutableMap()

    val cycles = this.getPieceTypes() alwaysTo { mutableListOf<Int>() }
    val cycleCount = (this.getPieceTypes() alwaysTo 0).toMutableMap()

    val solvedPieces = this.getPieceTypes() allTo { it.numPieces.filledArray(false) }
    val preSolvedPieces = this.getPieceTypes() allTo { it.numPieces.filledArray(false) }
    val misOrientedPieces = this.getPieceTypes() allTo { pt -> pt.targetsPerPiece.filledArray { pt.numPieces.filledArray(false) } }

    val parities = (this.getPieceTypes() alwaysTo false).toMutableMap()

    var scramble: Algorithm = SimpleAlg()
    var scrambleOrientationPreMoves: Algorithm = SimpleAlg()

    var letterPairLanguage: String = System.getProperty("user.language")

    val letterSchemes = (this.getPieceTypes() alwaysTo SpeffzUtil.FULL_SPEFFZ).toMutableMap()
    val avoidBreakIns = this.getPieceTypes() alwaysTo true
    val optimizeBreakIns = this.getPieceTypes() alwaysTo true

    val mainBuffers = (this.getPieceTypes() allTo { this.getDefaultCubies().getValue(it)[0][0] }).toMutableMap()
    val backupBuffers = this.getPieceTypes() alwaysTo { mutableListOf<Int>() }
    val bufferFloats = this.getPieceTypes() alwaysTo { mutableMapOf<Int, Int>() }

    var algSource: AlgSource? = null
    var misOrientMethod = MisOrientMethod.SOLVE_DIRECT
        set(value) {
            field = value
            this.reSolve()
        }

    fun loadPermutations(): Map<Move, Map<PieceType, Array<Int>>> {
        val filename = "permutations/$model.json"
        val fileURL = this.javaClass.getResource(filename)

        val json = JSON.fromURL(fileURL)

        val permutations = hashMapOf<Move, Map<PieceType, Array<Int>>>()

        for (key in json.nativeKeySet()) {
            val typeMap = hashMapOf<PieceType, Array<Int>>()
            val moveJson = json.get(key)

            for (type in this.getPieceTypes(true)) {
                val permutationList = moveJson.get(type.name).nativeList(JSON::intValue)
                val permutationArray = permutationList.toTypedArray()

                typeMap[type] = permutationArray
            }

            val move = model.reader.parse(key).firstMove()
            permutations[move] = typeMap
        }

        return permutations
    }

    protected fun initState(): Map<PieceType, Array<Int>> {
        return this.getPieceTypes(true) allTo { (it.numPieces * it.targetsPerPiece).countingArray() }
    }

    protected fun saveState(type: PieceType) {
        val current = this.state.getValue(type)
        this.lastScrambledState[type] = current.copyOf()
    }

    open fun parseScramble(scramble: Algorithm) {
        this.resetPuzzle()

        this.scramble = scramble

        this.scramblePuzzle(scramble)
        this.solvePuzzle()
    }

    fun reSolve() {
        this.parseScramble(this.scramble)
    }

    fun getPieceTypes(withOrientationModel: Boolean = false): List<PieceType> {
        val pieceTypes = this.getPermutationPieceTypes().toMutableList()

        if (withOrientationModel) {
            pieceTypes.addAll(this.getOrientationPieceTypes())
        }

        return pieceTypes
    }

    protected fun resetPuzzle() {  // TODO make more concise / beautiful
        this.state.forEach { (_, state) -> state.sort() }
        this.lastScrambledState.forEach { (_, state) -> state.sort() }

        this.cycles.forEach { (_, state) -> state.clear() }
        this.cycleCount.reset { 0 }

        this.solvedPieces.forEach { (_, state) -> state.fill(false) }
        this.preSolvedPieces.forEach { (_, state) -> state.fill(false) }
        this.misOrientedPieces.forEach { (_, state) -> state.forEach { it.fill(false) } }

        this.parities.reset { false }

        this.mainBuffers.forEach { (type, newBuffer) -> this.cycleCubiesForBuffer(type, newBuffer) }
        this.bufferFloats.forEach { (_, state) -> state.clear() }
    }

    fun solvePuzzle() {
        this.getOrientationPieceTypes().forEach { this.saveState(it) }

        this.reorientPuzzle()
        this.getPieceTypes().forEach { this.saveState(it) }
        this.getPieceTypes().forEach { this.solvePieces(it) }
    }

    protected fun reorientPuzzle() {
        this.scrambleOrientationPreMoves = this.getReorientationMoves()
        this.scrambleOrientationPreMoves.forEach(this::permute)
    }

    protected open fun scramblePuzzle(scramble: Algorithm) {
        val preMoveScramble = this.getSolvingOrientationPremoves().merge(scramble)

        preMoveScramble.asIterable()
                .filter(this.permutations.keys::contains)
                .forEach(this::permute)
    }

    protected fun permute(permutation: Move) {
        for (type in this.getPieceTypes(true)) {
            val current = this.state.getValue(type)
            val perm = this.permutations.getValue(permutation).getValue(type)

            this.applyPermutations(current, perm)
        }
    }

    protected fun applyPermutations(current: Array<Int>, perm: Array<Int>) {
        val exchanges = perm.size.filledArray(-1)

        for (i in exchanges.indices) {
            if (perm[i] != -1) {
                exchanges[perm[i]] = current[i]
            }
        }

        for (i in exchanges.indices) {
            if (exchanges[i] != -1) {
                current[i] = exchanges[i]
            }
        }
    }

    protected open fun getBreakInPermutationsAfter(piece: Int, type: PieceType): List<Int> {
        val targetCount = type.numPieces
        return targetCount.countingArray().asList().subList(1, targetCount)
    }

    protected open fun getBreakInOrientationsAfter(piece: Int, type: PieceType): Int {
        return 0
    }

    fun getBuffer(type: PieceType): Int {
        return if (type is LetterPairImage) 0 else this.cubies.getValue(type)[0][0]
    }

    protected fun cycleCubiesForBuffer(type: PieceType, newBuffer: Int): Boolean {
        val cubies = this.cubies[type]

        if (cubies != null) {
            val outer = cubies.deepOuterIndex(newBuffer)

            if (outer > -1) {
                val inner = cubies.deepInnerIndex(newBuffer)

                if (inner > -1) {
                    for (i in 0 until outer) cubies.cycleLeft()
                    for (i in 0 until inner) cubies[0].cycleLeft()

                    return true
                }
            }
        }

        return false
    }

    protected fun increaseCycleCount(type: PieceType) {
        this.cycleCount.increment(type)
    }

    protected fun getLastTarget(type: PieceType): Int {
        return this.cycles.getValue(type).lastOrNull() ?: -1
    }

    fun compileSolutionCycles(type: PieceType): List<PieceCycle> {
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

        for (i in 0 until type.targetsPerPiece) {
            val misOrients = this.getMisOrientedPieces(type, i)

            when {
                this.misOrientMethod == MisOrientMethod.SINGLE_TARGET -> {
                    val cubies = this.cubies.getValue(type)

                    for (piece in misOrients) {
                        val outer = cubies.deepOuterIndex(piece)
                        val inner = cubies.deepInnerIndex(piece)

                        cycles.add(ThreeCycle(mainBuffer, piece, cubies[outer][(inner + i) % type.targetsPerPiece]))
                    }
                }
                this.misOrientMethod == MisOrientMethod.SOLVE_DIRECT ->
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
        val ref = this.cubies.getValue(type)

        val accu = StringBuilder()
        var lastBuffer = -1

        for (cycle in currentCycles) {
            if (lastBuffer != cycle.buffer) {
                val mainBuffer = lastBuffer < 0
                lastBuffer = cycle.buffer

                if (!mainBuffer) {
                    val position = SpeffzUtil.speffzToSticker(SpeffzUtil.normalize(letters[cycle.buffer], letters), type)

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
            val bufferSticker = SpeffzUtil.speffzToSticker(SpeffzUtil.normalize(lettering[it.buffer], lettering), type)

            // val caseAlgs = this.algSource!!.getAlgorithms(type, cycle)
            val caseAlgs = this.algSource!!.getAlgorithms(type, it)
            return@map caseAlgs.toList().random()
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

    fun getLetterPairCorrespondant(type: PieceType, piece: Int): String {
        val lettering = this.getLetteringScheme(type)
        return this.getCorrespondents(type, piece).joinToString("") { lettering[it] }
    }

    fun getOrientationSides(type: PieceType, piece: Int): Set<Int> {
        return this.cubies.getValue(type)[piece].map { this.getOrientationSide(type, it) }.toSet()
    }

    fun getOrientationSide(type: PieceType, target: Int): Int {
        val piecesPerSide = (type.numPieces * type.targetsPerPiece) / this.getOrientationSideCount()
        return target / piecesPerSide
    }

    fun findCurrentTargetPosition(type: PieceType, piece: Int, side: Int): Int {
        val target = this.cubies.getValue(type)[piece].find { this.getOrientationSide(type, it) == side }
        return this.state.getValue(type).indexOf(target)
    }

    fun findCurrentOrientationSide(type: PieceType, piece: Int, side: Int): Int {
        return this.getOrientationSide(type, this.findCurrentTargetPosition(type, piece, side))
    }

    fun getPiecesOnOrientationSide(type: PieceType, side: Int): List<Int> {
        return this.cubies.getValue(type).indices.filter { this.getOrientationSides(type, it).contains(side) }
    }

    fun getCorrespondents(type: PieceType, target: Int): List<Int> {
        val outer = this.getPermutationPiece(type, target)

        if (outer > -1) {
            val pieceModel = this.cubies.getValue(type)[outer]
            return pieceModel.toMutableList() - target
        }

        return listOf()
    }

    fun getPermutationPiece(type: PieceType, target: Int): Int {
        return this.cubies.getValue(type).deepOuterIndex(target)
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

    open fun hasParity(type: PieceType): Boolean {
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
        val state = this.lastScrambledState.getValue(type)

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

    protected fun getBackupBuffers(type: PieceType): List<Int> {
        return this.backupBuffers.getValue(type).toList()
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

    fun setLetteringScheme(type: PieceType, newScheme: Array<String>): Boolean {
        if (this.getLetteringScheme(type).size == newScheme.size) {
            this.letterSchemes[type] = newScheme

            this.reSolve()
            return true
        }

        return false
    }

    fun setBuffer(type: PieceType, newBuffer: Int): Boolean {
        if (this.cycleCubiesForBuffer(type, newBuffer)) {
            this.mainBuffers[type] = newBuffer

            this.reSolve()
            return true
        }

        return false
    }

    fun setBuffer(type: PieceType, newBuffer: String): Boolean {
        val letterScheme = this.getLetteringScheme(type)

        return letterScheme.contains(newBuffer)
                && this.setBuffer(type, letterScheme.indexOf(newBuffer))
    }

    fun registerFloatingBuffer(type: PieceType, newBuffer: Int): Boolean {
        val floatingBuffers = this.backupBuffers.getValue(type)

        if (!floatingBuffers.contains(newBuffer) && this.mainBuffers[type] != newBuffer) {
            floatingBuffers.add(newBuffer)

            this.reSolve()
            return true
        }

        return false
    }

    fun registerFloatingBuffer(type: PieceType, newBuffer: String): Boolean {
        val letterScheme = this.getLetteringScheme(type)

        return letterScheme.contains(newBuffer)
                && this.registerFloatingBuffer(type, letterScheme.indexOf(newBuffer))
    }

    fun dropFloatingBuffers(type: PieceType) {
        this.backupBuffers.getValue(type).clear()

        this.reSolve()
    }

    fun dropFloatingBuffers() {
        this.getPieceTypes().forEach(this::dropFloatingBuffers)
    }

    protected abstract fun solvePieces(type: PieceType)

    protected abstract fun getPermutationPieceTypes(): List<PieceType>

    protected abstract fun getOrientationPieceTypes(): List<PieceType>

    protected abstract fun getExecutionOrderPieceTypes(): List<PieceType>

    protected abstract fun getOrientationSideCount(): Int

    protected abstract fun getDefaultCubies(): Map<PieceType, Array<Array<Int>>>

    protected abstract fun getReorientationMoves(): Algorithm

    protected abstract fun getSolvingOrientationPremoves(): Algorithm

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
    }

    fun solves(type: PieceType, alg: Algorithm, solutionCase: String, pure: Boolean = true): Boolean {
        val currentScramble = this.scramble

        this.parseScramble(alg.inverse())

        var solves = this.getSolutionTargets(type).equals(solutionCase.replace("\\s".toRegex(), ""), ignoreCase = true) && this.getMisOrientedCount(type) == 0

        if (pure) {
            val remainingTypes = this.getPieceTypes().toMutableList()
            remainingTypes.remove(type)

            for (remainingType in remainingTypes) {
                solves = solves and (this.getSolutionTargets(remainingType) == "Solved" && this.getMisOrientedCount(remainingType) == 0)
            }
        }

        this.parseScramble(currentScramble)

        return solves
    }

    public override fun clone(): BldPuzzle {
        return super.clone() as BldPuzzle // TODO deep clone
    }

    fun clone(scramble: Algorithm): BldPuzzle {
        val clone = this.clone()
        clone.parseScramble(scramble)

        return clone
    }

    enum class MisOrientMethod {
        SOLVE_DIRECT, SINGLE_TARGET
    }
}