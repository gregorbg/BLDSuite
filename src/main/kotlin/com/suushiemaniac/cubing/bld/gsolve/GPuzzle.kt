package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.analyze.BldAnalysis
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.AlgSource
import com.suushiemaniac.cubing.bld.model.cycle.*
import com.suushiemaniac.cubing.bld.optim.BreakInOptimizer
import com.suushiemaniac.cubing.bld.util.*
import com.suushiemaniac.cubing.bld.util.CollectionUtil.powerset
import com.suushiemaniac.cubing.bld.util.CollectionUtil.permutations
import com.suushiemaniac.cubing.bld.util.CollectionUtil.countingList
import com.suushiemaniac.cubing.bld.util.CollectionUtil.topologicalSort
import com.suushiemaniac.cubing.bld.util.CollectionUtil.countOf
import com.suushiemaniac.cubing.bld.util.StringUtil.splitLines
import com.suushiemaniac.cubing.bld.util.MathUtil.pMod

import java.io.File

open class GPuzzle(reader: NotationReader, kCommandMap: CommandMap, val bldCommandMap: CommandMap) : KPuzzle(reader, kCommandMap) {
    val letterSchemes = this.loadLetterSchemes()

    val buffers = this.loadBuffers()

    val reorientMethod = this.loadReorientMethod()
    val reorientState = this.loadReorientState()

    val misOrientMethod = this.loadMisOrientMethod()

    val parityDependencyFixes = this.loadParityDependencyFixes()

    val parityFirstPieceTypes = this.loadParityFirstPieceTypes()
    val executionPieceTypes = this.loadExecutionPieceTypes()

    var algSource: AlgSource? = null

    private val bruteForceRotations by lazy {
        val reOrientations = this.moveDefinitions.keys.filter { it.plane.isRotation }.toSet().powerset().filter { it.size in 1..2 }
        val nonCancelling = reOrientations.map { SimpleAlg(it.toList()) }.toSet()

        nonCancelling.flatMap { it.permutations() }.map { SimpleAlg(it) }.toMutableList().apply { add(SimpleAlg()) }.toSet().sortedBy { it.size }
    }

    // FILE LOADING

    fun loadLetterSchemes(): Map<PieceType, Array<String>> {
        val letterLines = this.bldCommandMap.getValue("Lettering").first()

        return loadFilePosition(this.pieceTypes, letterLines[0].splitLines()) { it.first }
    }

    fun loadBuffers(): Map<PieceType, List<Int>> {
        val bufferCommands = this.bldCommandMap.getValue("Buffer")

        return bufferCommands.groupBy(
                { this.findPieceTypeByName(it[0]) },
                { it.drop(1).map(String::toInt) })
                .mapValues { it.value.map { b -> pieceToTarget(it.key, b[0] - 1, b[1]) } }
    }

    fun loadReorientMethod(): String {
        return this.bldCommandMap.getValue("Orientation").first().first()
    }

    fun loadReorientState(): PuzzleState {
        val stateLines = this.bldCommandMap.getValue("Orientation").first()

        return loadKPosition(this.pieceTypes, stateLines[1].splitLines())
    }

    fun loadMisOrientMethod(): String {
        return this.bldCommandMap.getValue("MisOrient").first().first()
    }

    fun loadParityDependencyFixes(): Map<PieceType, PuzzleState> {
        val dependencyFixDescriptions = this.bldCommandMap["ParityDependency"] ?: emptyList()

        return dependencyFixDescriptions.associateBy(
                { this.findPieceTypeByName(it[0]) },
                { loadKPosition(this.pieceTypes, it[1].splitLines()) })
    }

    fun loadParityFirstPieceTypes(): List<PieceType> {
        val firstPieceTypes = this.bldCommandMap["ParityFirst"] ?: emptyList()

        return firstPieceTypes.firstOrNull()?.map(this::findPieceTypeByName) ?: emptyList()
    }

    fun loadExecutionPieceTypes(): List<PieceType> {
        return this.bldCommandMap.getValue("Execution").first().map(this::findPieceTypeByName)
    }

    // LETTER SCHEME METHODS

