package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType

typealias PieceState = Pair<Array<Int>, Array<Int>>
typealias PuzzleState = Map<PieceType, PieceState>

fun PieceState.clone(): PieceState {
    return this.first.copyOf() to this.second.copyOf()
}

fun PuzzleState.clone(): PuzzleState {
    return this.mapValues { it.value.clone() }
}

fun PieceState.deepEquals(other: PieceState): Boolean {
    return this.first.contentEquals(other.first) && this.second.contentEquals(other.second)
}

fun PuzzleState.deepEquals(other: PuzzleState): Boolean {
    return this.keys.containsAll(other.keys) && other.keys.containsAll(this.keys)
            && this.keys.all { this.getValue(it).deepEquals(other.getValue(it)) }
}