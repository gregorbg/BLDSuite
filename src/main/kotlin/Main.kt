import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import puzzle.*

import java.io.File

fun main() {
    //val analysis = CubicPuzzle.THREE_BLD.scrambleAnalysis
    //analysis.setBuffer(CubicPieceType.EDGE, 2)

    //println(analysis.scramble)
    //println(analysis.getSolutionPairs(true))

    val defFile = File("/home/suushie_maniac/jvdocs/BLDSuite/src/main/resources/kpuzzle/555.def")
    val bldFile = File("/home/suushie_maniac/jvdocs/BLDSuite/src/main/resources/gpuzzle/gregor/555.bld")

    val reader = CubicAlgorithmReader()
    val scrambler = NoInspectionFiveByFiveCubePuzzle()

    val testCube = GPuzzle(reader, defFile, bldFile)

    val scr = reader.parse(scrambler.generateScramble())
    val analysis = testCube.getAnalysis(scr)

    println(scr.toFormatString())
    println(analysis.getSolutionPairs(true))
}