    protected fun targetToLetter(type: PieceType, target: Int) = this.letterSchemes.getValue(type)[target]
    fun getLetterPairCorrespondants(type: PieceType, perm: Int) = permToTargets(type, perm).map { this.targetToLetter(type, it) }

    // TODO target/perm to posNotation conversion?!

    // K-STYLE METHODS

    protected fun currentlyAtTarget(type: PieceType, target: Int): Int {
        val state = this.puzzleState.getValue(type)
        val (lookupPerm, lookupOrient) = targetToPiece(type, target)

        val possiblePermSpot = this.solvedState.getValue(type).indexOfFirst { it.permutation == state[lookupPerm].permutation }

        return pieceToTarget(type, possiblePermSpot, (lookupOrient - state[lookupPerm].orientation) pMod type.orientations)
    }

    protected fun getSolutionSpots(type: PieceType, target: Int): List<Int> {
        val ref = this.solvedState.getValue(type)
        val (currentPerm, currentOrient) = targetToPiece(type, target)

        val possiblePermSpots = ref.indices.filter { ref[it].permutation == ref[currentPerm].permutation }

        return possiblePermSpots.map { pieceToTarget(type, it, (ref[it].orientation + currentOrient) % type.orientations) }
    }

    protected fun getSolutionAdjacency(type: PieceType, target: Int) = this.getSolutionSpots(type, target).flatMap { adjacentTargets(type, it) }

    protected fun targetCurrentlySolved(type: PieceType, target: Int) = target in this.getSolutionSpots(type, this.currentlyAtTarget(type, target))

    // BUFFER HELPERS

    fun getBufferTargets(type: PieceType) = this.buffers.getValue(type)

    // CYCLE BUILDERS

    protected fun compileTargetChain(type: PieceType): List<StickerTarget> {
        // TODO mark/use buffer float?
        val buffers = this.getBufferTargets(type).toMutableList()
        val mainBuffer = buffers.first()

        val bufferPreSolved = this.currentlyAtTarget(type, mainBuffer) in this.getSolutionAdjacency(type, mainBuffer)
        val defaultTarget = StickerTarget(mainBuffer, mainBuffer, bufferPreSolved)

        val accumulate = mutableListOf(defaultTarget)

        return generateSequence {
            this.getNextTarget(type, buffers.first(), accumulate)?.also { accumulate += it }
        }.toList().mapIndexed { i, t -> t.copy(isCycleBreak = accumulate[i].isCycleBreak) } // FIXME (long-term) not shift isCycleBreak
    }

    fun dumpTargets(): Map<PieceType, List<String>> {
        return this.letterSchemes.keys.associateWith { pt ->
            pt.permutations.countingList().map {
                this.targetToLetter(pt, this.currentlyAtTarget(pt, pieceToTarget(pt, it, 0)))
            }
        }
    }

    fun getAnalysis(scramble: Algorithm): BldAnalysis {
        resetState(this.solvedState, this.loadSolvedState())
        resetState(this.puzzleState, this.solvedState)

        this.applyScramble(scramble)

        val reorient = this.getReorientationMoves().also { this.applyScramble(it) }

        val parityDepKeys = this.parityDependencyFixes.mapValues { it.value.keys }
        val parityDependents = parityDepKeys.values.flatten().associateWith { parityDepKeys.filterValues { v -> it in v }.keys }

        val parityRelevantCycles = mutableMapOf<PieceType, List<StickerTarget>>()

        for (type in parityDependents.topologicalSort()) {
            val solutionCycles = this.compileTargetChain(type)

            if (solutionCycles.size % 2 != 0) {
                if (type in this.parityDependencyFixes) {
                    movePuzzle(this.solvedState, this.parityDependencyFixes.getValue(type))
                }
            }

            parityRelevantCycles[type] = solutionCycles
        }

        val cycles = this.executionPieceTypes.associateWith { parityRelevantCycles.getOrDefault(it, this.compileTargetChain(it)) }

        return BldAnalysis(this.reader, reorient, cycles, this.letterSchemes, this.algSource)
    }

