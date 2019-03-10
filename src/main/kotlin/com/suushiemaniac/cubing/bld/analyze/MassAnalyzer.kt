package com.suushiemaniac.cubing.bld.analyze

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.model.statistics.ScrambleStatistic
import com.suushiemaniac.cubing.bld.util.CollectionUtil.asyncList

class MassAnalyzer(val puzzle: TwistyPuzzle, configTag: String) {
    val analyzer = this.puzzle.gPuzzle(configTag)

    fun <T> analyzeProperty(scrambles: List<Algorithm>, stat: ScrambleStatistic<T>) = analyzeScrambles(scrambles) { stat.compute(it, this) }

    fun analyzeStatStrings(scrambles: List<Algorithm>) = analyzeScrambles(scrambles) { this.getStatString(it) }
    fun analyzeFullStatStrings(scrambles: List<Algorithm>) = analyzeScrambles(scrambles) { this.getStatString() }

    fun analyzeLetterPairs(scrambles: List<Algorithm>, singleLetter: Boolean) = analyzeScramblesFlat(scrambles) {
        this.getSolutionTargets(it)
                .replace((if (singleLetter) "\\s+?" else "$.").toRegex(), "")
                .split((if (singleLetter) "" else "\\s+").toRegex())
                .dropLastWhile(String::isEmpty)
    }

    private fun <T> analyzeScrambles(scrambles: List<Algorithm>, analysisMapping: BldAnalysis.(PieceType) -> T): Map<PieceType, Map<T, Int>> = this.analyzer.pieceTypes.associateWith { pt ->
        scrambles.map { this.analyzer.getAnalysis(it) }.map { it.analysisMapping(pt) }.groupingBy { it }.eachCount()
    }

    private fun <T> analyzeScramblesFlat(scrambles: List<Algorithm>, analysisMapping: BldAnalysis.(PieceType) -> Iterable<T>): Map<PieceType, Map<T, Int>> = this.analyzer.pieceTypes.associateWith { pt ->
        scrambles.map { this.analyzer.getAnalysis(it) }.flatMap { it.analysisMapping(pt) }.groupingBy { it }.eachCount()
    }

    fun generateRandom(numCubes: Int): List<Algorithm> {
        return numCubes.asyncList { puzzle.randomScramble }
    }
}
