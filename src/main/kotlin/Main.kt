import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import puzzle.*

import java.io.File
import kotlin.system.exitProcess

fun main() {
    //val analysis = CubicPuzzle.THREE_BLD.scrambleAnalysis
    //analysis.setBuffer(CubicPieceType.EDGE, 2)

    //println(analysis.scramble)
    //println(analysis.getSolutionPairs(true))

    val defFile = File("/home/suushie_maniac/jvdocs/BLDSuite/src/main/resources/kpuzzle/777.def")
    val bldFile = File("/home/suushie_maniac/jvdocs/BLDSuite/src/main/resources/gpuzzle/gregor/777.bld")

    val reader = CubicAlgorithmReader()
    val scrambler = CubePuzzle(7)

    val testCube = GPuzzle(reader, defFile, bldFile)

    val scr = reader.parse(scrambler.generateScramble())
    val analysis = testCube.getAnalysis(scr)

    println(scr.toFormatString())
    println(analysis.getSolutionPairs(true))
}