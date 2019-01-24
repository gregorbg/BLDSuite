package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType

object BruteForceUtil {
    val ALPHABET = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

    fun <T> Iterable<T>.permute(length: Int, inclusive: Boolean = false, mayRepeat: Boolean = false): Sequence<List<T>> {
        when {
            length < 1 -> return emptySequence()
            length == 1 -> return this.asSequence().map { listOf(it) }
            else -> return generateSequence<List<T>> {
                if (inclusive)
                    for (i in 1 until length)
                        for (subPerm in permute(i, false, mayRepeat))
                            subPerm // FIXME

                for (prevPermute in this.permute(length - 1, false, mayRepeat)) {
                    for (blockObj in this) {
                        if (mayRepeat || !prevPermute.contains(blockObj)) {
                            val nextPermute = prevPermute.toMutableList()
                            nextPermute.add(blockObj)

                            nextPermute // FIXME
                        }
                    }
                }

                null
            }
        }
    }

    fun <T> Array<T>.permute(length: Int, inclusive: Boolean = false, mayRepeat: Boolean = false): Sequence<List<T>> {
        return this.toList().permute(length, inclusive, mayRepeat)
    }

    fun Array<String>.permuteStr(length: Int, inclusive: Boolean = false, mayRepeat: Boolean = false): Sequence<String> {
        return this.permute(length, inclusive, mayRepeat).map { it.joinToString("") }
    }

    fun bruteForceAlg(analyze: GPuzzle, cycle: PieceCycle, type: PieceType, alphabet: Array<Move>, prune: Int = 21) {
        for (len in 1 until prune) {
            println("Trying length $len…")
            val moves = alphabet.permute(len, inclusive = false, mayRepeat = true).map { SimpleAlg(it) }

            for (alg in moves) {
                if (analyze.solves(type, alg, cycle, false)) {
                    println(alg.toFormatString())
                }
            }
        }
    }
}