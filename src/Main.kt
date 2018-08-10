import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle

fun main(args: Array<String>) {
    val test = CubicPuzzle.THREE_BLD.scrambleAnalysis

    println(test.scramble)
    println(test.getSolutionPairs())
}