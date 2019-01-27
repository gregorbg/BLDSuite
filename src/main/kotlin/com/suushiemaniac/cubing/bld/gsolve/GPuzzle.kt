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
import com.suushiemaniac.cubing.bld.util.StringUtil.splitAtWhitespace
import com.suushiemaniac.cubing.bld.util.MathUtil.pMod
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.clone
import com.suushiemaniac.cubing.bld.util.countEquals
import com.suushiemaniac.cubing.bld.util.deepEquals

import java.io.File

open class GPuzzle(reader: NotationReader, kCommandMap: Map<String, Map<String, List<String>>>, private val commandMap: Map<String, Map<String, List<String>>>) : KPuzzle(reader, kCommandMap) {
    constructor(reader: NotationReader, kCommandMap: Map<String, Map<String, List<String>>>, bldFile: File) : this(reader, kCommandMap, groupByCommand(bldFile.readLines()))
    constructor(reader: NotationReader, defFile: File, bldFile: File) : this(reader, groupByCommand(defFile.readLines()), groupByCommand(bldFile.readLines()))

    val letterSchemes = this.loadLetterSchemes()

    val mainBuffers = this.loadBuffers()

    val reorientMethod = this.loadReorientMethod()
    val reorientState = this.loadReorientState()

    val misOrientMethod = this.loadMisOrientMethod()

    val parityDependents = this.loadParityDependents()
    val parityDependencyFixes = this.loadParityDependencyFixes()

    val parityFirstPieceTypes = this.loadParityFirstPieceTypes()
    val executionPieceTypes = this.loadExecutionPieceTypes()

    var algSource: AlgSource? = null
    val optimizer by lazy { BreakInOptimizer(this.algSource!!, reader, *this.pieceTypes.toTypedArray(), fullCache = false) }

    val avoidBreakIns = this.pieceTypes.associateWith { true }
    val optimizeBreakIns = this.pieceTypes.associateWith { true }

    private val bruteForceRotations by lazy {
        val reOrientations = this.moveDefinitions.keys.filter { it.plane.isRotation }.toSet().powerset().filter { it.size in 1..2 }
        val nonCancelling = reOrientations.map { SimpleAlg(it.toList()) }.toSet()

        nonCancelling.flatMap { it.allMoves().permutations() }.map(::SimpleAlg).toSet()
    }

    // FILE LOADING

    fun loadLetterSchemes(): Map<PieceType, Array<String>> {
        val letterLines = this.commandMap.getValue("Lettering").getValue("Lettering")
        val parsed = loadFilePosition(this.pieceTypes, letterLines, "") { it.splitAtWhitespace().toTypedArray() }

        return parsed.mapValues { it.value.first }
    }

    fun loadBuffers(): Map<PieceType, Int> { // FIXME Stack<Int> instead to allow for floating?
        val bufferCommands = this.commandMap.getValue("Buffer")

        val collectionMap = mutableMapOf<PieceType, Int>()
        val pieceTypeNames = this.pieceTypes.map { it.name to it }.toMap() // Help for parsing

        for (bufferDef in bufferCommands.keys) {
            val defScheme = bufferDef.splitAtWhitespace().drop(1)
            val ptName = defScheme.first()

            if (ptName in pieceTypeNames) {
                val pieceType = pieceTypeNames.getValue(ptName)

                val bufferOrient = defScheme.last().toInt()
                val bufferPerm = defScheme.dropLast(1).last().toInt()

                // nasty perm - 1 hack
                collectionMap[pieceType] = pieceToTarget(pieceType, bufferPerm - 1, bufferOrient)
            }
        }

        return collectionMap
    }

    fun loadReorientMethod(): String {
        return this.commandMap.getValue("Orientation").keys.first().splitAtWhitespace().last()
    }

    fun loadReorientState(): PuzzleState {
        val stateLines = this.commandMap.getValue("Orientation").values.first()

        return loadKPosition(this.pieceTypes, stateLines)
    }

    fun loadMisOrientMethod(): String {
        return this.commandMap.getValue("MisOrient").keys.first().splitAtWhitespace().last()
    }

    fun loadParityDependents(): Map<PieceType, PieceType> {
        val dependentHeaders = this.commandMap["ParityDependency"]?.keys ?: emptySet()

        return dependentHeaders.map { it.splitAtWhitespace() }.map { this.findPieceTypeByName(it[1]) to this.findPieceTypeByName(it[2]) }.toMap()
    }

