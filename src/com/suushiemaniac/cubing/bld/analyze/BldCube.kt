package com.suushiemaniac.cubing.bld.analyze

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import com.suushiemaniac.cubing.bld.optim.BreakInOptim
import com.suushiemaniac.cubing.bld.util.ArrayUtil.countOf
import com.suushiemaniac.cubing.bld.util.ArrayUtil.countingArray
import com.suushiemaniac.cubing.bld.util.ArrayUtil.cycleLeft
import com.suushiemaniac.cubing.bld.util.ArrayUtil.deepInnerIndex
import com.suushiemaniac.cubing.bld.util.ArrayUtil.deepOuterIndex
import com.suushiemaniac.cubing.bld.util.ArrayUtil.swap

open class BldCube : BldPuzzle {
    var cornerParityMethod = CornerParityMethod.SWAP_UB_UL
        set(value) {
            field = value
            this.reSolve()
        }

    var reorientMethod = ReorientMethod.DYNAMIC
        set(value) {
            field = value
            this.reSolve()
        }

    val cornerParityDependents: List<PieceType>
        get() {
            val allDeps = mutableListOf(EDGE, WING, INNERWING)
            allDeps.retainAll(this.model.pieceTypes.asList())

            return allDeps
        }

    var executionOrder: List<PieceType> = listOf(TCENTER, XCENTER, WING, EDGE, CORNER)

    var top: Int = 0
    var front: Int = 0

    var optim: BreakInOptim? = null

    val dynamicReorientPieceTypes: List<PieceType>
        get() {
            val dynamicTypes = mutableListOf(XCENTER, TCENTER, INNERXCENTER, INNERTCENTER, LEFTOBLIQUE, RIGHTOBLIQUE)
            dynamicTypes.retainAll(this.model.pieceTypes.asList())

            return dynamicTypes
        }

    constructor(model: CubicPuzzle) : super(model)
    constructor(model: CubicPuzzle, scramble: Algorithm) : super(model, scramble)

    override fun getOrientationPieceTypes(): List<PieceType> {
        return if (this.model.pieceTypes.contains(CENTER)) listOf(CENTER) else listOf()
    }

    override fun getPermutationPieceTypes(): List<PieceType> {
        return this.model.pieceTypes.toList() - this.getOrientationPieceTypes()
    }

    override fun getExecutionOrderPieceTypes(): List<PieceType> {
        val definitiveOrder = this.executionOrder.toMutableList()
        val permutations = this.getPermutationPieceTypes().toMutableList()

        definitiveOrder.retainAll(permutations)

        permutations -= definitiveOrder
        definitiveOrder += permutations

        return definitiveOrder
    }

    override fun getDefaultCubies(): Map<PieceType, Array<Array<Int>>> {
        val cubies = mapOf<PieceType, Array<Array<Int>>>(
                CENTER to SPEFFZ_CENTERS,
                CORNER to SPEFFZ_CORNERS,
                EDGE to SPEFFZ_EDGES,
                WING to SPEFFZ_WINGS,
                XCENTER to SPEFFZ_XCENTERS,
                TCENTER to SPEFFZ_TCENTERS,
                INNERWING to SPEFFZ_WINGS,
                RIGHTOBLIQUE to SPEFFZ_OBLIQUES,
                LEFTOBLIQUE to SPEFFZ_OBLIQUES,
                INNERXCENTER to SPEFFZ_XCENTERS,
                INNERTCENTER to SPEFFZ_TCENTERS
        )

        return this.getPieceTypes(true)
                .map { it to cubies.getValue(it) }
                .toMap()
    }

    fun getRotationsFromOrientation(orientationModelTop: Int, orientationModelFront: Int): Algorithm {
        val neededRotations = REORIENTATIONS[orientationModelTop][orientationModelFront]
        return CENTER.reader.parse(neededRotations)
    }

