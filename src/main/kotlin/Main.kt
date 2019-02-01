import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.model.puzzle.WCAPuzzle

fun main() {
    randAnalysis(WCAPuzzle.THREE_BLD)
}

fun randAnalysis(puzzle: TwistyPuzzle) {
    val testCube = puzzle.gPuzzle("gregor")

    val scr = puzzle.randomScramble
    val analysis = testCube.getAnalysis(scr)

    println(scr.toString())
    println(analysis.getSolutionPairs(true))
}