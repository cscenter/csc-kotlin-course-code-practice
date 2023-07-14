package cub

import cub.jvm.BacktrackingSudokuSolver
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * Runs Check JVM warmup task from idea configurations
 * to see the difference between first iterations and the next ones
 * To see the difference, just check Fork: N of 3 sections, e.g.
 *
 * # Fork: 1 of 3
 * Iteration   1: 27041458.000 ns/op
 * Iteration   2: 1750.000 ns/op
 * Iteration   3: 1750.000 ns/op
 * Iteration   4: 625.000 ns/op
 * Iteration   5: 1125.000 ns/op
 * Iteration   6: 791.000 ns/op
 * Iteration   7: 958.000 ns/op
 * Iteration   8: 792.000 ns/op
 * Iteration   9: 750.000 ns/op
 * Iteration  10: 1084.000 ns/op
 *
 * Here we can see that the first iteration takes more time than all others.
 * We see different time between attempts, it is ok since the processor spents time for time measurement operations,
 * it affects to the results, but the time here is in nano secs, the difference is not significant.
**/
open class BenchmarkRunner {
    companion object {
        private const val BENCHMARK_ITERATIONS = 10
        private const val BENCHMARK_FORKS = 3
        private const val BENCHMARK_WARMUPS = 0
    }
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Measurement(iterations = BENCHMARK_ITERATIONS)
    @Fork(value = BENCHMARK_FORKS, warmups = BENCHMARK_WARMUPS)
    fun solveSudoku() {
        val solver = BacktrackingSudokuSolver()
        solver.solve()
    }
}
