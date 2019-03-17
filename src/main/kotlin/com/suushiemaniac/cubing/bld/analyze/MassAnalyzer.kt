package com.suushiemaniac.cubing.bld.analyze

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.util.CollectionUtil.asyncList

class MassAnalyzer(val puzzle: TwistyPuzzle, configTag: String) {
    val analyzer = this.puzzle.gPuzzle(configTag)

    fun analyzeStatStrings(scrambles: List<Algorithm>) = analyzeScrambles(scrambles) { this.getStatString(it) }
    fun analyzeFullStatStrings(scrambles: List<Algorithm>) = analyzeScramblesTotal(scrambles) { this.getStatString() }

    fun analyzeLetterPairs(scrambles: List<Algorithm>, singleLetter: Boolean) = analyzeScramblesFlat(scrambles) {
        this.compileSolutionTargetString(it)
                .replace((if (singleLetter) "\\s+?" else "$.").toRegex(), "")
                .split((if (singleLetter) "" else "\\s+").toRegex())
                .dropLastWhile(String::isEmpty)
    }

    fun <T> analyzeScrambles(scrambles: List<Algorithm>, analysisMapping: BldAnalysis.(PieceType) -> T) = this.analyzer.pieceTypes.associateWith { pt ->
        scrambles.map { this.analyzer.getAnalysis(it) }.map { it.analysisMapping(pt) }.groupingBy { it }.eachCount()
    }

    private fun <T> analyzeScramblesFlat(scrambles: List<Algorithm>, analysisMapping: BldAnalysis.(PieceType) -> Iterable<T>) = this.analyzer.pieceTypes.associateWith { pt ->
        scrambles.map { this.analyzer.getAnalysis(it) }.flatMap { it.analysisMapping(pt) }.groupingBy { it }.eachCount()
    }

    private fun <T> analyzeScramblesTotal(scrambles: List<Algorithm>, analysisMapping: BldAnalysis.() -> T) = scrambles.map { this.analyzer.getAnalysis(it) }.map { it.analysisMapping() }.groupingBy { it }.eachCount()

    fun generateRandom(numCubes: Int) = numCubes.asyncList { puzzle.randomScramble }
}
