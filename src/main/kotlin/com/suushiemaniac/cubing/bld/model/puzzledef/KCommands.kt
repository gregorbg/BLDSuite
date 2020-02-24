package com.suushiemaniac.cubing.bld.model.puzzledef

import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.util.CollectionUtil.filledList
import com.suushiemaniac.cubing.bld.util.CollectionUtil.headWithTail
import com.suushiemaniac.cubing.bld.util.CommandMap
import com.suushiemaniac.cubing.bld.util.Piece
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.StringUtil.splitAtWhitespace
import com.suushiemaniac.cubing.bld.util.deepCopy
import com.suushiemaniac.cubing.bld.util.StringUtil.splitLines
import java.io.File
import java.io.InputStream

data class KCommands(val pieceTypes: Set<PieceType>, val solvedState: PuzzleState, val moveDefinitions: Map<Move, PuzzleState>) {
    companion object {
        fun parse(commandMap: CommandMap): KCommands {
            val pieceTypes = loadPieceTypes(commandMap)
            val solvedState = loadSolvedState(commandMap, pieceTypes)
            val moveDefs = loadMoves(commandMap, pieceTypes, solvedState)

            return KCommands(pieceTypes, solvedState, moveDefs)
        }

        private fun loadPieceTypes(commandMap: CommandMap): Set<PieceType> {
            val pieceTypeCommands = commandMap["Set"].orEmpty()

            return pieceTypeCommands.map { PieceType(it[0], it[1].toInt(), it[2].toInt()) }.toSet()
        }

        private fun loadSolvedState(commandMap: CommandMap, pieceTypes: Set<PieceType>): PuzzleState {
            val solvedCommand = commandMap.getValue("Solved").first()

            return loadKPosition(pieceTypes, solvedCommand[0].splitLines())
        }

        private fun loadMoves(commandMap: CommandMap, pieceTypes: Set<PieceType>, solvedState: PuzzleState): Map<Move, PuzzleState> {
            val moveDefs = mutableMapOf<Move, PuzzleState>()
            val moveCommands = commandMap["Move"].orEmpty()

            for ((moveBaseName, moveDef) in moveCommands) {
                val moveConfig = loadKPosition(pieceTypes, moveDef.splitLines())

                moveDefs += KPuzzle.bruteForcePowerMoves(solvedState, moveConfig, moveBaseName)
            }

            val compositeCommands = commandMap["CompositeMove"].orEmpty()
            val identityMove = KPuzzle.computeIdentityMove(solvedState)

            for ((moveBaseName, moveSeq) in compositeCommands) {
                val moveConfig = this.reader.parse(moveSeq).mapNotNull(moveDefs::get).fold(identityMove.deepCopy(), KPuzzle.Companion::movePuzzle)

                moveDefs += KPuzzle.bruteForcePowerMoves(solvedState, moveConfig, moveBaseName)
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
            val pieceTypeNames = pieceTypes.associateBy { it.name }

            for ((i, ln) in defLines.withIndex()) {
                if (ln in pieceTypeNames.keys) {
                    val currType = pieceTypeNames.getValue(ln)

                    val permString = defLines[i + 1]
                    val permArray = permString.splitAtWhitespace()

                    val orientCanString = if (i + 2 in defLines.indices) defLines[i + 2] else "End"
                    val orientArray = if (orientCanString in pieceTypeNames.keys || orientCanString == "End")
                        permArray.size.filledList(0) else orientCanString.splitAtWhitespace().map { it.toInt() }

                    configurationMap[currType] = permArray.zip(orientArray).map(lineTransform).toTypedArray()
                }
            }

            return configurationMap
        }

        private val EXTRA_COMMANDS = listOf("Solved", "Move", "ParityDependency", "Lettering", "Orientation", "CompositeMove")

        fun groupByCommand(lines: List<String>): CommandMap {
            val cmdGroups = mutableMapOf<String, MutableList<List<String>>>()
            val usefulLines = lines.filter { it.isNotBlank() }

            for ((i, ln) in usefulLines.withIndex()) {
                val (cmd, args) = ln.splitAtWhitespace().headWithTail()
                val data = args.toMutableList()

                if (cmd in EXTRA_COMMANDS) {
                    data.add(untilNextEnd(usefulLines, i + 1).joinToString("\n"))
                }

                cmdGroups.getOrPut(cmd) { mutableListOf() }.add(data)
            }

            return cmdGroups
        }

        private fun untilNextEnd(lines: List<String>, currPointer: Int): List<String> {
            for ((i, ln) in lines.withIndex()) {
                if (i > currPointer && ln.trim().toLowerCase() == "end") {
                    return lines.subList(currPointer, i)
                }
            }

            return lines.drop(currPointer)
        }

        fun loadFileStream(kFile: InputStream) = groupByCommand(kFile.reader().readLines())
        fun loadFile(kFile: File) = groupByCommand(kFile.readLines())
    }
}