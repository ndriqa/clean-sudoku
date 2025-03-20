package com.ndriqa.cleansudoku.feature.sudoku.presentation

import android.os.SystemClock
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class SudokuViewModel @Inject constructor(): ViewModel() {
    private val _userBoard = mutableStateListOf<MutableList<MutableState<SudokuBoardItem>>>()
    val userBoard: List<List<MutableState<SudokuBoardItem>>> get() = _userBoard

    private val _elapsedTime = MutableStateFlow(0L) // time in milliseconds
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

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
            _userBoard[row][col].value = cell.copy(
                number = if (number != cell.number) number else null
            )
        }
    }

    fun cell(row: Int, col: Int) = _userBoard[row][col].value

    fun onControlNumberClicked(numberClicked: Int?) {
        val (row, col) = selectedCell.value ?: return
        updateCell(row, col, numberClicked)
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

    companion object {
        private const val TIMER_UPDATE_INTERVAL = 16L // update roughly every frame (60fps)
        private const val TIMER_INITIAL_VALUE = 0L
    }
}