    override fun getReorientationMoves(): Algorithm {
        when {
            this.getOrientationPieceTypes().contains(CENTER) -> {
                val lastScrambledCenters = this.lastScrambledState.getValue(CENTER)

                val top = lastScrambledCenters[0]
                val front = lastScrambledCenters[2]

                return this.getRotationsFromOrientation(top, front)
            }
            this.reorientMethod == ReorientMethod.FIXED_DLB_CORNER -> {
                val reorientation = arrayOf("x2 y", "z2", "x2 y'", "x2", "z'", "z' y", "z' y2", "z' y'", "x' y", "x' y2", "x' y'", "x'", "z y2", "z y'", "z", "z y", "x y'", "x", "x y", "x y2", "y", "y2", "y'", "")

                val xPosition = this.state.getValue(CORNER).indexOf(X)

                if (xPosition > -1) {
                    val neededRotation = reorientation[xPosition]
                    return CORNER.reader.parse(neededRotation)
                }

                return SimpleAlg()
            }
            this.reorientMethod == ReorientMethod.DYNAMIC -> {
                val possRotations = arrayOf("", "y'", "y", "y2", "z y", "z", "z y2", "z y'", "x y2", "x y'", "x y", "x", "z' y'", "z'", "z' y2", "z' y", "x'", "x' y'", "x' y", "x' y2", "x2 y'", "z2", "x2 y", "x2")

                var max = Double.MIN_VALUE
                var maxIndex = 0

                val orientationTypes = this.dynamicReorientPieceTypes

                for (i in possRotations.indices) {
                    var solvedCenters = 0.0
                    var solvedBadCenters = 0.0
                    var totalCheckedCenters = 0.0

                    for (type in orientationTypes) {
                        val state = this.state.getValue(type)
                        val copyState = state.copyOf()

                        totalCheckedCenters += state.size.toDouble()

                        if (i > 0) {
                            val rotation = type.reader.parse(possRotations[i])

                            for (permutation in rotation) {
                                val perm = this.permutations.getValue(permutation).getValue(type)
                                this.applyPermutations(copyState, perm)
                            }
                        }

                        for (j in copyState.indices) {
                            val norm = this.getPieceOrientations(type)

                            if (copyState[j] / norm == j / norm) {
                                solvedCenters++
                                if (j >= 2 * (copyState.size / 3)) solvedBadCenters++
                            }
                        }
                    }

                    solvedCenters /= totalCheckedCenters
                    solvedBadCenters /= totalCheckedCenters / 3.0

                    val solvedCoeff = (2 * solvedCenters + solvedBadCenters) / 3.0

                    if (solvedCoeff > max) {
                        max = solvedCoeff
                        maxIndex = i
                    }
                }

                if (maxIndex > 0) {
                    val rotation = possRotations[maxIndex]
                    return this.model.reader.parse(rotation)
                }

                return SimpleAlg()
            }
            else -> return SimpleAlg()
        }

    }

    override fun getSolvingOrientationPremoves(): Algorithm {
        return this.getRotationsFromOrientation(this.top, this.front)
    }

    fun setSolvingOrientation(top: Int, front: Int): Boolean {
        if (this.getAdjacentCenters(top).contains(front)) {
            this.top = top
            this.front = front

            return true
        }

        return false
    }

    protected fun swapParityTargets(type: PieceType) {
        val state = this.state.getValue(type)

        val swaps = mapOf(
                A to D,
                Q to E
        )

        for ((key, value) in swaps) {
            state.swap(state.indexOf(key), state.indexOf(value))
        }
    }

    override fun getOrientationSideCount(): Int {
        return 6
    }

    protected fun getAdjacentCenters(center: Int): Set<Int> {
        val adjacenceMatrix = arrayOf(
                arrayOf(1, 2, 3, 4),
                arrayOf(0, 2, 4, 5),
                arrayOf(0, 1, 3, 5)
        )

        val index = Math.min(center, this.getOppositeCenter(center))
        return setOf(*adjacenceMatrix[index])
    }

    protected fun getOppositeCenter(center: Int): Int {
        val opposites = arrayOf(5, 3, 4, 1, 2, 0)
        return opposites[center]
    }

    override fun getBreakInPermutationsAfter(piece: Int, type: PieceType): List<Int> {
        if (this.algSource == null)
            return super.getBreakInPermutationsAfter(piece, type)

        if (this.optim == null)
            this.optim = BreakInOptim(this.algSource!!, this, false)

        val bestTargets = this.optim!!.optimizeBreakInTargetsAfter(piece, type)
        val breakInPerms = bestTargets
                .map { this.cubies.getValue(type).deepOuterIndex(it) }
                .filter { it > -1 }
                .distinct().toMutableList()

        val expected = super.getBreakInPermutationsAfter(piece, type).toMutableList()
        expected.removeAll(breakInPerms)

        breakInPerms.addAll(expected)

        return breakInPerms
    }

