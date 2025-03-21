package com.ndriqa.cleansudoku.feature.sudoku.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.NoteAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ndriqa.cleansudoku.R
import com.ndriqa.cleansudoku.core.data.SudokuBoard
import com.ndriqa.cleansudoku.core.data.SudokuBoardItem
import com.ndriqa.cleansudoku.core.util.extensions.asCandidateGrid
import com.ndriqa.cleansudoku.core.util.extensions.dashedBorder
import com.ndriqa.cleansudoku.core.util.extensions.toFormattedTime
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.ui.components.SplitScreen
import com.ndriqa.cleansudoku.ui.theme.PaddingCompact
import com.ndriqa.cleansudoku.ui.theme.PaddingDefault
import com.ndriqa.cleansudoku.ui.theme.PaddingNano
import com.ndriqa.cleansudoku.ui.theme.SpaceMonoFontFamily
import com.ndriqa.cleansudoku.ui.theme.TopBarSize

private const val SUBTEXT_SIZE = 10
private const val MAIN_TEXT_SIZE = 18
private const val CLEAR_BUTTON_LABEL = "C"

@Composable
fun SudokuScreen(
    navController: NavController,
    sudokuBoard: SudokuBoard,
    selectedLevel: Level,
    viewModel: SudokuViewModel = hiltViewModel()
) {
    val userBoard by remember { derivedStateOf { viewModel.userBoard } }
    val selectedCell by remember { derivedStateOf { viewModel.selectedCell } }
    val solved = viewModel.isSolved
    val usedUpNumbers = viewModel.usedUpNumbers
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val candidatesEnabled by viewModel.areCandidatesEnabled.collectAsState()

    LaunchedEffect(solved) {
        if (solved) {
            navController.navigateUp()
        }
    }

    LaunchedEffect(Unit) {
        with(viewModel) {
            initializeBoard(sudokuBoard, selectedLevel)
            startTimer()
        }
    }

    Scaffold(
        topBar = { TopBarUi(
            elapsedTime = elapsedTime,
            selectedLevel = selectedLevel,
            onBackPress = navController::navigateUp
        ) },
        containerColor = Color.Transparent
    ) { contentPadding ->
        SplitScreen(
            primaryContent = {
                SudokuBoardUI(
                    board = userBoard,
                    selectedCell = selectedCell.value,
                    onCellClick = viewModel::onCellClick,
                )
            },
            secondaryContent = {
                ControlsUi(
                    usedUpNumbers = usedUpNumbers,
                    areCandidatesEnabled = candidatesEnabled,
                    onNumberClick = viewModel::onControlNumberClicked,
                    onCandidatesToggle = viewModel::toggleCandidates
                )
            },
            primaryContentPadding = 0.dp,
            secondaryContentPadding = PaddingCompact,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
fun TopBarUi(
    elapsedTime: Long = 0L,
    selectedLevel: Level? = null,
    onBackPress: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TopBarSize),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackPress, modifier = Modifier.size(TopBarSize)) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.cd_back_button),
            )
        }
        FormattedTimerText(
            elapsedTime = elapsedTime,
            modifier = Modifier.weight(1F)
        )
        selectedLevel?.let { level ->
            Text(text = stringResource(level.titleResId))
            Spacer(modifier = Modifier.size(PaddingCompact))
        }
    }
}

@Composable
fun FormattedTimerText(
    elapsedTime: Long,
    modifier: Modifier = Modifier
) {
    val formattedTime = elapsedTime.toFormattedTime()
    val mainTime = formattedTime.substringBeforeLast(":")
    val milliseconds = formattedTime.substringAfterLast(":")

    Text(
        text = buildAnnotatedString {
            append(mainTime)
            withStyle(style = SpanStyle(fontSize = SUBTEXT_SIZE.sp, fontWeight = FontWeight.Light)) {
                append(" $milliseconds")
            }
        },
        fontSize = MAIN_TEXT_SIZE.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun ControlsUi(
    usedUpNumbers: List<Int>,
    areCandidatesEnabled: Boolean,
    onNumberClick: (Int?) -> Unit,
    onCandidatesToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingDefault, alignment = Alignment.CenterVertically)
    ) {
        SudokuNumbersUi(
            usedUpNumbers = usedUpNumbers,
            onNumberClick = onNumberClick
        )

        HelperNumbersUi(
            areCandidatesEnabled = areCandidatesEnabled,
            onCandidatesToggle = onCandidatesToggle
        )
    }
}

@Composable
fun HelperNumbersUi(
    areCandidatesEnabled: Boolean,
    onCandidatesToggle: () -> Unit
) {
    val buttonsShape = RoundedCornerShape(PaddingDefault)
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary
    val enabledContainer = if (areCandidatesEnabled) contentColor else containerColor
    val enabledContent = if (areCandidatesEnabled) containerColor else contentColor

    Row(
        modifier = Modifier
            .clip(buttonsShape)
            .border(
                width = 1.dp,
                shape = buttonsShape,
                color = contentColor
            )
    ) {

        Row(
            modifier = Modifier
                .background(enabledContainer)
                .clickable(onClick = onCandidatesToggle)
                .weight(1F)
                .padding(PaddingCompact),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PaddingCompact, alignment = Alignment.CenterHorizontally)
        ) {
            Icon(imageVector = Icons.Rounded.Create, contentDescription = null, tint = enabledContent)
            Text(text = stringResource(R.string.normal), color = enabledContent)
        }
        Row(
            modifier = Modifier
                .background(enabledContent)
                .clickable(onClick = onCandidatesToggle)
                .weight(1F)
                .padding(PaddingCompact),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PaddingCompact, alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.candidates), color = enabledContainer)
            Icon(imageVector = Icons.Rounded.NoteAlt, contentDescription = null, tint = enabledContainer)
        }
    }
}

