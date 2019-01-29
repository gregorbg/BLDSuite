package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.analyze.BldAnalysis
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.AlgSource
import com.suushiemaniac.cubing.bld.model.cycle.*
import com.suushiemaniac.cubing.bld.optim.BreakInOptimizer
import com.suushiemaniac.cubing.bld.util.CollectionUtil.powerset
import com.suushiemaniac.cubing.bld.util.CollectionUtil.permutations
import com.suushiemaniac.cubing.bld.util.CollectionUtil.countingList
import com.suushiemaniac.cubing.bld.util.CollectionUtil.topologicalSort
import com.suushiemaniac.cubing.bld.util.CollectionUtil.countOf
import com.suushiemaniac.cubing.bld.util.StringUtil.splitAtWhitespace
import com.suushiemaniac.cubing.bld.util.StringUtil.splitLines
import com.suushiemaniac.cubing.bld.util.MathUtil.pMod
import com.suushiemaniac.cubing.bld.util.Piece
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.countEquals
import com.suushiemaniac.cubing.bld.util.deepEquals

import java.io.File

open class GPuzzle(reader: NotationReader, kCommandMap: Map<String, List<String>>, val bldCommandMap: Map<String, List<String>>) : KPuzzle(reader, kCommandMap) {
    constructor(reader: NotationReader, kCommandMap: Map<String, List<String>>, bldFile: File) : this(reader, kCommandMap, groupByCommand(bldFile.readLines()))
    constructor(reader: NotationReader, defFile: File, bldFile: File) : this(reader, groupByCommand(defFile.readLines()), groupByCommand(bldFile.readLines()))

    val letterSchemes = this.loadLetterSchemes()

    val allBuffers = this.loadBuffers()
    val mainBuffers = this.allBuffers.mapValues { it.value.first() }

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