    fun getNextTarget(type: PieceType, buffer: Int, previous: List<StickerTarget>): StickerTarget? {
        val targetedPerms = previous.map { targetToPerm(type, it.target) }

        val nextTargetChoice = if (previous.last().isCycleBreak) {
            this.getBreakInTargets(type, buffer, previous)
        } else {
            val inBuffer = this.currentlyAtTarget(type, previous.last().target)
            this.getSolutionSpots(type, inBuffer).sortedBy { this.targetToLetter(type, it) }
        }

        val unsolvedTargets = nextTargetChoice.filter { !this.targetCurrentlySolved(type, it) }

        val openBreakInPerms = previous.zipWithNext().filter { it.first.isCycleBreak }
                .map { targetToPerm(type, it.second.target) }
                .filter { targetedPerms.countOf(it) == 1 }

        val notTargeted = unsolvedTargets.filter { targetToPerm(type, it) !in (targetedPerms - openBreakInPerms) }
        val favorableTargets = notTargeted.filter { buffer !in this.getSolutionSpots(type, this.currentlyAtTarget(type, it)) }

        val nextTarget = favorableTargets.firstOrNull() ?: notTargeted.firstOrNull()

        return nextTarget?.let {
            val targetingBuffer = this.currentlyAtTarget(type, it) in this.getSolutionAdjacency(type, buffer)
            val closingCycle = targetToPerm(type, it) in targetedPerms

            StickerTarget(it, buffer, targetingBuffer || closingCycle)
        }
    }

    protected open fun getBreakInTargets(type: PieceType, buffer: Int, history: List<StickerTarget>): List<Int> {
        val preSolved = type.numTargets.countingList().filter { this.targetCurrentlySolved(type, it) }

        if (this.algSource == null) {
            val selection = type.numTargets.countingList() - history.map { it.target } - adjacentTargets(type, buffer) - preSolved
            val (buf, rem) = selection.partition { it in this.getSolutionSpots(type, buffer) }

            return buf.sortedBy { this.targetToLetter(type, it) } + rem.sortedBy { this.targetToLetter(type, it) }
        }

        return BreakInOptimizer(this.algSource!!, this.reader).optimizeBreakInTargetsAfter(history.last().target, buffer, type) - preSolved
    }

    protected fun getReorientationMoves() = when {
        this.reorientMethod == "Fixed" -> this.bruteForceRotations.find { this.hypotheticalScramble(it).deepEquals(this.reorientState, true) }
        this.reorientMethod == "Dynamic" -> this.bruteForceRotations.maxBy {
            val rotatedState = this.hypotheticalScramble(it)

            val solvedPieces = rotatedState.countEquals(this.solvedState.filterKeys { pt -> pt in reorientState.keys })
            val solvedPrefPieces = rotatedState.countEquals(this.reorientState)

            2 * solvedPieces + 3 * solvedPrefPieces
        }
        else -> SimpleAlg()
    } ?: SimpleAlg()

    fun solves(type: PieceType, alg: Algorithm, case: List<PieceCycle>, pure: Boolean = true): Boolean {
        val analysis = this.getAnalysis(alg)

        val solves = analysis.solutionTargets.getValue(type) == case

        val remainingTypes = this.executionPieceTypes - type
        val remainingOkay = remainingTypes.all { !pure || analysis.solutionTargets.getValue(it).isEmpty() }

        return solves && remainingOkay
    }
    
    companion object {
        fun pieceToTarget(type: PieceType, perm: Int, orient: Int) = (perm * type.orientations) + orient
        fun targetToPiece(type: PieceType, target: Int) = Piece(target / type.orientations, target % type.orientations)

        fun targetToPerm(type: PieceType, target: Int) = targetToPiece(type, target).permutation
        fun permToTargets(type: PieceType, perm: Int) = type.orientations.countingList().map { pieceToTarget(type, perm, it) }

        fun targetToOrient(type: PieceType, target: Int) = targetToPiece(type, target).orientation
        fun orientToTargets(type: PieceType, orient: Int) = type.permutations.countingList().map { pieceToTarget(type, it, orient) }

        fun adjacentTargets(type: PieceType, target: Int) = permToTargets(type, targetToPerm(type, target))

        fun preInstalledConfig(tag: String, person: String) = loadCommandMap(File(GPuzzle::class.java.classLoader.getResource("gpuzzle/$person/$tag.bld").toURI()))
    }
}
