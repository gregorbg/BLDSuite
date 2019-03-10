package com.suushiemaniac.cubing.bld.model.statistics

import com.suushiemaniac.cubing.bld.analyze.BldAnalysis
import com.suushiemaniac.cubing.bld.model.PieceType

enum class IntegerStat(override val symbol: Char) : ScrambleStatistic<Int> {
    YAY('c');

    override fun minFor(type: PieceType): Int = 0
    override fun maxFor(type: PieceType): Int = 1 // TODO

    override fun compute(type: PieceType, analysis: BldAnalysis): Int = 0 // TODO
}