package com.example.utkarsh.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(4000L) // 4 seconds to see the puzzle
        onTimeout()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFEEEEEE)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val gridItems = remember {
                val items = mutableListOf<GridItemData>()
                val utkarsh = "UTKARSH"
                val utkarshRow = 0
                val utkarshStartCol = 0

                // 1. Add UTKARSH (Horizontal)
                utkarsh.forEachIndexed { index, char ->
                    items.add(GridItemData(char, utkarshRow, utkarshStartCol + index, true))
                }

                // 2. Add Vertical Intersecting Words
                addVerticalWord(items, "TRUST", utkarshStartCol, 2, utkarshRow)
                addVerticalWord(items, "GRATITUDE", utkarshStartCol + 1, 3, utkarshRow)
                addVerticalWord(items, "KINDNESS", utkarshStartCol + 2, 0, utkarshRow)
                addVerticalWord(items, "CALM", utkarshStartCol + 3, 1, utkarshRow)
                addVerticalWord(items, "CARE", utkarshStartCol + 4, 2, utkarshRow)
                addVerticalWord(items, "BLISS", utkarshStartCol + 5, 3, utkarshRow)
                addVerticalWord(items, "HOPE", utkarshStartCol + 6, 0, utkarshRow)
                items
            }

            // Calculate actual bounds of the crossword
            val minRow = gridItems.minOf { it.row }
            val maxRow = gridItems.maxOf { it.row }
            val minCol = gridItems.minOf { it.col }
            val maxCol = gridItems.maxOf { it.col }
            
            val numRows = maxRow - minRow + 1
            val numCols = maxCol - minCol + 1

            // Calculate cell size based on screen constraints
            val cellWidth = maxWidth / numCols
            val cellHeight = maxHeight / numRows
            val cellSize = min(cellWidth.value, cellHeight.value).dp * 0.9f

            CrosswordGrid(gridItems, cellSize, minRow, minCol, numRows, numCols)
        }
    }
}

@Composable
fun CrosswordGrid(
    gridItems: List<GridItemData>,
    cellSize: Dp,
    minRow: Int,
    minCol: Int,
    numRows: Int,
    numCols: Int
) {
    // Correctly calculate total width and height using float multiplication for Dp
    val totalWidth = cellSize * numCols.toFloat()
    val totalHeight = cellSize * numRows.toFloat()

    Box(
        modifier = Modifier.size(width = totalWidth, height = totalHeight),
        contentAlignment = Alignment.TopStart // Elements are offset from the top-left of this box
    ) {
        for (item in gridItems) {
            // Offset calculation relative to the minRow and minCol
            val xOffset = cellSize * (item.col - minCol).toFloat()
            val yOffset = cellSize * (item.row - minRow).toFloat()
            
            Box(
                modifier = Modifier
                    .offset(x = xOffset, y = yOffset)
                    .size(cellSize - 2.dp)
                    .background(
                        if (item.isUtkarsh) MaterialTheme.colorScheme.primary else Color.White,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (item.isUtkarsh) Color.Transparent else Color.LightGray,
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.char.toString(),
                    fontSize = (cellSize.value * 0.6).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (item.isUtkarsh) Color.White else Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

data class GridItemData(val char: Char, val row: Int, val col: Int, val isUtkarsh: Boolean)

fun addVerticalWord(list: MutableList<GridItemData>, word: String, col: Int, intersectIndex: Int, intersectRow: Int) {
    val startRow = intersectRow - intersectIndex
    word.forEachIndexed { index, char ->
        val row = startRow + index
        if (!list.any { it.row == row && it.col == col }) {
            list.add(GridItemData(char, row, col, false))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onTimeout = {})
}
