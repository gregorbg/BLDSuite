package com.suushiemaniac.cubing.bld.model.puzzledef

import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.PieceType.Companion.findByName
import com.suushiemaniac.cubing.bld.util.CollectionUtil.filledList
import com.suushiemaniac.cubing.bld.util.Piece
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.StringUtil.splitAtWhitespace
import com.suushiemaniac.cubing.bld.util.deepCopy
import com.suushiemaniac.cubing.bld.util.StringUtil.splitLines

data class KCommands(
        val reader: NotationReader,
        val pieceTypes: Set<PieceType>,
        val solvedState: PuzzleState,
        val moveDefinitions: Map<Move, PuzzleState>
) {
    companion object {
        fun parse(commandMap: CommandMap, reader: NotationReader): KCommands {
            val pieceTypes = loadPieceTypes(commandMap)
            val solvedState = loadSolvedState(commandMap, pieceTypes)
            val moveDefs = loadMoves(commandMap, pieceTypes, solvedState, reader)

            return KCommands(reader, pieceTypes, solvedState, moveDefs)
        }

        private fun loadPieceTypes(commandMap: CommandMap): Set<PieceType> {
            val pieceTypeCommands = commandMap["Set"].orEmpty()

            return pieceTypeCommands.map { PieceType(it[0], it[1].toInt(), it[2].toInt()) }.toSet()
        }

        private fun loadSolvedState(commandMap: CommandMap, pieceTypes: Set<PieceType>): PuzzleState {
            val solvedCommand = commandMap.getValue("Solved").first()

            return loadKPosition(pieceTypes, solvedCommand[0].splitLines())
        }

        private fun loadMoves(commandMap: CommandMap, pieceTypes: Set<PieceType>, solvedState: PuzzleState, reader: NotationReader): Map<Move, PuzzleState> {
            val moveDefs = mutableMapOf<Move, PuzzleState>()
            val moveCommands = commandMap["Move"].orEmpty()

            for ((moveBaseName, moveDef) in moveCommands) {
                val moveConfig = loadKPosition(pieceTypes, moveDef.splitLines())
                val baseMove = reader.parse(moveBaseName).firstMove()

                moveDefs += KPuzzle.bruteForcePowerMoves(solvedState, moveConfig, baseMove)
            }

            val compositeCommands = commandMap["CompositeMove"].orEmpty() // FIXME topo sort on composite deps
            val identityMove = KPuzzle.computeIdentityMove(solvedState)

            for ((moveBaseName, moveSeq) in compositeCommands) {
                val moveConfig = reader.parse(moveSeq).mapNotNull(moveDefs::get)
                        .fold(identityMove.deepCopy(), KPuzzle.Companion::movePuzzle)
                val baseMove = reader.parse(moveBaseName).firstMove()

                moveDefs += KPuzzle.bruteForcePowerMoves(solvedState, moveConfig, baseMove)
            }

            return moveDefs
        }

        fun loadKPosition(pieceTypes: Set<PieceType>, defLines: List<String>): PuzzleState {
            return loadFilePosition(pieceTypes, defLines) {
                Piece(it.first.toIntOrNull() ?: -1, it.second)
            }
        }

        inline fun <reified T> loadFilePosition(pieceTypes: Set<PieceType>, defLines: List<String>, lineTransform: (Pair<String, Int>) -> T): Map<PieceType, Array<T>> {
            val configurationMap = mutableMapOf<PieceType, Array<T>>()
            val pieceTypeNames = pieceTypes.map { it.name }

            for ((i, ln) in defLines.withIndex()) {
                val currType = pieceTypes.findByName(ln) ?: continue

                val permString = defLines[i + 1]
                val permArray = permString.splitAtWhitespace()

                val orientCanString = defLines.getOrNull(i + 2) ?: CommandMap.LINE_COMMAND_TERMINATOR
                val orientArray = if (orientCanString in pieceTypeNames || orientCanString == CommandMap.LINE_COMMAND_TERMINATOR)
                    permArray.size.filledList(0) else orientCanString.splitAtWhitespace().map { it.toInt() }

                configurationMap[currType] = permArray.zip(orientArray).map(lineTransform).toTypedArray()
            }

            return configurationMap
        }
    }
}