    override fun getBreakInOrientationsAfter(piece: Int, type: PieceType): Int {
        if (this.algSource == null)
            return super.getBreakInOrientationsAfter(piece, type)

        if (this.optim == null)
            this.optim = BreakInOptim(this.algSource!!, this, false)

        val bestTargets = this.optim!!.optimizeBreakInTargetsAfter(piece, type)
        val breakInOrients = bestTargets
                .map { this.cubies.getValue(type).deepInnerIndex(it) }
                .filter { it > -1 }
                .toMutableList()

        val expected = type.targetsPerPiece.countingArray().toMutableList()
        expected.removeAll(breakInOrients)

        breakInOrients.addAll(expected)

        return breakInOrients[0]
    }

    override fun solvePieces(type: PieceType) {
        if (this.cornerParityDependents.contains(type)) {
            if (!this.isSolved(CORNER)) {
                this.solvePieces(CORNER)
            }

            if (this.hasParity(CORNER) && this.cornerParityMethod == CornerParityMethod.SWAP_UB_UL) {
                this.swapParityTargets(type)
            }
        }

        while (!this.isSolved(type)) {
            this.cycleByBuffer(type)
        }

        if (this.cycles.getValue(type).size % 2 == 1) {
            this.parities[type] = true
        }
    }

    override fun hasParity(type: PieceType): Boolean {
        return if (type === EDGE) this.hasParity(CORNER) else super.hasParity(type)

    }

    protected fun isSolved(type: PieceType): Boolean {
        var isSolved = true

        val state = this.state.getValue(type)
        val lastScrambledState = this.lastScrambledState.getValue(type)
        val ref = this.cubies.getValue(type)

        val solvedPieces = this.solvedPieces.getValue(type)
        val preSolvedPieces = this.preSolvedPieces.getValue(type)
        val misOrientations = this.misOrientedPieces.getValue(type)

        val currentCycleLength = this.cycles.getValue(type).size

        val divBase = type.numPieces / this.getPiecePermutations(type)
        val modBase = this.getPieceOrientations(type)

        // Check if pieces marked as unsolved haven't been preSolved yet
        for (i in 0 until type.numPieces) {
            if (i == 0 || !solvedPieces[i]) {
                val baseIndex = i / divBase

                var assumeSolved = false

                for (j in 0 until divBase) {
                    var currentSolved = true

                    for (k in 0 until type.targetsPerPiece) {
                        currentSolved = currentSolved and (state[ref[baseIndex][(i + k) % modBase]] == ref[baseIndex][(i + k + j) % modBase])
                    }

                    assumeSolved = assumeSolved or currentSolved
                }

                // Piece is preSolved and oriented
                if (assumeSolved) {
                    solvedPieces[i] = true

                    var assumePreSolved = true

                    for (k in 0 until type.targetsPerPiece) {
                        assumePreSolved = assumePreSolved and (lastScrambledState[ref[baseIndex][(i + k) % modBase]] == ref[baseIndex][(i + k) % modBase])
                    }

                    preSolvedPieces[i] = assumePreSolved
                } else {
                    // Piece is in correct position but needs to be rotated
                    var isRotated = false

                    //Rotations
                    val rotations = type.targetsPerPiece

                    var j = 1
                    while (j < rotations && !isRotated) {
                        var assumeRotated = true

                        for (k in 0 until rotations) {
                            assumeRotated = assumeRotated and (state[ref[i][k]] == ref[i][(k + j) % rotations])
                        }

                        if (assumeRotated) {
                            isRotated = true
                            solvedPieces[i] = true
                            misOrientations[j][i] = true
                        }
                        j++
                    }

                    if (!isRotated) {
                        solvedPieces[i] = false
                        isSolved = false
                    }
                }
            }
        }

        if (!isSolved && solvedPieces[0] && currentCycleLength % 2 == 0) {
            val unsolvedCount = solvedPieces.countOf(false)

            if (unsolvedCount > 2) {
                val bufferFloats = this.bufferFloats.getValue(type)
                val floatingBuffers = this.backupBuffers.getValue(type).toMutableList()

                val nextBufferFloat = floatingBuffers.find { !solvedPieces[ref.deepOuterIndex(it)] && !bufferFloats.containsValue(it) }

                if (nextBufferFloat != null && nextBufferFloat >= 0) {
                    this.cycleCubiesForBuffer(type, nextBufferFloat)

                    for (i in 0 until ref.deepOuterIndex(nextBufferFloat)) {
                        solvedPieces.cycleLeft()

                        for (j in 0 until type.targetsPerPiece) {
                            misOrientations[j].cycleLeft()
                        }
                    }

                    bufferFloats[currentCycleLength] = nextBufferFloat
                }
            }
        }

        return isSolved
    }

