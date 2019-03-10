package com.suushiemaniac.cubing.bld.model.statistics

import com.suushiemaniac.cubing.bld.analyze.BldAnalysis
import com.suushiemaniac.cubing.bld.model.PieceType

interface ScrambleStatistic<T> {
    val symbol: Char

    fun minFor(type: PieceType): T
    fun maxFor(type: PieceType): T

    fun compute(type: PieceType, analysis: BldAnalysis): T
}