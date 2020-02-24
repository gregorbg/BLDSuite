package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.puzzledef.KCommands
import com.suushiemaniac.cubing.bld.util.*

open class KPuzzle(val reader: NotationReader, val kCommands: KCommands) {
    val pieceTypes get() = kCommands.pieceTypes

    val solvedState = this.loadSolvedState().toMutableMap()
    val puzzleState = this.solvedState.toMutableMap()

    val moveDefinitions get() = kCommands.moveDefinitions

    protected fun loadSolvedState() = kCommands.solvedState

    fun applyScramble(scramble: Algorithm) = scramblePuzzle(this.puzzleState, scramble, this.moveDefinitions).let { this }
    fun hypotheticalScramble(scramble: Algorithm) = scramblePuzzle(this.puzzleState.deepCopy(), scramble, this.moveDefinitions)

    fun dumpMoves() = this.moveDefinitions.mapValues { it.value.toDefLines() }
    fun dumpState(): List<String> = this.puzzleState.toDefLines()

    companion object {
        fun computeIdentityMove(state: PuzzleState): PuzzleState {
            return state.mapValues { Array(it.value.size) { i -> Piece(i + 1, 0) } }
        }

        fun scramblePuzzle(current: PuzzleState, scramble: Algorithm, moveDefinitions: Map<Move, PuzzleState>): PuzzleState {
            return current.apply {
                scramble.mapNotNull(moveDefinitions::get).forEach { movePuzzle(this, it) }
            }
        }

        fun movePuzzle(current: PuzzleState, moveDef: PuzzleState): PuzzleState {
            return current.apply {
                for ((pt, m) in moveDef) {
                    movePieces(this.getValue(pt), pt, m)
                }
            }
        }

        protected fun movePieces(current: PieceState, type: PieceType, moveDef: PieceState) {
            val permTargets = Array(type.permutations) { current[moveDef[it].permutation - 1].permutation }
            val orientTargets = Array(type.permutations) { current[moveDef[it].permutation - 1].orientation }

            for (i in 0 until type.permutations) {
                current[i] = Piece(permTargets[i], (orientTargets[i] + moveDef[i].orientation) % type.orientations)
            }
        }

        fun resetState(toReset: MutableMap<PieceType, PieceState>, master: PuzzleState) {
            for ((pt, solvedConf) in master) {
                toReset[pt] = solvedConf.clone()
            }
        }

        fun bruteForcePowerMoves(solved: PuzzleState, moveDef: PuzzleState, baseMove: Move): Map<Move, PuzzleState> {
            val movePowers = mutableListOf<PuzzleState>()
            val currentPower = moveDef.deepCopy()

            while (!movePuzzle(solved.deepCopy(), currentPower).deepEquals(solved)) {
                movePowers.add(currentPower.deepCopy())

                movePuzzle(currentPower, moveDef)
            }

            val moveDefs = mutableMapOf<Move, PuzzleState>()

            for ((i, pow) in movePowers.withIndex()) {
                val powerMoveList = List(i + 1) { baseMove }
                val modelMove = powerMoveList.reduce(Move::merge)

                moveDefs[modelMove] = pow
            }

            return moveDefs
        }

        fun preInstalledConfig(tag: String) =
                KCommands.parse(KCommands.loadFileStream(KPuzzle::class.java.getResourceAsStream("kpuzzle/$tag.def")))
    }
}