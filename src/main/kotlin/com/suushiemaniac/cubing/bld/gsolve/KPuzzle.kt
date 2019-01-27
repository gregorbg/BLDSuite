package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.util.StringUtil.splitAtWhitespace
import com.suushiemaniac.cubing.bld.util.ArrayUtil.filledArray
import com.suushiemaniac.cubing.bld.util.PieceState
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.clone
import com.suushiemaniac.cubing.bld.util.deepEquals

import java.io.File

open class KPuzzle(protected val reader: NotationReader, private val commandMap: Map<String, Map<String, List<String>>>) {
    constructor(reader: NotationReader, defFile: File) : this(reader, groupByCommand(defFile.readLines()))

    val pieceTypes = this.loadPieceTypes()

    val defSolvedState get() = this.loadSolvedState()

    val solvedState = this.defSolvedState.toMutableMap()
    val puzzleState = this.solvedState.toMutableMap()

    val moveDefinitions = this.loadMoves()

    private fun loadPieceTypes(): Set<PieceType> {
        val pieceTypes = mutableSetOf<PieceType>()
        val pieceTypeCommands = this.commandMap.getValue("Set").keys

        for (ptCmd in pieceTypeCommands) {
            val (_, title, perm, orient) = ptCmd.splitAtWhitespace()
            pieceTypes.add(PieceType(title, perm.toInt(), orient.toInt()))
        }

        return pieceTypes
    }

    private fun loadSolvedState(): PuzzleState {
        val solvedCommand = this.commandMap.getValue("Solved").getValue("Solved")

        return loadKPosition(this.pieceTypes, solvedCommand)
    }

    private fun loadMoves(): Map<Move, PuzzleState> {
        val moveDefs = mutableMapOf<Move, PuzzleState>()
        val moveCommands = this.commandMap.getOrDefault("Move", emptyMap()) +
                this.commandMap.getOrDefault("CompositeMove", emptyMap())

        for ((lnKey, moveDef) in moveCommands) {
            val moveBaseName = lnKey.splitAtWhitespace().last()

            val moveConfig = if (lnKey.startsWith("Composite")) {
                val compositeDef = reader.parse(moveDef.joinToString(" "))
                scramblePuzzle(this.defSolvedState, compositeDef, moveDefs)
            } else {
                loadKPosition(this.pieceTypes, moveDef)
            }

            moveDefs += bruteForcePowerMoves(this.solvedState, moveConfig, this.reader, moveBaseName)
        }

        return moveDefs
    }

    protected fun applyScramble(scramble: Algorithm, reset: Boolean = false) {
        if (reset) {
            resetState(this.puzzleState, this.solvedState)
        }

        scramblePuzzle(this.puzzleState, scramble, this.moveDefinitions)
    }

    companion object {
        fun groupByCommand(lines: List<String>): Map<String, Map<String, List<String>>> { // TODO beautify return format
            val cmdGroups = mutableMapOf<String, MutableMap<String, List<String>>>()
            val usefulLines = lines.filter { it.isNotBlank() }

            for ((i, ln) in usefulLines.withIndex()) {
                val cmd = ln.splitAtWhitespace().first()
                val data = mutableListOf<String>()

                when (cmd.toLowerCase()) {
                    "name", "set", "buffer", "misorient", "execution", "parityfirst" -> data.add(ln)
                    "solved", "move", "paritydependency", "lettering", "orientation", "compositemove" -> data.addAll(untilNextEnd(usefulLines, i))
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

        inline fun <reified T> loadFilePosition(pieceTypes: Set<PieceType>, defLines: List<String>, default: T, lineTransform: (String) -> Array<T>): Map<PieceType, Pair<Array<T>, Array<T>>> { // TODO beautify
            val configurationMap = mutableMapOf<PieceType, Pair<Array<T>, Array<T>>>()
            val pieceTypeNames = pieceTypes.map { it.name to it }.toMap() // Help for parsing

            for ((i, ln) in defLines.withIndex()) {
                if (ln in pieceTypeNames.keys) {
                    val currType = pieceTypeNames.getValue(ln)

                    val permString = defLines[i + 1]
                    val permArray = lineTransform(permString)

                    val orientCanString = if (i + 2 in defLines.indices) defLines[i + 2] else "End"
                    val orientArray = if (orientCanString in pieceTypeNames.keys || orientCanString == "End")
                        permArray.size.filledArray(default) else lineTransform(orientCanString)

                    configurationMap[currType] = permArray to orientArray
                }
            }

            return configurationMap
        }

        fun loadKPosition(pieceTypes: Set<PieceType>, defLines: List<String>): PuzzleState {
            return loadFilePosition(pieceTypes, defLines, 0) {
                it.splitAtWhitespace().map { i -> i.toIntOrNull() ?: -1 }.toTypedArray()
            }
        }

        fun scramblePuzzle(current: PuzzleState, scramble: Algorithm, moveDefinitions: Map<Move, PuzzleState>): PuzzleState {
            return current.apply {
                scramble.mapNotNull(moveDefinitions::get).forEach { movePuzzle(this, it) }
            }
        }

        @JvmStatic protected fun movePuzzle(current: PuzzleState, moveDef: PuzzleState) {
            for ((pt, m) in moveDef) {
                movePieces(pt, current.getValue(pt), m)
            }
        }

        @JvmStatic protected fun movePieces(type: PieceType, current: PieceState, moveDef: PieceState) {
            val permTargets = Array(type.permutations) { current.first[moveDef.first[it] - 1] }
            val orientTargets = Array(type.permutations) { current.second[moveDef.first[it] - 1] }

            for (i in 0 until type.permutations) {
                current.first[i] = permTargets[i]
                current.second[i] = (orientTargets[i] + moveDef.second[i]) % type.orientations
            }
        }

        fun resetState(toReset: MutableMap<PieceType, PieceState>, master: PuzzleState) {
            for ((pt, solvedConf) in master) {
                toReset[pt] = solvedConf.clone()
            }
        }

        fun bruteForcePowerMoves(solved: PuzzleState, moveDef: PuzzleState, reader: NotationReader, moveBaseName: String): Map<Move, PuzzleState> {
            val movePowers = mutableListOf<PuzzleState>()
            val execState = solved.clone()

            movePuzzle(execState, moveDef)

            while (!execState.deepEquals(solved)) {
                movePowers.add(execState.clone())
                movePuzzle(execState, moveDef)
            }

            val moveDefs = mutableMapOf<Move, PuzzleState>()

            for ((i, pow) in movePowers.withIndex()) {
                val powerMoveStr = if (i <= movePowers.size / 2) {
                    if (i > 0) "$moveBaseName${i + 1}" else moveBaseName
                } else {
                    if (i < movePowers.size - 1) "$moveBaseName${movePowers.size - i}'" else "$moveBaseName'"
                } // TODO more concise notation?

                val modelMove = reader.parse(powerMoveStr).firstMove()

                moveDefs[modelMove] = pow
            }

            return moveDefs
        }
    }
}