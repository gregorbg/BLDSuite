package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.util.ArrayUtil.filledArray
import com.suushiemaniac.cubing.bld.util.PieceState
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.clone
import com.suushiemaniac.cubing.bld.util.deepEquals

import java.io.File

open class KPuzzle(defFile: File) {
    private val commandMap = groupByCommand(defFile.readLines())

    val model = this.loadNamedModel()

    val pieceTypes = this.loadPieceTypes()

    val solvedState get() = this.loadSolvedState()
    val puzzleState = this.solvedState.toMutableMap()

    val moveDefinitions = this.loadMoves()

    private fun loadNamedModel(): TwistyPuzzle {
        val nameCommand = this.commandMap.getValue("Name").keys.first() // Should only be one anyway
        val nameStr = nameCommand.split("\\s+".toRegex()).last()

        return CubicPuzzle.fromSize(nameStr[0].toInt(), "BLD") // FIXME Shortcut, should use proper puzzle registry instead
    }

    private fun loadPieceTypes(): Set<PieceType> {
        val pieceTypes = mutableSetOf<PieceType>()
        val pieceTypeCommands = this.commandMap.getValue("Set").keys

        for (ptCmd in pieceTypeCommands) {
            val (_, title, perm, orient) = ptCmd.split("\\s+".toRegex())

            val ptModel = CubicPieceType.valueOf(title.toUpperCase()) // FIXME shortcut, better link to TwistyPuzzle model supported PieceTypes

            if (ptModel.numPieces == perm.toInt() && ptModel.targetsPerPiece == orient.toInt()) {
                pieceTypes.add(ptModel)
            } // TODO else throw error?
        }

        return pieceTypes
    }

    private fun loadSolvedState(): PuzzleState {
        val solvedCommand = this.commandMap.getValue("Solved").getValue("Solved")

        return loadKPosition(this.pieceTypes, solvedCommand)
    }

    private fun loadMoves(): Map<Move, PuzzleState> {
        val moveDefs = mutableMapOf<Move, PuzzleState>()
        val moveCommands = this.commandMap.getOrDefault("Move", emptyMap())

        for ((lnKey, moveDef) in moveCommands) {
            val moveConfig = loadKPosition(this.pieceTypes, moveDef)

            val moveBaseName = lnKey.split("\\s+".toRegex()).last()
            val movePowers = bruteForcePowerMoves(this.solvedState, moveConfig)

            for ((i, pow) in movePowers.withIndex()) {
                val powerMoveStr = if (i <= movePowers.size / 2) {
                    if (i > 0) "$moveBaseName${i + 1}" else moveBaseName
                } else {
                    if (i < movePowers.size - 1) "$moveBaseName${movePowers.size - i}'" else "$moveBaseName'"
                } // TODO more concise notation?

                val modelMove = this.model.reader.parse(powerMoveStr).firstMove()
                moveDefs[modelMove] = pow
            }
        }

        return moveDefs
    }

    protected fun applyScramble(scramble: Algorithm, reset: Boolean = false) {
        if (reset) {
            for ((pt, solvedConf) in this.solvedState) {
                this.puzzleState[pt] = solvedConf.clone()
            }
        }

        scramble.mapNotNull { this.moveDefinitions[it] }
                .forEach { movePuzzle(this.puzzleState, it) }
    }

    companion object {
        fun groupByCommand(lines: List<String>): Map<String, Map<String, List<String>>> { // TODO beautify return format
            val cmdGroups = mutableMapOf<String, MutableMap<String, List<String>>>()

            for ((i, ln) in lines.withIndex()) {
                if (ln.isBlank()) {
                    continue
                }

                val cmd = ln.split("\\s+".toRegex()).first()
                val data = mutableListOf<String>()

                when (cmd.toLowerCase()) {
                    "name", "set" -> data.add(ln)
                    "solved", "move" -> data.addAll(untilNextEnd(lines, i))
                }

                if (data.size > 0) {
                    cmdGroups.getOrPut(cmd) { mutableMapOf() }[ln] = data
                }
            }

            return cmdGroups
        }

        private fun untilNextEnd(lines: List<String>, currPointer: Int): List<String> {
            for ((i, ln) in lines.withIndex()) {
                if (i > currPointer && ln.trim().toLowerCase() == "end") {
                    return lines.subList(currPointer + 1, i)
                }
            }

            return lines.drop(currPointer)
        }

        fun loadKPosition(pieceTypes: Set<PieceType>, defLines: List<String>): PuzzleState { // TODO beautify
            val configurationMap = mutableMapOf<PieceType, PieceState>()
            val pieceTypeNames = pieceTypes.map { it.name to it }.toMap() // Help for parsing

            for ((i, ln) in defLines.withIndex()) {
                if (ln in pieceTypeNames.keys) {
                    val currType = pieceTypeNames.getValue(ln)

                    val permString = defLines[i + 1]

                    val orientCanString = if (i + 2 < defLines.size) defLines[i + 2] else "End"
                    val orientString = if (orientCanString in pieceTypeNames.keys || orientCanString == "End")
                        currType.numPieces.filledArray(0).joinToString(" ") else orientCanString

                    configurationMap[currType] = permString.split("\\s+".toRegex()).map(String::toInt).toTypedArray() to
                            orientString.split("\\s+".toRegex()).map(String::toInt).toTypedArray()
                }
            }

            return configurationMap
        }

        protected fun movePuzzle(current: PuzzleState, moveDef: PuzzleState) {
            for ((pt, m) in current) {
                movePieces(pt, m, moveDef.getValue(pt))
            }
        }

        protected fun movePieces(type: PieceType, current: PieceState, moveDef: PieceState) {
            val permTargets = Array(type.numPieces) { current.first[moveDef.first[it] - 1] }
            val orientTargets = Array(type.numPieces) { current.second[moveDef.first[it] - 1] }

            for (i in 0 until type.numPieces) {
                current.first[i] = permTargets[i]
                current.second[i] = (orientTargets[i] + moveDef.second[i]) % type.targetsPerPiece
            }
        }

        fun bruteForcePowerMoves(solved: PuzzleState, moveDef: PuzzleState): List<PuzzleState> {
            val powerMoves = mutableListOf<PuzzleState>()
            val execState = solved.clone()

            movePuzzle(execState, moveDef)

            while (!execState.deepEquals(solved)) {
                powerMoves.add(execState.clone())
                movePuzzle(execState, moveDef)
            }

            return powerMoves
        }
    }
}