    fun loadParityDependencyFixes(): Map<PieceType, PuzzleState> {
        val dependencyFixDescriptions = this.commandMap["ParityDependency"] ?: emptyMap()
        val dependencyFixes = dependencyFixDescriptions.mapKeys { this.findPieceTypeByName(it.key.splitAtWhitespace()[1]) }

        return dependencyFixes.mapValues { it.value.toMutableList().apply { this.add(0, it.key.name) } }
                .mapValues { loadKPosition(this.pieceTypes, it.value) }
    }

    fun loadParityFirstPieceTypes(): List<PieceType> {
        return this.commandMap["ParityFirst"]?.let {
            it.keys.first().splitAtWhitespace().drop(1).map { st -> this.findPieceTypeByName(st) }
        } ?: emptyList()
    }

    fun loadExecutionPieceTypes(): List<PieceType> {
        return this.commandMap.getValue("Execution").keys.first().splitAtWhitespace().drop(1).map { this.findPieceTypeByName(it) }
    }

    private fun findPieceTypeByName(name: String): PieceType {
        return this.pieceTypes.find { it.name == name }!!
    }

    // LETTER SCHEME METHODS

    fun targetToLetter(type: PieceType, target: Int) = this.letterSchemes.getValue(type)[target]
    fun getLetterPairCorrespondants(type: PieceType, perm: Int) = permToTargets(type, perm).map { this.targetToLetter(type, it) }

    // K-STYLE METHODS

    protected fun currentlyAtTarget(type: PieceType, target: Int): Int {
        val (lookupPerm, lookupOrient) = targetToPiece(type, target)
        val (statePerm, stateOrient) = this.puzzleState.getValue(type)

        // nasty perm - 1 hack
        return pieceToTarget(type, statePerm[lookupPerm] - 1, (lookupOrient - stateOrient[lookupPerm]) pMod type.orientations)
    }

    protected fun getSolutionSpots(type: PieceType, target: Int): List<Int> {
        //val (currentPerm, currentOrient) = targetToPiece(type, this.currentlyAtTarget(type, target))
        val (currentPerm, currentOrient) = targetToPiece(type, target)
        val (refPerm, refOrient) = this.solvedState.getValue(type)

        // nasty perm - 1 hack
        val possiblePermSpots = refPerm.indices.filter { refPerm[it] - 1 == currentPerm }

        return possiblePermSpots.map { pieceToTarget(type, it, (refOrient[it] + currentOrient) % type.orientations) }
    }

    protected fun targetCurrentlySolved(type: PieceType, target: Int) = target in this.getSolutionSpots(type, this.currentlyAtTarget(type, target))
    protected fun targetCurrentlyPermuted(type: PieceType, target: Int) = target in this.getSolutionSpots(type, this.currentlyAtTarget(type, target))
            .map { targetToPerm(type, it) }
            .flatMap { permToTargets(type, it) }

    protected fun currentPermOrientation(type: PieceType, perm: Int) = this.puzzleState.getValue(type).second[perm]

    // BUFFER HELPERS

    fun getMainBufferTarget(type: PieceType) = this.mainBuffers.getValue(type)
    fun getMainBufferPerm(type: PieceType) = targetToPerm(type, this.getMainBufferTarget(type))

    fun getMainBufferOrientationTargets(type: PieceType) = permToTargets(type, this.getMainBufferPerm(type))

    // STATE MANIPULATION

    protected fun getHypotheticalState(scramble: Algorithm) = scramblePuzzle(this.puzzleState.clone(), scramble, this.moveDefinitions)

    // CYCLE BUILDERS

