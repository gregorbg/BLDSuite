package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle

object BruteForceUtil {
    val ALPHABET = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

    fun <T> Iterable<T>.permute(length: Int, inclusive: Boolean = false, mayRepeat: Boolean = false): List<List<T>> {
        val moveList = mutableListOf<List<T>>()

        when {
            length < 1 -> return listOf()
            length == 1 -> return this.map { listOf(it) }
            else -> {
                if (inclusive)
                    for (i in 1 until length)
                        moveList.addAll(this.permute(i, false, mayRepeat))

                for (prevPermute in this.permute(length - 1, false, mayRepeat)) {
                    for (blockObj in this) {
                        if (mayRepeat || !prevPermute.contains(blockObj)) {
                            val nextPermute = prevPermute.toMutableList()
                            nextPermute.add(blockObj)

                            moveList.add(nextPermute)
                        }
                    }
                }
            }
        }

        return moveList
    }

    fun <T> Array<T>.permute(length: Int, inclusive: Boolean = false, mayRepeat: Boolean = false): List<List<T>> {
        return this.toList().permute(length, inclusive, mayRepeat)
    }

    fun Array<String>.permuteStr(length: Int, inlusive: Boolean = false, mayRepeat: Boolean = false): List<String> {
        return this.permute(length, inlusive, mayRepeat).map { it.joinToString("") }
    }

    fun bruteForceAlg(lpCase: String, type: PieceType, alphabet: Array<String>, prune: Int = 21) {
        val reader = CubicAlgorithmReader()
        val analyze = CubicPuzzle.THREE_BLD.analyzingPuzzle

        for (len in 1 until prune) {
            println("Trying length $len…")
            val moves = alphabet.permuteStr(len, true)

            for (alg in moves) {
                val current = reader.parse(alg)

                if (analyze.solves(type, current, lpCase, false)) {
                    println(current.toFormatString())
                }
            }
        }
    }
}