        nonCancelling.flatMap { it.allMoves().permutations() }.map(::SimpleAlg).toSet().sortedBy { it.algLength() }
    }

    // FILE LOADING

    fun loadLetterSchemes(): Map<PieceType, Array<String>> {
        val letterLines = this.bldCommandMap.getValue("Lettering").first().splitLines()
        return loadFilePosition(this.pieceTypes, letterLines.drop(1)) { it.first }
    }

    fun loadBuffers(): Map<PieceType, List<Int>> {
        val bufferCommands = this.bldCommandMap.getValue("Buffer")

        val collectionMap = mutableListOf<Pair<PieceType, Int>>()

        for (bufferDef in bufferCommands) {
            val defScheme = bufferDef.splitAtWhitespace().drop(1)
            val ptName = defScheme.first()

            val pieceType = this.findPieceTypeByName(ptName)

            val bufferOrient = defScheme.last().toInt()
            val bufferPerm = defScheme.dropLast(1).last().toInt()

            collectionMap += pieceType to pieceToTarget(pieceType, bufferPerm - 1, bufferOrient)
        }

        return collectionMap.groupBy(Pair<PieceType, Int>::first, Pair<PieceType, Int>::second)
    }

    fun loadReorientMethod(): String {
        return this.bldCommandMap.getValue("Orientation").first().splitLines().first().splitAtWhitespace().last()
    }

    fun loadReorientState(): PuzzleState {
        val stateLines = this.bldCommandMap.getValue("Orientation").first().splitLines()

        return loadKPosition(this.pieceTypes, stateLines.drop(1))
    }

    fun loadMisOrientMethod(): String {
        return this.bldCommandMap.getValue("MisOrient").first().splitAtWhitespace().last()
    }

    fun loadParityDependencyFixes(): Map<PieceType, PuzzleState> {
        val dependencyFixDescriptions = this.bldCommandMap["ParityDependency"] ?: emptyList()

        return dependencyFixDescriptions.map { it.splitLines() }.map { it[0].splitAtWhitespace()[1] to it.drop(1) }
                .toMap().mapKeys { this.findPieceTypeByName(it.key) }
                .mapValues { loadKPosition(this.pieceTypes, it.value) }
    }

    fun loadParityFirstPieceTypes(): List<PieceType> {
        val firstPieceTypes = this.bldCommandMap["ParityFirst"]?.first()?.splitAtWhitespace() ?: emptyList()

        return firstPieceTypes.drop(1).map { this.findPieceTypeByName(it) }
    }

    fun loadExecutionPieceTypes(): List<PieceType> {
        return this.bldCommandMap.getValue("Execution").first().splitAtWhitespace().drop(1).map { this.findPieceTypeByName(it) }
    }

    private fun findPieceTypeByName(name: String): PieceType {
        return this.pieceTypes.find { it.name == name }!!
    }

    // LETTER SCHEME METHODS

    fun targetToLetter(type: PieceType, target: Int) = this.letterSchemes.getValue(type)[target]
    fun getLetterPairCorrespondants(type: PieceType, perm: Int) = permToTargets(type, perm).map { this.targetToLetter(type, it) }

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

    protected fun targetCurrentlySolved(type: PieceType, target: Int) = target in this.getSolutionSpots(type, this.currentlyAtTarget(type, target))
    protected fun targetCurrentlyPermuted(type: PieceType, target: Int) = target in this.getSolutionSpots(type, this.currentlyAtTarget(type, target))
            .map { targetToPerm(type, it) }
            .flatMap { permToTargets(type, it) }

    protected fun currentPermOrientation(type: PieceType, perm: Int) = this.puzzleState.getValue(type)[perm].orientation

    // BUFFER HELPERS

    fun getMainBufferTarget(type: PieceType) = this.mainBuffers.getValue(type)
    fun getMainBufferPerm(type: PieceType) = targetToPerm(type, this.getMainBufferTarget(type))

    // CYCLE BUILDERS

    protected fun compileTargetChain(type: PieceType): List<Int> {
        val accumulate = mutableListOf<Int>()

        // TODO mark/use buffer float?
        return generateSequence {
            this.getNextTarget(type, this.getMainBufferTarget(type), accumulate)?.also { accumulate.add(it) }
        }.toList()
    }

    protected fun compileMisOrientedPieces(type: PieceType, orientation: Int): List<Int> {
        val prePermuted = type.numTargets.countingList().filter { this.targetCurrentlyPermuted(type, it) }.map { targetToPerm(type, it) }.distinct()
        val preSolved = type.numTargets.countingList().filter { this.targetCurrentlySolved(type, it) }.map { targetToPerm(type, it) }.distinct()

        val misOriented = prePermuted - preSolved - this.getMainBufferPerm(type)

        return misOriented.filter { this.currentPermOrientation(type, it) == orientation }
    }

    protected open fun compilePermuteSolutionCycles(type: PieceType): List<PieceCycle> {
        val targets = this.compileTargetChain(type)
        val mainBuffer = this.getMainBufferTarget(type)

        val cycles = mutableListOf<PieceCycle>()

        val chunks = if (this.parityFirstPieceTypes.contains(type))
            targets.reversed().chunked(2).reversed().map { it.reversed() } else
            targets.chunked(2)

        for (c in chunks) {
            if (c.size == 2) {
                cycles.add(ThreeCycle(mainBuffer, c[0], c[1]))
            } else {
                cycles.add(ParityCycle(mainBuffer, c[0]))
            }
        }

        return cycles
    }

    fun compileSolutionCycles(type: PieceType): List<PieceCycle> {
        val cycles = mutableListOf<PieceCycle>()
        cycles.addAll(this.compilePermuteSolutionCycles(type))

        for (i in 1 until type.orientations) {
            val misOrients = this.compileMisOrientedPieces(type, i)

            when (this.misOrientMethod) {
                "Single" -> {
                    for (piece in misOrients) {
                        val (perm, orient) = targetToPiece(type, piece)
                        val next = pieceToTarget(type, perm, (orient + i) % type.orientations)

                        cycles += SingleMisOrientCycle(i, piece, next)
                    }
                }
                "Compound" ->
                    cycles += misOrients.map { MisOrientPiece(pieceToTarget(type, it, i), i) }
            }
        }

        return cycles
    }

    private fun dumpDebug(): Map<PieceType, List<String>> {
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

        val parityRelevantCycles = mutableMapOf<PieceType, List<PieceCycle>>()

        for (type in parityDependents.topologicalSort()) {
            val solutionCycles = this.compileSolutionCycles(type)

            if (solutionCycles.any { it is ParityCycle }) {
                if (type in this.parityDependencyFixes) {
                    movePuzzle(this.solvedState, this.parityDependencyFixes.getValue(type))
                }
            }

            parityRelevantCycles[type] = solutionCycles
        }

        val cycles = this.executionPieceTypes.associateWith { parityRelevantCycles.getOrDefault(it, this.compileSolutionCycles(it)) }

        return BldAnalysis(this.reader, reorient, cycles, this.mainBuffers, this.letterSchemes, this.algSource)
    }

    fun getNextTarget(type: PieceType, buffer: Int, previous: List<Int>): Int? {
        val bufferAdjacency = this.getSolutionSpots(type, buffer).flatMap { adjacentTargets(type, it) }
        val targetedPerms = previous.map { targetToPerm(type, it) }

        val cycleShift = listOf(this.getMainBufferTarget(type)) + previous

        val currentCycleStart = cycleShift.mapIndexed { i, t ->
            this.currentlyAtTarget(type, t) in bufferAdjacency
                    || targetedPerms.subList(0, i).countOf(targetToPerm(type, t)) > 1
        }

        val nextTargetChoice = if (currentCycleStart.last()) {
            this.getBreakInTargetsAfter(type, buffer, cycleShift.last(), previous)
        } else {
            val inBuffer = this.currentlyAtTarget(type, cycleShift.last())
            this.getSolutionSpots(type, inBuffer).sortedBy { this.targetToLetter(type, it) }
        }

        val unsolvedTargets = nextTargetChoice.filter { !this.targetCurrentlySolved(type, it) }

        val openBreakInPerms = currentCycleStart.indices.drop(1).filter { currentCycleStart[it - 1] }
                .map { targetToPerm(type, cycleShift[it]) }
                .filter { targetedPerms.countOf(it) == 1 }

        val notTargeted = unsolvedTargets.filter { targetToPerm(type, it) !in (targetedPerms - openBreakInPerms) }
        val favorableTargets = notTargeted.filter { buffer !in this.getSolutionSpots(type, this.currentlyAtTarget(type, it)) }

        return favorableTargets.firstOrNull() ?: notTargeted.firstOrNull()
    }

    protected open fun getBreakInTargetsAfter(type: PieceType, buffer: Int, target: Int, targeted: List<Int>): List<Int> {
        val preSolved = type.numTargets.countingList().filter { this.targetCurrentlyPermuted(type, it) }

        if (this.algSource == null) {
            val selection = type.numTargets.countingList() - targeted - adjacentTargets(type, buffer) - preSolved
            val (buf, rem) = selection.partition { it in this.getSolutionSpots(type, buffer) }

            return buf.sortedBy { this.targetToLetter(type, it) } + rem.sortedBy { this.targetToLetter(type, it) }
        }

        return BreakInOptimizer(this.algSource!!, this.reader).optimizeBreakInTargetsAfter(target, buffer, type) - preSolved
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

        val solves = analysis.solutionCycles.getValue(type) == case

        val remainingTypes = this.executionPieceTypes - type
        val remainingOkay = remainingTypes.all { !pure || analysis.solutionCycles.getValue(it).isEmpty() }

        return solves && remainingOkay
    }
    
    companion object {
        fun pieceToTarget(type: PieceType, perm: Int, orient: Int) = (perm * type.orientations) + orient
        fun targetToPiece(type: PieceType, target: Int) = Piece(target / type.orientations, target % type.orientations)

        fun targetToPerm(type: PieceType, target: Int) = targetToPiece(type, target).permutation
        fun permToTargets(type: PieceType, perm: Int) = type.orientations.countingList().map { pieceToTarget(type, perm, it) }

        fun adjacentTargets(type: PieceType, target: Int) = permToTargets(type, targetToPerm(type, target))

        fun preInstalledConfig(tag: String, person: String) = File(GPuzzle::class.java.classLoader.getResource("gpuzzle/$person/$tag.bld").toURI())
    }
}
