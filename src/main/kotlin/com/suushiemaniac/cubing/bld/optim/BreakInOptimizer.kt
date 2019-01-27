package com.suushiemaniac.cubing.bld.optim

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.model.cycle.ThreeCycle
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.AlgSource

class BreakInOptimizer(val source: AlgSource, val reader: NotationReader, vararg pieceType: PieceType, fullCache: Boolean = true) {
    val cache: MutableMap<PieceType, MutableMap<Int, List<Int>>> = mutableMapOf()

    init {
        for (type in pieceType) {
            val typeMap = mutableMapOf<Int, List<Int>>()

            if (fullCache) {
                for (target in 0 until type.numTargets) {
                    typeMap[target] = this.optimizeBreakInTargetsAfter(target, type)
                }
            }

            this.cache[type] = typeMap
        }
    }

    fun optimizeBreakInTargetsAfter(target: Int, type: PieceType): List<Int> { // TODO this has to return actual targets
        return this.cache.getOrPut(type) { mutableMapOf() }.getOrPut(target) {
            val algList = mutableListOf<Algorithm>()
            val targetMap = mutableMapOf<Algorithm, Int>()

            for (t in 0 until type.numTargets) { // FIXME improve int iteration
                val case = ThreeCycle(0, target, t) // FIXME buffer
                val sourceList = this.source.getAlgorithms(type, this.reader, case)

                for (alg in sourceList) {
                    algList += alg
                    targetMap[alg] = t
                }
            }

            algList.sortedWith(AlgComparator.SINGLETON)
                    .map { targetMap.getValue(it) }
        }
    }

    fun optimizeBreakInAlgorithmsAfter(target: Int, type: PieceType): List<Algorithm> {
        return this.optimizeBreakInTargetsAfter(target, type)
                .flatMap { this.source.getAlgorithms(type, this.reader, ThreeCycle(0, target, it)) } // FIXME buffer
    }
}