package com.suushiemaniac.cubing.bld.analyze

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.TwistyPuzzle
import com.suushiemaniac.lang.json.JSON

abstract class BldPuzzleTrans(var model: TwistyPuzzle) : Cloneable {
    constructor(model: TwistyPuzzle, scramble: Algorithm): this(model) {
        this.parseScramble(scramble)
    }

    val permutations = this.loadPermutations()
    val cubies = this.initCubies()

    var scramble: Algorithm = SimpleAlg()

    var scrambleOrientationPremoves = SimpleAlg()
    var letterPairLanguage = System.getProperty("user.language")

    var letterSchemes: Nothing = TODO()
    var avoidBreakIns: Nothing = TODO()
    var optimizeBreakIns: Nothing = TODO()

    var mainBuffers = this.readCurrentBuffers()
    var backupBuffers: Nothing = TODO()

    var algSource = null
    var misOrientMethod = MisOrientMethod.SOLVE_DIRECT

    fun loadPermutations(): Map<Move, Map<PieceType, Array<Int>>> {
        val filename = "permutations/$model.json"
        val fileURL = this.javaClass.getResource(filename)

        val json = JSON.fromURL(fileURL)

        val permutations = hashMapOf<Move, Map<PieceType, Array<Int>>>()

        for (key in json.nativeKeySet()) {
            val typeMap = hashMapOf<PieceType, Array<Int>>()
            val moveJson = json.get(key)

            for (type in this.getPieceTypes(true)) {
                val permutationList = moveJson.get(type.name).nativeList(JSON::intValue)
                val permutationArray = permutationList.toTypedArray()

                typeMap[type] = permutationArray
            }

            val move = model.reader.parse(key).firstMove()
            permutations[move] = typeMap
        }

        return permutations
    }

    fun initCubies(): Map<PieceType, Array<Array<Int>>> {
        val cubies = this.getDefaultCubies().toMutableMap()
        this.getOrientationPieceTypes().forEach { cubies.remove(it) }

        return cubies
    }

    fun parseScramble(scramble: Algorithm) {
        this.resetPuzzle()

        this.scramble = scramble

        this.scramblePuzzle(scramble)
        this.solvePuzzle()
    }

    fun getPieceTypes(withOrientationModel: Boolean = false): List<PieceType> {
        val pieceTypes = this.getPermutationPieceTypes().toMutableList()

        if (withOrientationModel) {
            pieceTypes.addAll(this.getOrientationPieceTypes())
        }

        return pieceTypes
    }

    fun readCurrentBuffers(): Map<PieceType, Int> {
        return TODO()
    }

    fun resetPuzzle() {
        TODO()
    }

    fun scramblePuzzle(scramble: Algorithm) {
        TODO()
    }

    fun solvePuzzle() {
        TODO()
    }

    protected abstract fun getPermutationPieceTypes(): List<PieceType>

    protected abstract fun getOrientationPieceTypes(): List<PieceType>

    protected abstract fun getDefaultCubies(): Map<PieceType, Array<Array<Int>>>

    enum class MisOrientMethod {
        SOLVE_DIRECT, SINGLE_TARGET
    }
}