import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle
import java.io.File

fun main() {
    //val analysis = CubicPuzzle.THREE_BLD.scrambleAnalysis
    //analysis.setBuffer(CubicPieceType.EDGE, 2)

    //println(analysis.scramble)
    //println(analysis.getSolutionPairs(true))

    val defFile = File("/home/suushie_maniac/jvdocs/BLDSuite/src/main/resources/permutations/333.def")
    val bldFile = File("/home/suushie_maniac/jvdocs/BLDSuite/src/main/resources/orientations/gregor/333.bld")

    val testCube = GPuzzle(defFile, bldFile)

    val scr = CubicPuzzle.THREE_BLD.randomScramble
    val analysis = testCube.getAnalysis(scr)

    println(scr.toFormatString())
    println(analysis.getSolutionPairs(true))
}