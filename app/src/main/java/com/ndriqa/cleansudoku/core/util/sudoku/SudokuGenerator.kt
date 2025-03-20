package com.ndriqa.cleansudoku.core.util.sudoku

private const val SUDOKU_SIZE = 9
private const val SUDOKU_SUBGRID_SIZE = 3

suspend fun generateSudoku(): Array<IntArray> {
    val grid = Array(9) { IntArray(9) }
    fillDiagonalBoxes(grid)
    fillRemainingCells(grid, 0, 3)
    return grid
}

private fun fillDiagonalBoxes(grid: Array<IntArray>) {
    for (i in 0 until 9 step 3) {
        fillBox(grid, i, i)
    }
}

private fun fillBox(grid: Array<IntArray>, row: Int, col: Int) {
    val numbers = (1..9).toList().shuffled()
    var index = 0
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            grid[row + i][col + j] = numbers[index++]
        }
    }
}

private suspend fun fillRemainingCells(grid: Array<IntArray>, row: Int, col: Int): Boolean {
    var currentRow = row
    var currentCol = col

    // Move to the next row if we've reached the end of the current row
    if (currentCol >= 9 && currentRow < 8) {
        currentRow++
        currentCol = 0
    }

    // If we've filled all cells, return true
    if (currentRow >= 9 && currentCol >= 9) {
        return true
    }

    // Skip cells in the diagonal boxes (they are already filled)
    if (currentRow < 3 && currentCol < 3) {
        currentCol = 3
    } else if (currentRow < 6 && currentCol == (currentRow / 3) * 3) {
        currentCol += 3
    } else if (currentRow >= 6 && currentCol == 6) {
        currentRow++
        currentCol = 0
        if (currentRow >= 9) {
            return true
        }
    }

    // Try numbers 1-9 in the current cell
    for (num in 1..9) {
        if (isSafe(grid, currentRow, currentCol, num)) {
            grid[currentRow][currentCol] = num
            if (fillRemainingCells(grid, currentRow, currentCol + 1)) {
                return true
            }
            grid[currentRow][currentCol] = 0 // Backtrack
        }
    }
    return false
}

private fun isSafe(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
    return !usedInRow(grid, row, num) &&
            !usedInCol(grid, col, num) &&
            !usedInBox(grid, row - row % 3, col - col % 3, num)
}

private fun usedInRow(grid: Array<IntArray>, row: Int, num: Int): Boolean {
    for (col in 0 until 9) {
        if (grid[row][col] == num) {
            return true
        }
    }
    return false
}

private fun usedInCol(grid: Array<IntArray>, col: Int, num: Int): Boolean {
    for (row in 0 until 9) {
        if (grid[row][col] == num) {
            return true
        }
    }
    return false
}

private fun usedInBox(grid: Array<IntArray>, boxStartRow: Int, boxStartCol: Int, num: Int): Boolean {
    for (row in 0 until 3) {
        for (col in 0 until 3) {
            if (grid[row + boxStartRow][col + boxStartCol] == num) {
                return true
            }
        }
    }
    return false
}

fun isSolvable(board: Array<IntArray>): Boolean {
    val tempBoard = board.map { it.copyOf() }.toTypedArray() // Copy to avoid modifying original
    return solveSudoku(tempBoard)
}

private fun solveSudoku(board: Array<IntArray>): Boolean {
    for (row in board.indices) {
        for (col in board[row].indices) {
            if (board[row][col] == 0) { // Find an empty cell
                for (num in 1..9) {
                    if (isValidMove(board, row, col, num)) {
                        board[row][col] = num
                        if (solveSudoku(board)) return true
                        board[row][col] = 0 // Backtrack
                    }
                }
                return false // No valid number found, backtrack
            }
        }
    }
    return true // Board is fully solved
}

private fun isValidMove(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
    // Check row & column
    for (i in 0 until 9) {
        if (board[row][i] == num || board[i][col] == num) return false
    }

    // Check 3x3 box
    val startRow = row / 3 * 3
    val startCol = col / 3 * 3
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            if (board[startRow + i][startCol + j] == num) return false
        }
    }
    return true
}

fun countSolutions(board: Array<IntArray>): Int {
    val tempBoard = board.map { it.copyOf() }.toTypedArray()
    var solutionCount = 0

    fun solve(): Boolean {
        for (row in board.indices) {
            for (col in board[row].indices) {
                if (tempBoard[row][col] == 0) {
                    for (num in 1..9) {
                        if (isValidMove(tempBoard, row, col, num)) {
                            tempBoard[row][col] = num
                            if (solve()) return true
                            tempBoard[row][col] = 0 // Backtrack
                        }
                    }
                    return false
                }
            }
        }
        solutionCount++
        return solutionCount > 1 // Stop if more than one solution is found
    }

    solve()
    return solutionCount
}

fun isValidSudoku(board: Array<IntArray>): Boolean {
    // Check rows and columns
    for (i in 0 until 9) {
        if (!isValidSet(board[i])) return false // Check row
        if (!isValidSet(board.map { it[i] }.toIntArray())) return false // Check column
    }

    // Check 3x3 subgrids
    for (row in 0 until 9 step 3) {
        for (col in 0 until 9 step 3) {
            if (!isValidBox(board, row, col)) return false
        }
    }
    return true
}

// Checks if an array contains numbers 1-9 exactly once
private fun isValidSet(numbers: IntArray): Boolean {
    return numbers.sorted() == (1..9).toList() // Must contain all digits from 1-9
}

// Checks if a 3x3 box is valid
private fun isValidBox(board: Array<IntArray>, startRow: Int, startCol: Int): Boolean {
    val numbers = mutableListOf<Int>()
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            numbers.add(board[startRow + i][startCol + j])
        }
    }
    return isValidSet(numbers.toIntArray())
}

fun isSudokuSolved(board: Array<IntArray>): Boolean {
    // Ensure all cells are filled (no zeros)
    if (board.any { row -> row.any { it == 0 } }) return false

    // Ensure Sudoku is valid (rows, cols, and boxes contain unique numbers)
    return isValidSudoku(board)
}
