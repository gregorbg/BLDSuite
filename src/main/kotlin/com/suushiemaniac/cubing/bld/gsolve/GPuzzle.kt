package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import java.io.File

open class GPuzzle(defFile: File, bldFile: File) : KPuzzle(defFile) {
    /*protected fun cycleByBuffer(type: PieceType) {
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

    }*/
}
