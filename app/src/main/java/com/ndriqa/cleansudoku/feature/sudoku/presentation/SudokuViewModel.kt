package com.ndriqa.cleansudoku.feature.sudoku.presentation

import android.content.Context
import android.os.SystemClock
import android.os.Vibrator
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ndriqa.cleansudoku.core.data.SudokuBoard
import com.ndriqa.cleansudoku.core.data.SudokuBoardItem
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.core.util.sudoku.isSolvable
import com.ndriqa.cleansudoku.core.util.sudoku.isSudokuSolved
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import com.ndriqa.cleansudoku.core.data.MoveDirection
import com.ndriqa.cleansudoku.core.util.extensions.bzz
import com.ndriqa.cleansudoku.core.util.extensions.getVibrator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class SudokuViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {
    private val _userBoard = mutableStateListOf<MutableList<MutableState<SudokuBoardItem>>>()
    val userBoard: List<List<MutableState<SudokuBoardItem>>> get() = _userBoard

    private val _elapsedTime = MutableStateFlow(0L) // time in milliseconds
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    private val _areCandidatesEnabled = MutableStateFlow(false)
    val areCandidatesEnabled = _areCandidatesEnabled.asStateFlow()

    private var startTime: Long = 0L
    private var timerJob: Job? = null

    val isSolved by derivedStateOf {
        if (_userBoard.isEmpty()) return@derivedStateOf false

        val boardArray = _userBoard.map { row ->
            row.map { it.value.number ?: 0 }.toIntArray()
        }.toTypedArray()

        isSudokuSolved(boardArray)
    }

    val usedUpNumbers by derivedStateOf {
        val numberCount = mutableMapOf<Int, Int>()

        _userBoard.forEach { row ->
            row.forEach { cell ->
                val number = cell.value.number
                if (number != null) {
                    numberCount[number] = numberCount.getOrDefault(number, 0) + 1
                }
            }
        }

        numberCount.filterValues { it >= 9 }.keys.toList()
    }

    private val _selectedCell = mutableStateOf<Pair<Int, Int>?>(null)
    val selectedCell: MutableState<Pair<Int, Int>?> get() = _selectedCell

    private val vibrator: Vibrator? = context.getVibrator()

    fun initializeBoard(sudokuBoard: SudokuBoard, selectedLevel: Level) {
        if (_userBoard.isNotEmpty()) return // prevent re-initialization

        val initBoard = sudokuBoard.board.map { it.copyOf() }.toTypedArray()
        val cellsToRemove = mutableSetOf<Pair<Int, Int>>()
        val totalCells = initBoard.size * initBoard[0].size
        val maxRemovable = minOf(selectedLevel.digitsToRemove, totalCells)

        while (cellsToRemove.size < maxRemovable) {
            val randomRowIndex = (0 until initBoard.size).random()
            val randomColumnIndex = (0 until initBoard[randomRowIndex].size).random()

            if (initBoard[randomRowIndex][randomColumnIndex] != 0) {
                val backupValue = initBoard[randomRowIndex][randomColumnIndex]
                initBoard[randomRowIndex][randomColumnIndex] = 0

                if (!isSolvable(initBoard)) {// || countSolutions(initBoard) != 1) {
                    // if board is unsolvable or has multiple solutions, revert the change
                    initBoard[randomRowIndex][randomColumnIndex] = backupValue
                } else {
                    cellsToRemove.add(randomRowIndex to randomColumnIndex)
                }
            }
        }

        val modifiedBoard = initBoard.map { row ->
            row.map { num ->
                val isInitial = num != 0
                mutableStateOf(SudokuBoardItem(
                    number = if (isInitial) num else null,
                    isInitial = isInitial
                ))
            }.toMutableList()
        }

        _userBoard.apply {
            clear()
            addAll(modifiedBoard)
        }
    }

    fun onCellClick(row: Int, col: Int) {
        if (row !in 0 until _userBoard.size) return
        if (col !in 0 until _userBoard[0].size) return

        val newSelectedCell = row to col
        _selectedCell.value?.let {
            if (it sameAs newSelectedCell) {
                _selectedCell.value = null
                return
            }
        }
        _selectedCell.value = newSelectedCell
    }

    fun updateCell(row: Int, col: Int, number: Int?) {
        val cell = cell(row, col)

        if (!cell.isInitial) {
            vibrator?.bzz()

            _userBoard[row][col].value = cell.copy(
                number = if (number != cell.number) number else null,
                candidates = cell.candidates.filterNot { it == number }
            )

            // remove number from row candidates
            for (index in 0 until _userBoard[row].size) {
                removeCellCandidate(row, index, number)
            }

            // remove number from col candidates
            for (index in 0 until _userBoard.size) {
                removeCellCandidate(index, col, number)
            }

            // remove number from 3x3 grid (row, col) is in
            // eg. 7 / 3 = 2 (because of Int), 2 * 3 = 6
            // so [6, 7, 8] are indices of the row, similar for col
            val gridStartRow = (row / 3) * 3
            val gridStartCol = (col / 3) * 3
            for (gridRow in gridStartRow until gridStartRow + 3) {
                for (gridCol in gridStartCol until gridStartCol + 3) {
                    removeCellCandidate(gridRow, gridCol, number)
                }
            }
        }
    }

    private fun removeCellCandidate(row: Int, col: Int, number: Int?) {
        val currentCell = cell(row, col)
        if (currentCell.isInitial && currentCell.candidates.contains(number)) return

        _userBoard[row][col].value = currentCell.copy(
            candidates = currentCell.candidates.filterNot { it == number }
        )
    }

    fun updateCellCandidate(row: Int, col: Int, number: Int?) {
        number ?: return
        val cell = cell(row, col)

        if (!cell.isInitial) {
            val containsNumber = cell.candidates.contains(number)
            val newDigits =
                if (containsNumber) cell.candidates.filterNot { it == number }
                else cell.candidates + number

            _userBoard[row][col].value = cell.copy(
                candidates = newDigits.sorted()
            )
        }
    }

    fun cell(row: Int, col: Int) = _userBoard[row][col].value

    fun onControlNumberClicked(numberClicked: Int?) {
        val (row, col) = selectedCell.value ?: return

        if (_areCandidatesEnabled.value) {
            updateCellCandidate(row, col, numberClicked)
        } else {
            updateCell(row, col, numberClicked)
        }
    }

    private infix fun Pair<Int, Int>.sameAs(other: Pair<Int, Int>): Boolean {
        return first == other.first && second == other.second
    }

    fun startTimer() {
        if (timerJob != null) return // prevent multiple timers

        startTime = SystemClock.elapsedRealtime() // store start time

        timerJob = viewModelScope.launch {
            while (isActive) {
                val currentTime = SystemClock.elapsedRealtime()
                _elapsedTime.value = currentTime - startTime // update elapsed time
                delay(TIMER_UPDATE_INTERVAL) // control update frequency
            }
        }
    }


    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        stopTimer()
        _elapsedTime.value = TIMER_INITIAL_VALUE
    }

    fun toggleCandidates() {
        _areCandidatesEnabled.update { _areCandidatesEnabled.value.not() }
    }

    fun moveSelectedCell(direction: MoveDirection) {
        val (row, col) = selectedCell.value ?: return

        // too lazy to figure it out why I should switch them
        val newRow = row + direction.offset.y
        val newCol = col + direction.offset.x
        onCellClick(newRow, newCol)
    }

    companion object {
        private const val TIMER_UPDATE_INTERVAL = 16L // update roughly every frame (60fps)
        private const val TIMER_INITIAL_VALUE = 0L
    }
}