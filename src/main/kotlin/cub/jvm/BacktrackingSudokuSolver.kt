package cub.jvm

// Just a simple class with some computations
class BacktrackingSudokuSolver {

    fun printBoard() {
        board.forEach { row ->
            row.forEach { print("$it ") }
            println()
        }
    }

    fun solve(): Boolean {
        board.forEachIndexed { i, row ->
            row.forEachIndexed { j, column ->
                if (board[i][j] == NO_VALUE) {
                    for (k in MIN_VALUE..MAX_VALUE) {
                        board[i][j] = k
                        if (isValid(i, j) && solve()) {
                            return true
                        }
                        board[i][j] = NO_VALUE
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(row: Int, column: Int): Boolean {
        return rowConstraint(row) &&
                columnConstraint(column) &&
                subsectionConstraint(row, column)
    }

    private fun subsectionConstraint(row: Int, column: Int): Boolean {
        val constraint = BooleanArray(BOARD_SIZE)
        val subsectionRowStart = row / SUBSECTION_SIZE * SUBSECTION_SIZE
        val subsectionRowEnd = subsectionRowStart + SUBSECTION_SIZE
        val subsectionColumnStart = column / SUBSECTION_SIZE * SUBSECTION_SIZE
        val subsectionColumnEnd = subsectionColumnStart + SUBSECTION_SIZE
        for (r in subsectionRowStart until subsectionRowEnd) {
            for (c in subsectionColumnStart until subsectionColumnEnd) {
                if (!checkConstraint(r, constraint, c)) return false
            }
        }
        return true
    }

    private fun columnConstraint(column: Int): Boolean {
        val constraint = BooleanArray(BOARD_SIZE)
        return (BOARD_START_INDEX until BOARD_SIZE).all { checkConstraint(it, constraint, column) }
    }

    private fun rowConstraint(row: Int): Boolean {
        val constraint = BooleanArray(BOARD_SIZE)
        return (BOARD_START_INDEX until BOARD_SIZE).all { checkConstraint(row, constraint, it) }
    }

    private fun checkConstraint(row: Int, constraint: BooleanArray, column: Int): Boolean {
        if (board[row][column] != NO_VALUE) {
            if (!constraint[board[row][column] - 1]) {
                constraint[board[row][column] - 1] = true
            } else {
                return false
            }
        }
        return true
    }

    companion object {
        private const val BOARD_SIZE = 9
        private const val SUBSECTION_SIZE = 3
        private const val BOARD_START_INDEX = 0
        private const val NO_VALUE = 0
        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 9
        private val board = arrayOf(
            intArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 3, 6, 0, 0, 0, 0, 0),
            intArrayOf(0, 7, 0, 0, 9, 0, 2, 0, 0),
            intArrayOf(0, 5, 0, 0, 0, 7, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 4, 5, 7, 0, 0),
            intArrayOf(0, 0, 0, 1, 0, 0, 0, 3, 0),
            intArrayOf(0, 0, 1, 0, 0, 0, 0, 6, 8),
            intArrayOf(0, 0, 8, 5, 0, 0, 0, 1, 0),
            intArrayOf(0, 9, 0, 0, 0, 0, 4, 0, 0)
        )
    }
}
