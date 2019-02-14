package com.suushiemaniac.cubing.bld.optim

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.model.cycle.ThreeCycle
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.AlgSource
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.util.CollectionUtil.countingList
import com.suushiemaniac.cubing.bld.util.CollectionUtil.toEach

class BreakInOptimizer(val source: AlgSource, val reader: NotationReader) {
    fun optimizeBreakInTargetsAfter(target: Int, buffer: Int, type: PieceType): List<Int> {
        return this.optimizeBreakInChoicesAfter(target, buffer, type).map { it.first.getAllTargets().first() }
    }

    fun optimizeBreakInAlgorithmsAfter(target: Int, buffer: Int, type: PieceType): List<Algorithm> {
        return this.optimizeBreakInChoicesAfter(target, buffer, type).map(Pair<PieceCycle, Algorithm>::second)
    }

    fun optimizeBreakInChoicesAfter(target: Int, buffer: Int, type: PieceType): List<Pair<PieceCycle, Algorithm>> {
        return type.numTargets.countingList().map { ThreeCycle(buffer, target, it) }.flatMap {
            it toEach this.source.getAlgorithms(type, this.reader, it)
        }.sortedByDescending { AlgComparator.scoreAlg(it.second) }
    }
}