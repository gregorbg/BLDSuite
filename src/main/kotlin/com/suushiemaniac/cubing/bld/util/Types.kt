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

fun PieceState.countEquals(other: PieceState?, allowWildcard: Boolean = false): Int {
    if (other == null) {
        return 0
    }

    val toCmp = other.clone()

    if (allowWildcard) {
        for (i in toCmp.first.indices) { // FIXME for orientation (.second) as well?
            if (toCmp.first[i] == -1) {
                toCmp.first[i] = this.first[i]
            }
        }
    }
    
    return this.first.zip(toCmp.first).zip(this.second.zip(toCmp.second)).sumBy { (a, b) -> if (a.first == b.first && a.second == b.second) 1 else 0 }
}

fun PuzzleState.countEquals(other: PuzzleState, allowWildcard: Boolean = false): Int {
    return this.entries.sumBy { (pt, st) -> st.countEquals(other[pt], allowWildcard) }
}