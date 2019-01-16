package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.bld.analyze.BldCube
import com.suushiemaniac.cubing.bld.model.cycle.MisOrientPiece
import com.suushiemaniac.cubing.bld.model.cycle.ParityCycle
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.model.cycle.ThreeCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType
import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import com.suushiemaniac.cubing.bld.optim.BreakInOptim
import com.suushiemaniac.cubing.bld.util.SpeffzUtil

import java.io.File

open class GPuzzle(defFile: File, bldFile: File) : KPuzzle(defFile) {
    val letterSchemes = (this.getPieceTypes() alwaysTo SpeffzUtil.FULL_SPEFFZ).toMutableMap()

    val mainBuffers = (this.getPieceTypes() allTo { this.getDefaultCubies().getValue(it)[0][0] }).toMutableMap()
    val backupBuffers = this.getPieceTypes() alwaysTo { mutableListOf<Int>() }

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

    fun getBufferPiece(type: PieceType): Array<Int> {
        if (type is LetterPairImage) {
            return arrayOf()
        }

        return this.cubies.getValue(type)[0].copyOf()
    }

    protected fun getCurrentBufferOrientation(type: PieceType): Int {
        val reference = this.cubies.getValue(type)
        val state = this.state.getValue(type)

        for (i in 1 until type.targetsPerPiece) {
            val bufferCurrentOrigin = (0 until type.targetsPerPiece).all {
                state[reference[0][it]] == reference[0][(it + i) % type.targetsPerPiece]
            }

            if (bufferCurrentOrigin) {
                return i
            }
        }

        return 0
    }

    fun getBufferTarget(type: PieceType): String {
        return if (type is LetterPairImage) this.letterPairLanguage else this.getLetteringScheme(type)[this.getBuffer(type)]
    }

    fun getLetterPairCorrespondant(type: PieceType, piece: Int): String {
        val lettering = this.getLetteringScheme(type)
        return this.getCorrespondents(type, piece).joinToString("") { lettering[it] }
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
                SINGLE__TARGET -> {
                    val cubies = this.cubies.getValue(type)

                    for (piece in misOrients) {
                        val outer = cubies.deepOuterIndex(piece)
                        val inner = cubies.deepInnerIndex(piece)

                        cycles.add(ThreeCycle(mainBuffer, piece, cubies[outer][(inner + i) % type.targetsPerPiece]))
                    }
                }
                SOLVE__DIRECT ->
                    cycles.addAll(misOrients.map { MisOrientPiece(it, i) })
            }
        }

        return cycles
    }

    fun getBufferPieceTargets(type: PieceType): Array<String> {
        if (type is LetterPairImage) {
            return arrayOf(this.letterPairLanguage)
        }

        return this.getBufferPiece(type)
                .map { this.letterSchemes.getValue(type)[it] }
                .toTypedArray()
    }

    protected fun cycleByBuffer(type: PieceType) {
        val state = this.puzzleState.getValue(type)
        val ref = this.cubies.getValue(type)

        val cycles = this.cycles.getValue(type)

        val avoidBreakIns = this.avoidBreakIns.getValue(type)

        val divBase = type.numPieces / this.getPiecePermutations(type)
        val modBase = this.getPieceOrientations(type)

        // If the buffer is preSolved, replace it with an unsolved corner
        if (solvedPieces[0]) {
            this.increaseCycleCount(type)

            val lastTarget = this.getLastTarget(type)
            val breakInTargets = this.getBreakInTargetsAfter(type, lastTarget)

            for (breakInTarget in breakInTargets) {
                val piece = this.getTargetPermutation(type, breakInTarget)
                val bestOrient = this.getTargetOrientation(type, breakInTarget)

                val suitable = state[breakInTarget] / divBase != ref[0][0] / divBase

                // First unsolved pieces is selected
                if (!solvedPieces[piece * divBase + bestOrient / type.targetsPerPiece] && suitable) {
                    for (targetFaces in 0 until type.targetsPerPiece) {
                        state.swap(
                                ref[0][targetFaces % modBase],
                                ref[piece][(bestOrient + targetFaces) % modBase]
                        )
                    }

                    // PieceConfig cycle is inserted into solution array
                    cycles.add(breakInTarget)

                    break
                }
            }
        } else { // If the buffer is not preSolved, swap it to the position where the pieces belongs
            for (permutation in 0 until this.getPiecePermutations(type)) {
                for (orientation in 0 until this.getPieceOrientations(type)) {
                    val pieceTargets = type.targetsPerPiece

                    val assumeMatch = (0 until pieceTargets).all {
                        val currentlyInBuffer = state[ref[0][it]] / divBase
                        val currentLoopTarget = ref[permutation][(it + orientation) % modBase] / divBase

                        currentlyInBuffer == currentLoopTarget
                    }

                    if (assumeMatch && !solvedPieces[permutation * divBase + orientation / pieceTargets]) {
                        val pieceIndex = (orientation until divBase).find {
                            val alternativeSolved = solvedPieces[permutation * divBase + it / pieceTargets]

                            val alternativePiece = state[ref[permutation][it % modBase]] / divBase
                            val alternativeSuitable = alternativePiece != ref[0][0] / divBase

                            !avoidBreakIns || (!alternativeSolved && alternativeSuitable)
                        } ?: orientation

                        for (targetFaces in 0 until pieceTargets) {
                            val currentTarget = (pieceIndex + targetFaces) % modBase

                            // Buffer pieces is replaced with pieces
                            state[ref[0][targetFaces]] = state[ref[permutation][currentTarget]]

                            // PieceConfig is solved
                            state[ref[permutation][currentTarget]] = ref[permutation][currentTarget]
                        }

                        // PieceConfig cycle is inserted into solution array
                        cycles.add(ref[permutation][pieceIndex])

                        return
                    }
                }
            }
        }
    }

    protected fun getReorientationMoves(): Algorithm {
        when {
            this.getOrientationPieceTypes().contains(CubicPieceType.CENTER) -> {
                val lastScrambledCenters = this.state.getValue(CubicPieceType.CENTER)

                val top = lastScrambledCenters[0]
                val front = lastScrambledCenters[2]

                return this.getRotationsFromOrientation(top, front)
            }
            this.reorientMethod == com.suushiemaniac.cubing.bld.gsolve.GPuzzle.ReorientMethod.FIXED_DLB_CORNER -> {
                val reorientation = arrayOf("x2 y", "z2", "x2 y'", "x2", "z'", "z' y", "z' y2", "z' y'", "x' y", "x' y2", "x' y'", "x'", "z y2", "z y'", "z", "z y", "x y'", "x", "x y", "x y2", "y", "y2", "y'", "")

                val xPosition = this.state.getValue(CubicPieceType.CORNER).indexOf(X)

                if (xPosition > -1) {
                    val neededRotation = reorientation[xPosition]
                    return CubicPieceType.CORNER.reader.parse(neededRotation)
                }

                return SimpleAlg()
            }
            this.reorientMethod == com.suushiemaniac.cubing.bld.gsolve.GPuzzle.ReorientMethod.DYNAMIC -> {
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
}
