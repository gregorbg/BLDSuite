import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import java.io.File

fun main() {
    //val analysis = CubicPuzzle.THREE_BLD.scrambleAnalysis
    //analysis.setBuffer(CubicPieceType.EDGE, 2)

    //println(analysis.scramble)
    //println(analysis.getSolutionPairs(true))

    val defFile = File("/home/suushie_maniac/jvdocs/BLDSuite/src/main/resources/permutations/333.def")
    val dummy = KPuzzle(defFile)

    println("yay")
}