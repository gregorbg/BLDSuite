package com.suushiemaniac.cubing.bld.model.statistics

import com.suushiemaniac.cubing.bld.analyze.BldAnalysis
import com.suushiemaniac.cubing.bld.model.PieceType

enum class BooleanStat(override val symbol: Char) : ScrambleStatistic<Boolean> {
    YAY('c');

    override fun minFor(type: PieceType): Boolean = false
    override fun maxFor(type: PieceType): Boolean = true

    override fun compute(type: PieceType, analysis: BldAnalysis): Boolean = false // TODO
}