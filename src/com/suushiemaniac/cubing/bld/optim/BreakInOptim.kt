package com.suushiemaniac.cubing.bld.optim

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import com.suushiemaniac.cubing.bld.model.source.AlgSource

class BreakInOptim(val source: AlgSource, val refCube: BldPuzzle = CubicPuzzle.FIVE.analyzingPuzzle, fullCache: Boolean = true) {
    val cache: MutableMap<PieceType, MutableMap<String, List<String>>> = mutableMapOf()

    init {
        for (type in refCube.pieceTypes) {
            val typeMap = mutableMapOf<String, List<String>>()

            if (fullCache) {
                for (letter in refCube.getLetteringScheme(type)) {
                    typeMap[letter] = this.optimizeBreakInTargetsAfter(letter, type)
                }
            }

            this.cache[type] = typeMap
        }
    }

    fun optimizeBreakInTargetsAfter(target: String, type: PieceType): List<String> {
        return this.cache.getOrPut(type) { mutableMapOf() }.getOrPut(target) {
            val algList = mutableListOf<Algorithm>()
            val targetMap = mutableMapOf<Algorithm, String>()

            for (t in this.refCube.getLetteringScheme(type)) {
                val sourceList = this.source.getAlgorithms(type, target + t)

                for (alg in sourceList) {
                    algList += alg
                    targetMap[alg] = t
                }
            }

            return algList.sortedWith(AlgComparator.SINGLETON)
                    .map { targetMap.getValue(it) }
        }
    }

    fun optimizeBreakInAlgorithmsAfter(target: String, type: PieceType): List<Algorithm> {
        return this.optimizeBreakInTargetsAfter(target, type)
                .flatMap { this.source.getAlgorithms(type, target + it) }
    }
}