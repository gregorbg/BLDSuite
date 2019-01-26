package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.bld.analyze.BldAnalysis
import com.suushiemaniac.cubing.bld.model.cycle.MisOrientPiece
import com.suushiemaniac.cubing.bld.model.cycle.ParityCycle
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.model.cycle.ThreeCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.source.AlgSource
import com.suushiemaniac.cubing.bld.optim.BreakInOptimizer
import com.suushiemaniac.cubing.bld.util.CollectionUtil.powerset
import com.suushiemaniac.cubing.bld.util.CollectionUtil.permutations
import com.suushiemaniac.cubing.bld.util.CollectionUtil.countingList
import com.suushiemaniac.cubing.bld.util.StringUtil.splitAtWhitespace
import com.suushiemaniac.cubing.bld.util.MathUtil.pMod
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.clone
import com.suushiemaniac.cubing.bld.util.countEquals
import com.suushiemaniac.cubing.bld.util.deepEquals

import java.io.File

open class GPuzzle(kCommandMap: Map<String, Map<String, List<String>>>, private val commandMap: Map<String, Map<String, List<String>>>) : KPuzzle(kCommandMap) {
    constructor(kCommandMap: Map<String, Map<String, List<String>>>, bldFile: File) : this(kCommandMap, groupByCommand(bldFile.readLines()))
    constructor(defFile: File, bldFile: File) : this(groupByCommand(defFile.readLines()), groupByCommand(bldFile.readLines()))

    val letterSchemes = this.loadLetterSchemes()

    val mainBuffers = this.loadBuffers()

    val reorientMethod = this.loadReorientMethod()
    val reorientState = this.loadReorientState()

    val misOrientMethod = this.loadMisOrientMethod()

    var algSource: AlgSource? = null
    val optimizer by lazy { BreakInOptimizer(this.algSource!!, *this.pieceTypes.toTypedArray(), fullCache = false) }

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

                collectionMap[pieceType] = this.pieceToTarget(pieceType, bufferPerm - 1, bufferOrient) // FIXME nasty perm - 1 hack
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

    // CONVERSION METHODS

    fun pieceToTarget(type: PieceType, perm: Int, orient: Int) = (perm * type.orientations) + orient
    fun targetToPiece(type: PieceType, target: Int) = target / type.orientations to target % type.orientations

    fun targetToPerm(type: PieceType, target: Int) = this.targetToPiece(type, target).first
    fun permToTargets(type: PieceType, perm: Int) = type.orientations.countingList().map { this.pieceToTarget(type, perm, it) }

    fun targetToLetter(type: PieceType, target: Int) = this.letterSchemes.getValue(type)[target]

    fun getLetterPairCorrespondants(type: PieceType, perm: Int) = this.permToTargets(type, perm).map { this.targetToLetter(type, it) }

    // K-STYLE METHODS

    protected fun currentlyAtTarget(type: PieceType, target: Int): Int { // FIXME is this working?
        val (lookupPerm, lookupOrient) = this.targetToPiece(type, target)
        val (statePerm, stateOrient) = this.puzzleState.getValue(type)

        // FIXME nasty perm - 1 hack
        return this.pieceToTarget(type, statePerm[lookupPerm] - 1, (lookupOrient - stateOrient[lookupPerm]) pMod type.orientations)
    }

    protected fun getSolutionSpots(type: PieceType, target: Int): List<Int> { // FIXME is this working?
        //val (currentPerm, currentOrient) = this.targetToPiece(type, this.currentlyAtTarget(type, target))
        val (currentPerm, currentOrient) = this.targetToPiece(type, target)
        val (refPerm, refOrient) = this.solvedState.getValue(type)

        // FIXME nasty perm - 1 hack
        val possiblePermSpots = refPerm.indices.filter { refPerm[it] - 1 == currentPerm }

        return possiblePermSpots.map { this.pieceToTarget(type, it, (refOrient[it] + currentOrient) % type.orientations) }
    }

    protected fun targetCurrentlySolved(type: PieceType, target: Int) = target in this.getSolutionSpots(type, this.currentlyAtTarget(type, target))

    // BUFFER HELPERS

    fun getMainBufferTarget(type: PieceType) = this.mainBuffers.getValue(type)
    fun getMainBufferPerm(type: PieceType) = this.targetToPerm(type, this.getMainBufferTarget(type))

    fun getMainBufferOrientationTargets(type: PieceType) = this.permToTargets(type, this.getMainBufferPerm(type))

    // STATE MANIPULATION

    protected fun getHypotheticalState(scramble: Algorithm) = scramblePuzzle(this.puzzleState.clone(), scramble, this.moveDefinitions)

    // CYCLE BUILDERS

    protected fun compileTargetChain(type: PieceType): List<Int> { // TODO beautify
        val compiled = mutableListOf<Int>()

        do {
            val next = this.getNextTarget(type, *compiled.toIntArray())

            if (next != null) {
                compiled.add(next)
            }
        } while (next != null)

        return compiled
    }

    protected fun compileMisOrientedPieces(type: PieceType, orientation: Int): List<Int> {
        return emptyList() // FIXME
    }

