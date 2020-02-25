package com.suushiemaniac.cubing.bld.model.puzzledef

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.PieceType.Companion.findByName
import com.suushiemaniac.cubing.bld.util.PuzzleState
import com.suushiemaniac.cubing.bld.util.StringUtil.splitLines

data class GCommands(
        val baseCommands: KCommands,
        val letterSchemes: Map<PieceType, Array<String>>,
        val buffers: Map<PieceType, List<Int>>,
        val reorientMethod: ReorientMethod,
        val reorientState: PuzzleState,
        val misOrientMethod: MisOrientMethod,
        val parityDependencyFixes: Map<PieceType, PuzzleState>,
        val parityFirstPieceTypes: List<PieceType>,
        val executionPieceTypes: List<PieceType>,
        val skeletonReorientationMoves: Algorithm
) {
    companion object {
        fun parse(commandMap: CommandMap, kCommands: KCommands): GCommands {
            val pieceTypes = kCommands.pieceTypes
            val reader = kCommands.reader

            return GCommands(
                    kCommands,
                    loadLetterSchemes(commandMap, pieceTypes),
                    loadBuffers(commandMap, pieceTypes),
                    loadReorientMethod(commandMap),
                    loadReorientState(commandMap, pieceTypes),
                    loadMisOrientMethod(commandMap),
                    loadParityDependencyFixes(commandMap, pieceTypes),
                    loadParityFirstPieceTypes(commandMap, pieceTypes),
                    loadExecutionPieceTypes(commandMap, pieceTypes),
                    loadSkeletonReorientationMoves(commandMap, reader)
            )
        }

        fun loadLetterSchemes(commandMap: CommandMap, pieceTypes: Set<PieceType>): Map<PieceType, Array<String>> {
            val letterLines = commandMap.getValue("Lettering").first()

            return KCommands.loadFilePosition(pieceTypes, letterLines[0].splitLines()) { it.first }
        }

        fun loadBuffers(commandMap: CommandMap, pieceTypes: Set<PieceType>): Map<PieceType, List<Int>> {
            val bufferCommands = commandMap.getValue("Buffer")

            return bufferCommands.groupBy(
                    { pieceTypes.findByName(it[0])!! },
                    { it.drop(1).map(String::toInt) })
                    .mapValues { it.value.map { b -> GPuzzle.pieceToTarget(it.key, b[0] - 1, b[1]) } }
        }

        fun loadReorientMethod(commandMap: CommandMap): ReorientMethod {
            val methodRaw = commandMap.getValue("Orientation").first().first()
            return ReorientMethod.valueOf(methodRaw.toUpperCase())
        }

        fun loadReorientState(commandMap: CommandMap, pieceTypes: Set<PieceType>): PuzzleState {
            val stateLines = commandMap.getValue("Orientation").first()

            return KCommands.loadKPosition(pieceTypes, stateLines[1].splitLines())
        }

        fun loadMisOrientMethod(commandMap: CommandMap): MisOrientMethod {
            val methodRaw = commandMap.getValue("MisOrient").first().first()
            return MisOrientMethod.valueOf(methodRaw.toUpperCase())
        }

        fun loadParityDependencyFixes(commandMap: CommandMap, pieceTypes: Set<PieceType>): Map<PieceType, PuzzleState> {
            val dependencyFixDescriptions = commandMap["ParityDependency"].orEmpty()

            return dependencyFixDescriptions.associateBy(
                    { pieceTypes.findByName(it[0])!! },
                    { KCommands.loadKPosition(pieceTypes, it[1].splitLines()) })
        }

        fun loadParityFirstPieceTypes(commandMap: CommandMap, pieceTypes: Set<PieceType>): List<PieceType> {
            val firstPieceTypes = commandMap["ParityFirst"].orEmpty()

            return firstPieceTypes.firstOrNull()?.mapNotNull { pieceTypes.findByName(it) }.orEmpty()
        }

        fun loadExecutionPieceTypes(commandMap: CommandMap, pieceTypes: Set<PieceType>) =
                commandMap.getValue("Execution").first().mapNotNull { pieceTypes.findByName(it) }

        fun loadSkeletonReorientationMoves(commandMap: CommandMap, reader: NotationReader): Algorithm {
            val skeletonOrientation = commandMap["SkeletonOrientation"]
                    ?.first()?.joinToString(" ").orEmpty()

            return reader.parse(skeletonOrientation)
        }
    }
}