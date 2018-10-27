import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle

fun main(vararg args: String) {
    // val test = CubicPuzzle.THREE_BLD.getScrambleAnalysis(CubicPuzzle.THREE_BLD.reader.parse("D' R2 F2 U' F2 L2 D F2 R2 U2 B L' R' F R D L2 D2 U' R' F' Rw2"))

    // L' U2 R' U2 B2 L' F2 R' B2 F2 U2 F' U' B2 L2 D' R' B U' L2 F' Rw2 Uw2
    // R2 B2 L2 U' B2 U' L2 B2 D' F2 D L F D B D2 U' B' L' B L Uw

    // so halb falsch
    // D2 B U2 L2 F U2 F' U2 F L D' F2 D2 U R' D' B2 L' U Rw

    val analysis = CubicPuzzle.THREE_BLD.scrambleAnalysis
    analysis.setBuffer(CubicPieceType.EDGE, 2)

    println(analysis.scramble)
    println(analysis.getSolutionPairs(true))
}