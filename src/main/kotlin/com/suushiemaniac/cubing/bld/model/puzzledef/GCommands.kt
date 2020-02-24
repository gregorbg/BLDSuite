package com.suushiemaniac.cubing.bld.model.puzzledef

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.util.CommandMap
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.StringUtil.splitLines

data class GCommands(
        val letterSchemes: Map<PieceType, Array<String>>,
        val buffers: Map<PieceType, List<Int>>,
        val reorientMethod: String,
        val reorientState: PuzzleState,
        val misorientMethod: String,
        val parityDependencyFixes: Map<PieceType, PuzzleState>,
        val parityFirstPieceTypes: List<PieceType>,
        val executionPieceTypes: List<PieceType>,
        val skeletonReorientationMoves: Algorithm
) {
    companion object {
        fun parse(commandMap: CommandMap): GCommands {
            return GCommands()
        }

        fun Set<PieceType>.findPieceTypeByName(name: String): PieceType {
            return find { it.name == name }!!
        }

        fun loadLetterSchemes(commandMap: CommandMap, pieceTypes: Set<PieceType>): Map<PieceType, Array<String>> {
            val letterLines = commandMap.getValue("Lettering").first()

            return KCommands.loadFilePosition(pieceTypes, letterLines[0].splitLines()) { it.first }
        }

        fun loadBuffers(commandMap: CommandMap, pieceTypes: Set<PieceType>): Map<PieceType, List<Int>> {
            val bufferCommands = commandMap.getValue("Buffer")

            return bufferCommands.groupBy(
                    { pieceTypes.findPieceTypeByName(it[0]) },
                    { it.drop(1).map(String::toInt) })
                    .mapValues { it.value.map { b -> GPuzzle.pieceToTarget(it.key, b[0] - 1, b[1]) } }
        }

        fun loadReorientMethod(commandMap: CommandMap) = commandMap.getValue("Orientation").first().first()

        fun loadReorientState(commandMap: CommandMap, pieceTypes: Set<PieceType>): PuzzleState {
            val stateLines = commandMap.getValue("Orientation").first()

            return KCommands.loadKPosition(pieceTypes, stateLines[1].splitLines())
        }

        fun loadMisOrientMethod(commandMap: CommandMap) = commandMap.getValue("MisOrient").first().first()

        fun loadParityDependencyFixes(commandMap: CommandMap, pieceTypes: Set<PieceType>): Map<PieceType, PuzzleState> {
            val dependencyFixDescriptions = commandMap["ParityDependency"].orEmpty()

            return dependencyFixDescriptions.associateBy(
                    { pieceTypes.findPieceTypeByName(it[0]) },
                    { KCommands.loadKPosition(pieceTypes, it[1].splitLines()) })
        }

        fun loadParityFirstPieceTypes(commandMap: CommandMap, pieceTypes: Set<PieceType>): List<PieceType> {
            val firstPieceTypes = commandMap["ParityFirst"].orEmpty()

            return firstPieceTypes.firstOrNull()?.map { pieceTypes.findPieceTypeByName(it) }.orEmpty()
        }

        fun loadExecutionPieceTypes(commandMap: CommandMap, pieceTypes: Set<PieceType>) = commandMap.getValue("Execution").first().map { pieceTypes.findPieceTypeByName(it) }

        fun loadSkeletonReorientationMoves(commandMap: CommandMap): Algorithm {
            val skeletonOrientation = commandMap["SkeletonOrientation"]
                    ?.first()?.joinToString(" ").orEmpty()

            return this.reader.parse(skeletonOrientation)
        }
    }
}