    protected open fun compilePermuteSolutionCycles(type: PieceType): List<PieceCycle> {
        val targets = this.compileTargetChain(type) // TODO allow for buffer floating?
        val mainBuffer = this.getMainBufferTarget(type)

        val cycles = mutableListOf<PieceCycle>()

        for (c in targets.chunked(2)) { // TODO allow for different cycle lengths?
            if (c.size == 2) {
                cycles.add(ThreeCycle(mainBuffer, c[0], c[1]))
            } else {
                cycles.add(ParityCycle(mainBuffer, c[0]))
            }
        }

        return cycles
    }

    fun compileSolutionCycles(type: PieceType): List<PieceCycle> { // TODO!!
        val mainBuffer = this.getMainBufferTarget(type)

        val cycles = mutableListOf<PieceCycle>()
        cycles.addAll(this.compilePermuteSolutionCycles(type))

        for (i in 1 until type.orientations) {
            val misOrients = this.compileMisOrientedPieces(type, i)

            when (this.misOrientMethod) {
                "Single" -> {
                    for (piece in misOrients) {
                        val (perm, orient) = this.targetToPiece(type, piece)
                        val next = this.pieceToTarget(type, perm, (orient + i) % type.orientations)

                        cycles.add(ThreeCycle(mainBuffer, piece, next))
                    }
                }
                "Compound" ->
                    cycles.addAll(misOrients.map { MisOrientPiece(it, i) })
            }
        }

        return cycles
    }

    private fun dumpDebug(): Map<PieceType, List<String>> {
        return this.letterSchemes.mapValues { (pt, _) ->
            pt.permutations.countingList().map {
                this.targetToLetter(pt, this.currentlyAtTarget(pt, this.pieceToTarget(pt, it, 0)))
            }
        }
    }

    fun getAnalysis(scramble: Algorithm): BldAnalysis {
        this.applyScramble(scramble, true)

        // TODO resolve parities (according to dependency definitions)

        val reorient = this.getReorientationMoves().apply {
            applyScramble(this)
        }

        val cycles = this.mainBuffers.mapValues { this.compileSolutionCycles(it.key) } // FIXME Nasty-ish hack to avoid compilation of 3x3 CENTER cycles

        return BldAnalysis(this, scramble, reorient, cycles, this.letterSchemes, this.algSource)
    }

    fun getNextTarget(type: PieceType, vararg previous: Int): Int? {
        val avoidBreakIns = this.avoidBreakIns.getValue(type)

        val lastTarget = previous.lastOrNull() ?: this.getMainBufferTarget(type)

        val targetedPerms = previous.map { this.targetToPerm(type, it) }
        val lastTargetedPerm = this.targetToPerm(type, lastTarget)

        val currentlyInBuffer = this.currentlyAtTarget(type, lastTarget)

        val bufferSolved = currentlyInBuffer in this.getMainBufferOrientationTargets(type)
        val lastCycleCompleted = targetedPerms.count { it == lastTargetedPerm } > 1

        if (bufferSolved || lastCycleCompleted) {
            val possNext = this.getBreakInTargetsAfter(type, lastTarget, *previous)

            // TODO mark buffer float?
            return possNext.find {
                // FIXME don't shoot to twisted pieces (not in targetSlots but still permuted correctly)
                val alternativeSolved = this.targetCurrentlySolved(type, it)
                val alternativeSuitable = this.targetToPerm(type, it) !in targetedPerms

                !alternativeSolved && alternativeSuitable
            }
        } else {
            val possNext = this.getSolutionSpots(type, currentlyInBuffer)

            return possNext.find {
                // TODO use "current" buffer (according to floats) instead of "main" buffer?
                val alternativeSolved = this.targetCurrentlySolved(type, it)
                val alternativeSuitable = this.getMainBufferTarget(type) !in this.getSolutionSpots(type, it)

                !alternativeSolved && (!avoidBreakIns || alternativeSuitable)
            }
        }
    }

    protected open fun getBreakInTargetsAfter(type: PieceType, target: Int, vararg targeted: Int): List<Int> {
        // TODO only use/return unsolved by default
        if (this.algSource == null || !this.optimizeBreakIns.getValue(type)) {
            return type.numTargets.countingList() - targeted.toList() - this.getMainBufferOrientationTargets(type)
        }

        return this.optimizer.optimizeBreakInTargetsAfter(target, type)
    }

    protected fun getReorientationMoves() = when {
        this.reorientMethod == "Fixed" -> this.bruteForceRotations.find { this.getHypotheticalState(it).deepEquals(this.reorientState, true) }
        this.reorientMethod == "Dynamic" -> this.bruteForceRotations.maxBy {
            val rotatedState = this.getHypotheticalState(it)

            val solvedCenters = rotatedState.countEquals(this.solvedState)
            val solvedBadCenters = rotatedState.countEquals(this.reorientState)

            2 * solvedCenters + 3 * solvedBadCenters // FIXME is this an appropriate / intuitive weighting?
        }
        else -> SimpleAlg()
    } ?: SimpleAlg()

    fun solves(type: PieceType, alg: Algorithm, case: PieceCycle, pure: Boolean = true): Boolean {
        return false // FIXME
    }
}
