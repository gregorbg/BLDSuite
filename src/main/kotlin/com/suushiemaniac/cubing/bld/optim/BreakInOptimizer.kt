package com.suushiemaniac.cubing.bld.optim

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.AlgSource
import com.suushiemaniac.cubing.bld.util.CollectionUtil.countingList
import com.suushiemaniac.cubing.bld.util.StickerTarget
import com.suushiemaniac.cubing.bld.util.CollectionUtil.toEach

class BreakInOptimizer(val source: AlgSource, val reader: NotationReader) {
    fun optimizeBreakInTargetsAfter(target: Int, buffer: Int, type: PieceType): List<Int> {
        return this.optimizeBreakInChoicesAfter(target, buffer, type).map { it.first.target }
    }

    fun optimizeBreakInAlgorithmsAfter(target: Int, buffer: Int, type: PieceType): List<Algorithm> {
        return this.optimizeBreakInChoicesAfter(target, buffer, type).map { it.second }
    }

    fun optimizeBreakInChoicesAfter(target: Int, buffer: Int, type: PieceType): List<Pair<StickerTarget, Algorithm>> {
        val currentTarget = StickerTarget(target, buffer, true)

        return type.numTargets.countingList().map { StickerTarget(it, buffer) }.flatMap {
            it toEach this.source.getAlgorithms(type, this.reader, listOf(currentTarget, it))
        }.sortedByDescending { AlgComparator.score(it.second) }
    }
}