package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.util.MathUtil.toInt

data class Piece(val permutation: Int, val orientation: Int = 0)
typealias PieceState = Array<Piece>
typealias PuzzleState = Map<PieceType, PieceState>
typealias CommandMap = Map<String, List<List<String>>>

fun PieceState.deepCopy(): PieceState {
    return this.map { it.copy() }.toTypedArray()
}

fun PuzzleState.deepCopy(): PuzzleState {
    return this.mapValues { it.value.deepCopy() }
}

fun PieceState.deepEquals(other: PieceState, allowWildcard: Boolean = false): Boolean {
    val toCmp = this.clone()
    
    if (allowWildcard) {
        for (i in toCmp.indices) { // FIXME for orientation (.second) as well?
            if (toCmp[i].permutation == -1) {
                toCmp[i] = toCmp[i].copy(permutation = other[i].permutation)
            }
        }
    }

    return other.contentEquals(toCmp)
}

fun PuzzleState.deepEquals(other: PuzzleState, allowWildcard: Boolean = false): Boolean {
    return this.keys.containsAll(other.keys) && (allowWildcard || other.keys.containsAll(this.keys))
            && other.all { it.value.deepEquals(this.getValue(it.key), allowWildcard) }
}

fun PieceState.countEquals(other: PieceState?): Int {
    if (other == null) {
        return 0
    }

    val bigZip = this.zip(other)
    return bigZip.sumBy { (t, o) -> (t == o).toInt() }
}

fun PuzzleState.countEquals(other: PuzzleState): Int {
    return other.entries.sumBy { (pt, st) -> st.countEquals(this[pt]) }
}