    protected fun cycleByBuffer(type: PieceType) {
        var pieceCycled = false

        val state = this.state.getValue(type)
        val ref = this.cubies.getValue(type)

        val solvedPieces = this.solvedPieces.getValue(type)

        val cycles = this.cycles.getValue(type)

        val avoidBreakIns = this.avoidBreakIns[type]
        val optimizeBreakIns = this.optimizeBreakIns[type]

        val currentCycleLength = cycles.size

        val divBase = type.numPieces / this.getPiecePermutations(type)
        val modBase = this.getPieceOrientations(type)

        // If the buffer is preSolved, replace it with an unsolved corner
        if (solvedPieces[0]) {
            this.increaseCycleCount(type)

            val lastTarget = this.getLastTarget(type)

            val breakInPerms = if (optimizeBreakIns!! && lastTarget > 0 && currentCycleLength % 2 == 1)
                this.getBreakInPermutationsAfter(lastTarget, type)
            else
                super.getBreakInPermutationsAfter(lastTarget, type)

            var pieceIndex = 0
            while (pieceIndex < type.numPiecesNoBuffer && !pieceCycled) {
                val piece = breakInPerms[pieceIndex]

                // First unsolved pieces is selected
                if (!solvedPieces[piece]) {
                    val baseIndex = piece / divBase
                    val parts = type.targetsPerPiece

                    var bestOrient = if (optimizeBreakIns && lastTarget > 0 && currentCycleLength % 2 == 1)
                        this.getBreakInOrientationsAfter(lastTarget, type)
                    else
                        super.getBreakInOrientationsAfter(lastTarget, type)

                    bestOrient += modBase - piece
                    bestOrient %= parts

                    val tempPiece = IntArray(parts)

                    for (targetFaces in 0 until parts) {
                        val extIndex = (piece + bestOrient + targetFaces) % modBase

                        // Buffer is placed in a temp pieces
                        tempPiece[targetFaces] = state[ref[0][targetFaces % modBase]]

                        // Buffer is replaced with pieces
                        state[ref[0][targetFaces % modBase]] = state[ref[baseIndex][extIndex]]

                        // Piece is replaced with temp pieces
                        state[ref[baseIndex][extIndex]] = tempPiece[targetFaces]
                    }

                    // Piece cycle is inserted into solution array
                    cycles.add(ref[baseIndex][(piece + bestOrient) % modBase])

                    // set flag to break out of loop
                    pieceCycled = true
                }
                pieceIndex++
            }
        } else {
            var permutation = 0
            while (permutation < this.getPiecePermutations(type) && !pieceCycled) {
                var orientation = 0
                while (orientation < this.getPieceOrientations(type) && !pieceCycled) {
                    val pieceTargets = type.targetsPerPiece

                    var assumeMatch = true

                    for (targetFaces in 0 until pieceTargets) {
                        val currentlyInBuffer = state[ref[0][targetFaces]] / divBase
                        val currentLoopTarget = ref[permutation][(targetFaces + orientation) % modBase] / divBase

                        assumeMatch = assumeMatch and (currentlyInBuffer == currentLoopTarget)
                    }

                    if (assumeMatch && !solvedPieces[permutation * divBase + orientation / pieceTargets]) {
                        var pieceIndex = orientation

                        if (avoidBreakIns!!) {
                            val aimedTarget = state[ref[permutation][pieceIndex % modBase]] / divBase
                            val normalizedBuffer = ref[0][0] / divBase

                            if (aimedTarget == normalizedBuffer) {
                                for (alternative in orientation + 1 until divBase) {
                                    val alternativeSolved = solvedPieces[permutation * divBase + alternative / pieceTargets]

                                    val alternativePiece = state[ref[permutation][alternative % modBase]] / divBase
                                    val alternativeSuitable = alternativePiece != normalizedBuffer

                                    if (!alternativeSolved && alternativeSuitable) {
                                        pieceIndex = alternative
                                        break
                                    }
                                }
                            }
                        }

                        for (targetFaces in 0 until pieceTargets) {
                            val currentTarget = (pieceIndex + targetFaces) % modBase

                            // Buffer pieces is replaced with pieces
                            state[ref[0][targetFaces]] = state[ref[permutation][currentTarget]]

                            // Piece is solved
                            state[ref[permutation][currentTarget]] = ref[permutation][currentTarget]
                        }

                        // Piece cycle is inserted into solution array
                        cycles.add(ref[permutation][pieceIndex])

                        // set flag to break out of loop
                        pieceCycled = true
                    }
                    orientation++
                }
                permutation++
            }
        }// If the buffer is not preSolved, swap it to the position where the pieces belongs
    }

