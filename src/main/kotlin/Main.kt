import com.suushiemaniac.cubing.bld.model.puzzle.WCAPuzzle

fun main() {
    //val analysis = CubicPuzzle.THREE_BLD.scrambleAnalysis
    //analysis.setBuffer(CubicPieceType.EDGE, 2)

    //println(analysis.scramble)
    //println(analysis.getSolutionPairs(true))

    val puzzle = WCAPuzzle.THREE_BLD
    val testCube = puzzle.gPuzzle("gregor")

    val scr = puzzle.randomScramble
    val analysis = testCube.getAnalysis(scr)

    println(scr.toFormatString())
    println(analysis.getSolutionPairs(true))
}