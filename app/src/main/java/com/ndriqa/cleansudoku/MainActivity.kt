package com.ndriqa.cleansudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ndriqa.cleansudoku.navigation.AppNavigation
import com.ndriqa.cleansudoku.ui.theme.AppColor
import com.ndriqa.cleansudoku.ui.theme.CleanSudokuTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CleanSudokuTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = AppColor
                ) { innerPadding ->
                    AppUi(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppUi(modifier: Modifier) {
    AppNavigation(modifier)
}

@Preview(showBackground = true)
@Composable
fun AppUiPreview() {
    CleanSudokuTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppUi(modifier = Modifier.padding(innerPadding))
        }
    }
}

