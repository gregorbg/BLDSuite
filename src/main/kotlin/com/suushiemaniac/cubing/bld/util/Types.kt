package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.bld.model.PieceType

typealias PieceState = Pair<Array<Int>, Array<Int>>
typealias PuzzleState = Map<PieceType, PieceState>

fun PieceState.clone(): PieceState {
    return this.first.copyOf() to this.second.copyOf()
}

fun PuzzleState.clone(): PuzzleState {
    return this.mapValues { it.value.clone() }
}

fun PieceState.deepEquals(other: PieceState, allowWildcard: Boolean = false): Boolean {
    val toCmp = this.clone()
    
    if (allowWildcard) {
        for (i in toCmp.first.indices) { // FIXME for orientation (.second) as well?
            if (toCmp.first[i] == -1) {
                toCmp.first[i] = other.first[i]
            }
        }
    }

    return other.first.contentEquals(toCmp.first) && other.second.contentEquals(toCmp.second)
}

fun PuzzleState.deepEquals(other: PuzzleState, allowWildcard: Boolean = false): Boolean {
    return this.keys.containsAll(other.keys) && (allowWildcard || other.keys.containsAll(this.keys))
            && other.all { it.value.deepEquals(this.getValue(it.key), allowWildcard) }
}

fun PieceState.countEquals(other: PieceState?): Int {
    if (other == null) {
        return 0
    }

    val bigZip = this.first.zip(other.first).zip(this.second.zip(other.second))
    return bigZip.sumBy { (p, o) -> if (p.first == p.second && o.first == o.second) 1 else 0 }
}

fun PuzzleState.countEquals(other: PuzzleState): Int {
    return other.entries.sumBy { (pt, st) -> st.countEquals(this[pt]) }
}