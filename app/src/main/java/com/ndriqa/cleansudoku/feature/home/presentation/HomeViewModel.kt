package com.ndriqa.cleansudoku.feature.home.presentation

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndriqa.cleansudoku.core.data.SudokuBoard
import com.ndriqa.cleansudoku.core.domain.preferences.DataStoreManager
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.core.util.sudoku.generateSudoku
import com.ndriqa.cleansudoku.ui.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _sudoku : MutableStateFlow<UiState<SudokuBoard>> = MutableStateFlow(UiState.Idle)
    val sudoku = _sudoku.asStateFlow()

    val preferredDifficulty = dataStoreManager.preferredDifficulty
        .stateIn(viewModelScope, SharingStarted.Lazily, Level.EASY)

    fun tryGenerateSudoku() {
        _sudoku.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val sudokuBoard = generateSudoku()
            val endTime = System.currentTimeMillis()
            withContext(Dispatchers.Main) {
                _sudoku.value = UiState.Success(SudokuBoard(sudokuBoard))
            }
        }
    }

    fun resetGeneratedSudoku() {
        _sudoku.value = UiState.Idle
    }
}