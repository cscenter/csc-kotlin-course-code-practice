package cub

import cub.jvm.BacktrackingSudokuSolver
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * Runs Check JVM warmup task from idea configurations
 * to see the difference between first iterations and the next ones
 * To see the difference, just check Fork: N of 3 sections, e.g.
 *
    # Run progress: 50.00% complete, ETA 00:00:00
    # Fork: 1 of 3
    # Warmup Iteration   1: 27579584.000 ns/op
    # Warmup Iteration   2: 1333.000 ns/op
    # Warmup Iteration   3: 667.000 ns/op
    Iteration   1: 625.000 ns/op
    Iteration   2: 833.000 ns/op
    Iteration   3: 917.000 ns/op
    Iteration   4: 625.000 ns/op
    Iteration   5: 875.000 ns/op
    Iteration   6: 667.000 ns/op
    Iteration   7: 708.000 ns/op
    Iteration   8: 625.000 ns/op
    Iteration   9: 1584.000 ns/op
    Iteration  10: 4292.000 ns/op
 *
 * Here we can see that after several Warmup iterations time is table .
 * We see different time between attempts, it is ok since the processor spents time for time measurement operations,
 * it affects to the results, but the time here is in nano secs, the difference is not significant.
**/
open class BenchmarkRunner {
    companion object {
        private const val BENCHMARK_ITERATIONS = 10
        private const val BENCHMARK_FORKS = 3
        private const val BENCHMARK_WARMUPS = 3
    }
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Measurement(iterations = BENCHMARK_ITERATIONS)
    @Warmup(iterations = BENCHMARK_WARMUPS)
    @Fork(value = BENCHMARK_FORKS, warmups = BENCHMARK_WARMUPS)
    fun solveSudoku() {
        val solver = BacktrackingSudokuSolver()
        solver.solve()
    }
}

