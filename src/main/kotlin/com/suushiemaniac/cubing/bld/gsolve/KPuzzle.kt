package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.util.*
import com.suushiemaniac.cubing.bld.util.StringUtil.splitAtWhitespace
import com.suushiemaniac.cubing.bld.util.StringUtil.splitLines
import com.suushiemaniac.cubing.bld.util.CollectionUtil.filledList

import java.io.File

open class KPuzzle(val reader: NotationReader, val commandMap: Map<String, List<String>>) {
    constructor(reader: NotationReader, defFile: File) : this(reader, groupByCommand(defFile.readLines()))

    val pieceTypes = this.loadPieceTypes()

    val solvedState = this.loadSolvedState().toMutableMap()
    val puzzleState = this.solvedState.toMutableMap()

    val moveDefinitions = this.loadMoves()

    private fun loadPieceTypes(): Set<PieceType> {
        val pieceTypes = mutableSetOf<PieceType>()
        val pieceTypeCommands = this.commandMap["Set"] ?: emptyList()

        for (ptCmd in pieceTypeCommands) {
            val (_, title, perm, orient) = ptCmd.splitAtWhitespace()
            pieceTypes.add(PieceType(title, perm.toInt(), orient.toInt()))
        }

        return pieceTypes
    }

    protected fun loadSolvedState(): PuzzleState {
        val solvedCommand = this.commandMap.getValue("Solved").first().splitLines()

        return loadKPosition(this.pieceTypes, solvedCommand.drop(1))
    }

    private fun loadMoves(): Map<Move, PuzzleState> {
        val moveDefs = mutableMapOf<Move, PuzzleState>()
        val moveCommands = this.commandMap.getOrDefault("Move", emptyList()) +
                this.commandMap.getOrDefault("CompositeMove", emptyList())

        for (moveDef in moveCommands) {
            val moveLines = moveDef.splitLines()
            val moveHeader = moveLines.first().splitAtWhitespace()

            val moveConfig = if (moveHeader.first().startsWith("Composite")) {
                val compositeDef = this.reader.parse(moveLines.drop(1).joinToString(""))
                val identityMove = computeIdentityMove(this.solvedState)

                compositeDef.mapNotNull(moveDefs::get).map { it.deepCopy() }.fold(identityMove, ::movePuzzle)
            } else {
                loadKPosition(this.pieceTypes, moveLines.drop(1))
            }

            moveDefs += bruteForcePowerMoves(this.solvedState, moveConfig, this.reader, moveHeader.last())
        }

        return moveDefs
    }

    protected fun applyScramble(scramble: Algorithm) = scramblePuzzle(this.puzzleState, scramble, this.moveDefinitions)
    protected fun hypotheticalScramble(scramble: Algorithm) = scramblePuzzle(this.puzzleState.deepCopy(), scramble, this.moveDefinitions)

    companion object {
        fun groupByCommand(lines: List<String>): Map<String, List<String>> {
            val cmdGroups = mutableMapOf<String, MutableList<String>>()
            val usefulLines = lines.filter { it.isNotBlank() }

            for ((i, ln) in usefulLines.withIndex()) {
                val cmd = ln.splitAtWhitespace().first()

                val data = when (cmd.toLowerCase()) {
                    "solved", "move", "paritydependency", "lettering", "orientation", "compositemove" -> untilNextEnd(usefulLines, i).joinToString("\n")
                    else -> ln
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

        inline fun <reified T> loadFilePosition(pieceTypes: Set<PieceType>, defLines: List<String>, lineTransform: (Pair<String, String>) -> T): Map<PieceType, Array<T>> {
            val configurationMap = mutableMapOf<PieceType, Array<T>>()
            val pieceTypeNames = pieceTypes.map { it.name to it }.toMap() // Help for parsing

            for ((i, ln) in defLines.withIndex()) {
                if (ln in pieceTypeNames.keys) {
                    val currType = pieceTypeNames.getValue(ln)

                    val permString = defLines[i + 1]
                    val permArray = permString.splitAtWhitespace()

                    val orientCanString = if (i + 2 in defLines.indices) defLines[i + 2] else "End"
                    val orientArray = if (orientCanString in pieceTypeNames.keys || orientCanString == "End")
                        permArray.size.filledList("0") else orientCanString.splitAtWhitespace()

                    configurationMap[currType] = permArray.zip(orientArray).map(lineTransform).toTypedArray()
                }
            }

            return configurationMap
        }

        fun loadKPosition(pieceTypes: Set<PieceType>, defLines: List<String>): PuzzleState {
            return loadFilePosition(pieceTypes, defLines) {
                Piece(it.first.toIntOrNull() ?: -1, it.second.toInt())
            }
        }

        fun computeIdentityMove(state: PuzzleState): PuzzleState {
            return state.mapValues { Array(it.value.size) { i -> Piece(i + 1, 0) } }
        }

        fun scramblePuzzle(current: PuzzleState, scramble: Algorithm, moveDefinitions: Map<Move, PuzzleState>): PuzzleState {
            return current.apply {
                scramble.mapNotNull(moveDefinitions::get).forEach { movePuzzle(this, it) }
            }
        }

        @JvmStatic protected fun movePuzzle(current: PuzzleState, moveDef: PuzzleState): PuzzleState {
            return current.apply {
                for ((pt, m) in moveDef) {
                    movePieces(this.getValue(pt), pt, m)
                }
            }
        }

        @JvmStatic protected fun movePieces(current: PieceState, type: PieceType, moveDef: PieceState) {
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

        fun bruteForcePowerMoves(solved: PuzzleState, moveDef: PuzzleState, reader: NotationReader, moveBaseName: String): Map<Move, PuzzleState> {
            val movePowers = mutableListOf<PuzzleState>()
            val currentPower = moveDef.deepCopy()

            while (!movePuzzle(solved.deepCopy(), currentPower).deepEquals(solved)) {
                movePowers.add(currentPower.deepCopy())

                movePuzzle(currentPower, moveDef)
            }

            val moveDefs = mutableMapOf<Move, PuzzleState>()

            for ((i, pow) in movePowers.withIndex()) {
                val powerMove = StringBuilder(moveBaseName)
                val shortCount = if (i <= movePowers.size / 2) i + 1 else movePowers.size - i

                if (shortCount > 1) {
                    powerMove.append(shortCount)
                }

                if (i > movePowers.size / 2) {
                    powerMove.append("'")
                }

                val modelMove = reader.parse(powerMove.toString()).firstMove()

                moveDefs[modelMove] = pow
            }

            return moveDefs
        }

        fun preInstalledConfig(tag: String) = File(KPuzzle::class.java.classLoader.getResource("kpuzzle/$tag.def").toURI())
    }
}