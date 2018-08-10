package com.suushiemaniac.cubing.bld.optim

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import com.suushiemaniac.cubing.bld.model.cycle.ThreeCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import com.suushiemaniac.cubing.bld.model.source.AlgSource

class BreakInOptim(val source: AlgSource, val refCube: BldPuzzle = CubicPuzzle.FIVE.analyzingPuzzle, fullCache: Boolean = true) {
    val cache: MutableMap<PieceType, MutableMap<Int, List<Int>>> = mutableMapOf()

    init {
        for (type in refCube.getPieceTypes()) {
            val typeMap = mutableMapOf<Int, List<Int>>()

            if (fullCache) {
                for (target in refCube.getLetteringScheme(type).indices) {
                    typeMap[target] = this.optimizeBreakInTargetsAfter(target, type)
                }
            }

            this.cache[type] = typeMap
        }
    }

    fun optimizeBreakInTargetsAfter(target: Int, type: PieceType): List<Int> {
        return this.cache.getOrPut(type) { mutableMapOf() }.getOrPut(target) {
            val algList = mutableListOf<Algorithm>()
            val targetMap = mutableMapOf<Algorithm, Int>()

            for (t in this.refCube.getLetteringScheme(type).indices) { // FIXME improve int iteration
                val case = ThreeCycle(0, target, t) // FIXME buffer
                val sourceList = this.source.getAlgorithms(type, case)

                for (alg in sourceList) {
                    algList += alg
                    targetMap[alg] = t
                }
            }

            return algList.sortedWith(AlgComparator.SINGLETON)
                    .map { targetMap.getValue(it) }
        }
    }

    fun optimizeBreakInAlgorithmsAfter(target: Int, type: PieceType): List<Algorithm> {
        return this.optimizeBreakInTargetsAfter(target, type)
                .flatMap { this.source.getAlgorithms(type, ThreeCycle(0, target, it)) } // FIXME buffer
    }
}