    protected fun getPieceOrientations(type: PieceType): Int {
        return if (type.numPieces != this.cubies.getValue(type).size) this.cubies.getValue(type)[0].size else type.targetsPerPiece
    }

    protected fun getPiecePermutations(type: PieceType): Int {
        return if (type.numPieces != this.cubies.getValue(type).size) this.getOrientationSideCount() else type.numPieces
    }

    enum class CornerParityMethod {
        SWAP_UB_UL, APPLY_ALGORITHM
    }

    enum class ReorientMethod {
        DYNAMIC, FIXED_DLB_CORNER
    }

    companion object {
        const val A = 0
        const val B = 1
        const val C = 2
        const val D = 3
        const val E = 4
        const val F = 5
        const val G = 6
        const val H = 7
        const val I = 8
        const val J = 9
        const val K = 10
        const val L = 11
        const val M = 12
        const val N = 13
        const val O = 14
        const val P = 15
        const val Q = 16
        const val R = 17
        const val S = 18
        const val T = 19
        const val U = 20
        const val V = 21
        const val W = 22
        const val X = 23

        const val UP = 0
        const val LEFT = 1
        const val FRONT = 2
        const val RIGHT = 3
        const val BACK = 4
        const val DOWN = 5

        val SPEFFZ_CORNERS = arrayOf(arrayOf(A, E, R), arrayOf(B, Q, N), arrayOf(C, M, J), arrayOf(D, I, F), arrayOf(L, U, G), arrayOf(P, V, K), arrayOf(T, W, O), arrayOf(H, X, S))
        val SPEFFZ_CENTERS = arrayOf(arrayOf(UP), arrayOf(LEFT), arrayOf(FRONT), arrayOf(RIGHT), arrayOf(BACK), arrayOf(DOWN))
        val SPEFFZ_EDGES = arrayOf(arrayOf(U, K), arrayOf(A, Q), arrayOf(B, M), arrayOf(C, I), arrayOf(D, E), arrayOf(L, F), arrayOf(X, G), arrayOf(R, H), arrayOf(J, P), arrayOf(T, N), arrayOf(V, O), arrayOf(W, S))
        val SPEFFZ_WINGS = arrayOf(arrayOf(U), arrayOf(A), arrayOf(B), arrayOf(C), arrayOf(D), arrayOf(E), arrayOf(F), arrayOf(G), arrayOf(H), arrayOf(I), arrayOf(J), arrayOf(K), arrayOf(L), arrayOf(M), arrayOf(N), arrayOf(O), arrayOf(P), arrayOf(Q), arrayOf(R), arrayOf(S), arrayOf(T), arrayOf(V), arrayOf(W), arrayOf(X))
        val SPEFFZ_XCENTERS = arrayOf(arrayOf(A, B, C, D), arrayOf(E, F, G, H), arrayOf(I, J, K, L), arrayOf(M, N, O, P), arrayOf(Q, R, S, T), arrayOf(U, V, W, X))
        val SPEFFZ_TCENTERS = arrayOf(arrayOf(U, V, W, X), arrayOf(A, B, C, D), arrayOf(E, F, G, H), arrayOf(I, J, K, L), arrayOf(M, N, O, P), arrayOf(Q, R, S, T))
        val SPEFFZ_OBLIQUES = arrayOf(arrayOf(U, V, W, X), arrayOf(A, B, C, D), arrayOf(E, F, G, H), arrayOf(I, J, K, L), arrayOf(M, N, O, P), arrayOf(Q, R, S, T))

        val REORIENTATIONS = arrayOf(arrayOf("", "y", "", "y'", "y2", ""), arrayOf("y' z'", "", "z'", "", "y2 z'", "y z'"), arrayOf("x y2", "y x'", "", "y' x'", "", "x'"), arrayOf("y z", "", "z", "", "y2 z", "y' z"), arrayOf("x", "y x", "", "y' x", "", "y2 x"), arrayOf("", "y x2", "z2", "y' x2", "x2", ""))
    }
}