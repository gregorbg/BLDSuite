package com.suushiemaniac.cubing.bld.gsolve

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.util.ArrayUtil.filledArray
import com.suushiemaniac.lang.json.JSON

import java.io.File

abstract class KPuzzle(defFile: File) : Cloneable {
    data class PieceConfig(val permutation: Int, val orientation: Int = 0)

    init {
        val contentLines = defFile.readLines().filter { it.isNotBlank() }

        val pzlName = contentLines[0].substring("Name ".length)

        val setDefs = contentLines.drop(1).takeWhile { it.startsWith("Set") }

        val solvedDef = contentLines.drop(1 + setDefs.size).takeWhile { it != "End" }
        val moveDefs = contentLines.drop(2 + setDefs.size + solvedDef.size).fold()
    }

    val solvedState = this.initState()
    val puzzleState = this.initState().toMutableMap()

    val moves = this.loadMoves()

    fun loadMoves(): Map<Move, Map<PieceType, Array<PieceConfig>>> {
        val filename = "permutations/$model.def"
        val fileURL = this.javaClass.classLoader.getResource(filename)

        val fileLines = File(fileURL.toURI()).readLines()

        val permutations = hashMapOf<Move, Map<PieceType, Array<PieceConfig>>>()

        val json = JSON.fromStream(fileURL) ?: return permutations

        for ((key, moveJson) in json.nativeMap { it }) {
            val typeMap = this.getPieceTypes(true).map {
                it to moveJson[it.name]!!.nativeList(JSON::intValue).toTypedArray()
            }.toMap().denullify()

            val move = model.reader.parse(key).firstMove()
            permutations[move] = typeMap
        }

        return permutations
    }

    protected fun initState(): Map<PieceType, Array<PieceConfig>> {
        return this.getPieceTypes(true).associateWith { pt ->
            val ref = this.cubies.getValue(pt)

            Array(ref.size) { n ->
                ref[n].copy()
            }
        }
    }

    fun loadScramble(scramble: Algorithm) {
        this.resetPuzzleState()
        this.applyScramble(scramble)
    }

    protected fun resetPuzzleState() {
        for ((pt, solvedConf) in this.solvedState) {
            val resetConf = solvedConf.map { it.copy() }
            this.puzzleState[pt] = resetConf.toTypedArray()
        }
    }

    protected fun applyScramble(scramble: Algorithm) {
        scramble.filter(this.moves.keys::contains)
                .forEach(this::applyMove)
    }

    protected fun applyMove(permutation: Move) {
        for (type in this.getPieceTypes(true)) {
            val current = this.state.getValue(type)
            val perm = this.moves.getValue(permutation).getValue(type)

            this.applyPermutations(current, perm)
        }
    }

    protected fun applyPermutations(current: Array<Int>, perm: Array<Int>) {
        val exchanges = perm.size.filledArray(-1)

        for (i in exchanges.indices) {
            if (perm[i] != -1) {
                exchanges[perm[i]] = current[i]
            }
        }

        for (i in exchanges.indices) {
            if (exchanges[i] != -1) {
                current[i] = exchanges[i]
            }
        }
    }

    protected fun isSolved(type: PieceType? = null): Boolean {
        if (type == null) {
            return this.puzzleState.keys.all { this.isSolved(it) }
        }

        return this.puzzleState.getValue(type).contentEquals(this.solvedState.getValue(type))
    }
}