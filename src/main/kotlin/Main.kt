import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import puzzle.NoInspectionFiveByFiveCubePuzzle
import puzzle.NoInspectionFourByFourCubePuzzle
import puzzle.NoInspectionThreeByThreeCubePuzzle

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

    val scr = reader.parse("B' Fw' R Rw' D F' Bw R2 U D' R2 Lw2 Rw D' R2 D2 Uw F2 Uw2 D Rw R' U' L Lw F Fw' D' Rw Lw2 Uw' F2 Dw F Dw' B2 F' D2 Rw Fw2 F R' D2 R2 U Bw2 R' U Dw D' Bw2 L Lw2 B2 L Lw Uw Lw' B2 D2 3Rw")
    val analysis = testCube.getAnalysis(scr)

    println(scr.toFormatString())
    println(analysis.getSolutionPairs(true))
}