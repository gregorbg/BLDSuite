import com.suushiemaniac.cubing.bld.model.puzzle.TwistyPuzzle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val dummyChannel = Channel<Int>(Channel.UNLIMITED)

    repeat(5) {
        GlobalScope.launch {
            while (true) {
                delay(1000)
                dummyChannel.send(Random.nextInt(0, 25) + 1)
            }
        }
    }

    val runtime = measureTimeMillis {
        runBlocking {
            dummyChannel.filter { it % 5 == 0 }.take(5).toList()
        }
    }

    println(runtime / 1000f)

}

fun randAnalysis(puzzle: TwistyPuzzle) {
    val testCube = puzzle.gPuzzle("gregor")

    val scr = puzzle.randomScramble
    val analysis = testCube.getAnalysis(scr)

    println(scr.toFormatString())
    println(analysis.getSolutionPairs(true))
}