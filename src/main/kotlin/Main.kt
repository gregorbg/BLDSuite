import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.puzzle.WCAPuzzle

fun main() {
    //val analysis = CubicPuzzle.THREE_BLD.scrambleAnalysis
    //analysis.setBuffer(CubicPieceType.EDGE, 2)

    //println(analysis.scramble)
    //println(analysis.getSolutionPairs(true))

    val puzzle = WCAPuzzle.THREE_BLD

    val bldFile = GPuzzle.preInstalledConfig(puzzle.kConfigTag, "gregor")
    val testCube = puzzle.gPuzzle(bldFile)

    val scr = puzzle.randomScramble
    val analysis = testCube.getAnalysis(scr)

    println(scr.toFormatString())
    println(analysis.getSolutionPairs(true))
}