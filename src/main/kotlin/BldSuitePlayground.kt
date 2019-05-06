import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.model.puzzle.WCAPuzzle

object BldSuitePlayground {
    @JvmStatic
    fun main(args: Array<String>) {
        randAnalysis(WCAPuzzle.THREE_BLD)
    }

    fun gAnalysis(puzzle: TwistyPuzzle, scramble: Algorithm) {
        val testCube = puzzle.gPuzzle("2012BILL01")

        val analysis = testCube.getAnalysis(scramble)

        println(scramble.toString())
        println(analysis.getSolutionPairs(true))
        println(analysis.getStatString())
    }

    fun gAnalysis(puzzle: TwistyPuzzle, scramble: String) = gAnalysis(puzzle, puzzle.antlrReader.parse(scramble))
    fun randAnalysis(puzzle: TwistyPuzzle) = gAnalysis(puzzle, puzzle.randomScramble)
}
