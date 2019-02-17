import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.model.puzzle.WCAPuzzle

fun main() {
    randAnalysis(WCAPuzzle.THREE_BLD)
}

fun gAnalysis(puzzle: TwistyPuzzle, scramble: Algorithm) {
    val testCube = puzzle.gPuzzle("gregor")

    val analysis = testCube.getAnalysis(scramble)

    println(scramble.toString())
    println(analysis.getSolutionPairs(true))
    println(analysis.getStatString())
}

fun gAnalysis(puzzle: TwistyPuzzle, scramble: String) = gAnalysis(puzzle, puzzle.reader.parse(scramble))
fun randAnalysis(puzzle: TwistyPuzzle) = gAnalysis(puzzle, puzzle.randomScramble)
