package com.suushiemaniac.cubing.bld.optim

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import com.suushiemaniac.cubing.bld.model.source.AlgSource

class BreakInOptim(val source: AlgSource, val refCube: BldPuzzle = CubicPuzzle.FIVE.analyzingPuzzle, fullCache: Boolean = true) {
    val cache: MutableMap<PieceType, MutableMap<String, List<String?>>> = mutableMapOf()

    init {
        for (type in refCube.pieceTypes) {
            val typeMap = mutableMapOf<String, List<String?>>()

            if (fullCache) {
                for (letter in refCube.getLetteringScheme(type)) {
                    typeMap[letter] = this.optimizeBreakInTargetsAfter(letter, type)
                }
            }

            this.cache[type] = typeMap
        }
    }

    fun optimizeBreakInTargetsAfter(target: String, type: PieceType): List<String?> { // FIXME get rid of ?
        val cache = this.cache.getOrDefault(type, mutableMapOf())[target] // TODO nested map access

        if (cache != null) {
            return cache
        }

        val algList = mutableListOf<Algorithm>()
        val targetMap = mutableMapOf<Algorithm, String>()

        for (t in this.refCube.getLetteringScheme(type)) {
            val sourceList = this.source.getAlgorithms(type, target + t) ?: continue

            for (alg in sourceList) {
                algList += alg
                targetMap[alg] = t
            }
        }

        algList.sortWith(AlgComparator.SINGLETON)

        val optimizedList = algList.map { targetMap[it] }

        this.cache.getOrDefault(type, HashMap())[target] = optimizedList
        return optimizedList
    }

    fun optimizeBreakInAlgorithmsAfter(target: String, type: PieceType): List<Algorithm> {
        return this.optimizeBreakInTargetsAfter(target, type)
                .flatMap { this.source.getAlgorithms(type, target + it) }
    }
}