    protected fun compileTargetChain(type: PieceType): List<Int> {
        val accumulate = mutableListOf<Int>()

        return generateSequence {
            this.getNextTarget(type, accumulate)?.also { accumulate.add(it) }
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

        for (c in chunks) { // TODO allow for different cycle lengths? (M2/OP cycle length 1)
            if (c.size == 2) {
                cycles.add(ThreeCycle(mainBuffer, c[0], c[1]))
            } else {
                cycles.add(ParityCycle(mainBuffer, c[0]))
            }
        }

        return cycles
    }

    fun compileSolutionCycles(type: PieceType): List<PieceCycle> {
        val mainBuffer = this.getMainBufferTarget(type)

        val cycles = mutableListOf<PieceCycle>()
        cycles.addAll(this.compilePermuteSolutionCycles(type))

        for (i in 1 until type.orientations) {
            val misOrients = this.compileMisOrientedPieces(type, i)

            when (this.misOrientMethod) {
                "Single" -> {
                    for (piece in misOrients) {
                        val (perm, orient) = targetToPiece(type, piece)
                        val next = pieceToTarget(type, perm, (orient + i) % type.orientations)

                        cycles += SingleMisOrientCycle(mainBuffer, i, piece, next)
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
        resetState(this.solvedState, this.defSolvedState)
        this.applyScramble(scramble, true)

        val reorient = this.getReorientationMoves().also { this.applyScramble(it) }

        val paritySolvingOrder = this.parityDependents.mapValues { setOf(it.value) }.topologicalSort()
        val parityRelevantCycles = mutableMapOf<PieceType, List<PieceCycle>>()

        for (type in paritySolvingOrder) {
            val fixNecessary = this.parityDependents[type]?.let {
                parityRelevantCycles.getValue(it).any { c -> c is ParityCycle }
            } ?: false

            if (fixNecessary) {
                movePuzzle(this.solvedState, this.parityDependencyFixes.getValue(type)) // FIXME fix not correct
            }

            parityRelevantCycles[type] = this.compileSolutionCycles(type)
        }

        val cycles = this.executionPieceTypes.associateWith { parityRelevantCycles.getOrDefault(it, this.compileSolutionCycles(it)) }

        return BldAnalysis(this.reader, reorient, cycles, this.mainBuffers, this.letterSchemes, this.algSource)
    }

    fun getNextTarget(type: PieceType, previous: List<Int>): Int? {
        val avoidBreakIns = this.avoidBreakIns.getValue(type)

        val lastTarget = previous.lastOrNull() ?: this.getMainBufferTarget(type)

        val targetedPerms = previous.map { targetToPerm(type, it) }
        val lastTargetedPerm = targetToPerm(type, lastTarget)

        val currentlyInBuffer = this.currentlyAtTarget(type, lastTarget)

        val bufferSolved = currentlyInBuffer in this.getMainBufferOrientationTargets(type)
        val lastCycleCompleted = targetedPerms.count { it == lastTargetedPerm } > 1

        if (bufferSolved || lastCycleCompleted) {
            val possNext = this.getBreakInTargetsAfter(type, lastTarget, previous)

            // TODO mark buffer float?
            return possNext.find {
                val alternativeSolved = this.targetCurrentlySolved(type, it)
                val alternativeSuitable = targetToPerm(type, it) !in targetedPerms

                !alternativeSolved && alternativeSuitable
            }
        } else {
            val possNext = this.getSolutionSpots(type, currentlyInBuffer)

            // TODO use "current" buffer (according to floats) instead of "main" buffer?
            return possNext.find {
                val alternativeSolved = this.targetCurrentlySolved(type, it)
                val alternativeSuitable = this.getMainBufferTarget(type) !in this.getSolutionSpots(type, it)

                !alternativeSolved && (!avoidBreakIns || alternativeSuitable)
            } ?: possNext.find { !this.targetCurrentlySolved(type, it) }
        }
    }

    protected open fun getBreakInTargetsAfter(type: PieceType, target: Int, targeted: List<Int>): List<Int> {
        val preSolved = type.numTargets.countingList().filter { this.targetCurrentlyPermuted(type, it) }

        if (this.algSource == null || !this.optimizeBreakIns.getValue(type)) {
            return type.numTargets.countingList() - targeted - this.getMainBufferOrientationTargets(type) - preSolved
        }

        return this.optimizer.optimizeBreakInTargetsAfter(target, type) - preSolved
    }

    protected fun getReorientationMoves() = when {
        this.reorientMethod == "Fixed" -> this.bruteForceRotations.find { this.getHypotheticalState(it).deepEquals(this.reorientState, true) }
        this.reorientMethod == "Dynamic" -> this.bruteForceRotations.maxBy {
            val rotatedState = this.getHypotheticalState(it)

            val solvedCenters = rotatedState.countEquals(this.solvedState)
            val solvedBadCenters = rotatedState.countEquals(this.reorientState)

            2 * solvedCenters + 3 * solvedBadCenters // FIXME weighting?
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
        fun targetToPiece(type: PieceType, target: Int) = target / type.orientations to target % type.orientations

        fun targetToPerm(type: PieceType, target: Int) = targetToPiece(type, target).first
        fun permToTargets(type: PieceType, perm: Int) = type.orientations.countingList().map { pieceToTarget(type, perm, it) }
    }
}
