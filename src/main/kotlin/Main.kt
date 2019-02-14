import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle
import com.suushiemaniac.cubing.bld.model.puzzle.WCAPuzzle

fun main() {
    randAnalysis(WCAPuzzle.FOUR_BLD)
    //gAnalysis(WCAPuzzle.THREE_BLD, "D' L2 U B2 F2 R2 B2 U2 B2 R2 F' U F' L R F' U R' B' R2 U' Rw'")
}

fun gAnalysis(puzzle: TwistyPuzzle, scramble: Algorithm) {
    val testCube = puzzle.gPuzzle("gregor")

    val analysis = testCube.getAnalysis(scramble)

    println(scramble.toString())
    println(analysis.getSolutionPairs(true))
}

fun gAnalysis(puzzle: TwistyPuzzle, scramble: String) = gAnalysis(puzzle, puzzle.reader.parse(scramble))
fun randAnalysis(puzzle: TwistyPuzzle) = gAnalysis(puzzle, puzzle.randomScramble)