@Composable
fun SudokuNumbersUi(
    usedUpNumbers: List<Int>,
    onNumberClick: (Int?) -> Unit
) {
    val topRow = (1..5).map { "$it" }
    val bottomRow = (6..9).map { "$it" } + CLEAR_BUTTON_LABEL

    fun String.isNotUsedUp() = this.toIntOrNull() !in usedUpNumbers

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingCompact)
        ) {
            topRow.forEach { label -> NumberButton(label, label.isNotUsedUp(), onNumberClick) }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PaddingCompact)
        ) {
            bottomRow.forEach { label -> NumberButton(label, label.isNotUsedUp(), onNumberClick) }
        }
    }
}

@Composable
fun RowScope.NumberButton(label: String, enabled: Boolean, onNumberClick: (Int?) -> Unit) {
    Button(
        onClick = { onNumberClick(label.toIntOrNull()) },
        modifier = Modifier.weight(1F),
        enabled = enabled
    ) {
        when(label) {
            CLEAR_BUTTON_LABEL -> Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(R.string.cd_clear_button)
            )
            else -> Text(text = label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SudokuBoardUI(
    board: List<List<MutableState<SudokuBoardItem>>>,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val boardSize = board.size
    val cellDefaultShape = RoundedCornerShape(PaddingNano)
    val cellModifier = Modifier
        .aspectRatio(1f)
        .clip(cellDefaultShape)
    val selectedBoardItem = selectedCell?.let { (x, y) -> board[x][y].value }
    val borderWidth = if (isLandscape) 1.dp else 2.dp
    val cellBackgroundColor = MaterialTheme.colorScheme.surface
    val cellBorderColor = MaterialTheme.colorScheme.primaryContainer

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val boardMaxSize = minOf(this.maxWidth, this.maxHeight)

        Column(
            modifier = Modifier
                .size(boardMaxSize)
                .clip(RoundedCornerShape(PaddingCompact)),
        ) {
            board.forEachIndexed { row, rowArray ->
                Row(modifier = Modifier.weight(1f),) {
                    rowArray.forEachIndexed { col, cellState ->
                        val cell = cellState.value
                        val isSelected = row == selectedCell?.first && col == selectedCell.second
                        val sameNumberSelected = cell.number != null && selectedBoardItem?.number == cell.number

                        val topStartRadius = if (row == 0 && col == 0) PaddingCompact else PaddingNano
                        val topEndRadius = if (row == 0 && col == boardSize - 1) PaddingCompact else PaddingNano
                        val bottomStartRadius = if (row == boardSize - 1 && col == 0) PaddingCompact else PaddingNano
                        val bottomEndRadius = if (row == boardSize - 1 && col == boardSize - 1) PaddingCompact else PaddingNano
                        val cellShape = RoundedCornerShape(
                            topStart = topStartRadius,
                            topEnd = topEndRadius,
                            bottomEnd = bottomEndRadius,
                            bottomStart = bottomStartRadius
                        )

                        val startBorder = if (col == 0 || col % 3 == 0) 2.dp else 1.dp
                        val topBorder = if (row == 0 || row % 3 == 0) 2.dp else 1.dp
                        val endBorder = if (col == boardSize - 1 || (col + 1) % 3 == 0) 2.dp else 1.dp
                        val bottomBorder = if (row == boardSize - 1 || (row + 1) % 3 == 0) 2.dp else 1.dp
                        val borderModifier = Modifier.drawBehind {
                            val stroke = Paint().apply {
                                color = cellBorderColor
                                style = PaintingStyle.Stroke
                                strokeWidth = 0f // default, we override it per side
                            }

                            val widthPx = size.width
                            val heightPx = size.height

                            // draw each side separately
                            drawLine(
                                color = stroke.color,
                                strokeWidth = startBorder.toPx(),
                                start = Offset(0f, 0f),
                                end = Offset(0f, heightPx)
                            )
                            drawLine(
                                color = stroke.color,
                                strokeWidth = topBorder.toPx(),
                                start = Offset(0f, 0f),
                                end = Offset(widthPx, 0f)
                            )
                            drawLine(
                                color = stroke.color,
                                strokeWidth = endBorder.toPx(),
                                start = Offset(widthPx, 0f),
                                end = Offset(widthPx, heightPx)
                            )
                            drawLine(
                                color = stroke.color,
                                strokeWidth = bottomBorder.toPx(),
                                start = Offset(0f, heightPx),
                                end = Offset(widthPx, heightPx)
                            )
                        }


                        Box(
                            modifier = cellModifier
                                .weight(1f)
                                .background(
                                    when {
                                        cell.isInitial -> cellBackgroundColor.copy(alpha = .7F)
                                        cell.number != null -> cellBackgroundColor.copy(alpha = .875F)
                                        else -> cellBackgroundColor
                                    }
                                )
                                .then(
                                    when {
                                        isSelected -> Modifier.border(
                                            width = borderWidth,
                                            color = MaterialTheme.colorScheme.secondary,
                                            shape = cellShape
                                        )

                                        sameNumberSelected -> Modifier.dashedBorder(
                                            borderWidth = borderWidth,
                                            color = MaterialTheme.colorScheme.secondary,
                                            shape = cellShape,
                                        )

                                        else -> borderModifier
                                    }
                                )
                                .clickable { onCellClick(row, col) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell.number != null) {
                                Text(
                                    text = cell.number.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = cell.candidates.asCandidateGrid(boardSize),
                                    lineHeight = 10.sp,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = SpaceMonoFontFamily,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}