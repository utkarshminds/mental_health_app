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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
            // Further optimized grid size to make it even bigger
            // We'll use 8x12 grid bounds for maximum screen utilization
            val gridWidth = 8
            val gridHeight = 12
            
            val cellWidth = maxWidth / gridWidth
            val cellHeight = maxHeight / gridHeight
            val cellSize = min(cellWidth.value, cellHeight.value).dp

            CrosswordGrid(cellSize)
        }
    }
}

@Composable
fun CrosswordGrid(cellSize: androidx.compose.ui.unit.Dp) {
    val utkarsh = "UTKARSH"
    // Center UTKARSH at Row 5, Col 0 (adjusted for larger display)
    val utkarshRow = 5
    val utkarshStartCol = 0

    val gridItems = mutableListOf<GridItemData>()

    // 1. Add UTKARSH (Horizontal)
    utkarsh.forEachIndexed { index, char ->
        gridItems.add(GridItemData(char, utkarshRow, utkarshStartCol + index, true))
    }

    // 2. Add Vertical Intersecting Words
    addVerticalWord(gridItems, "TRUST", utkarshStartCol + 0, 2, utkarshRow)
    addVerticalWord(gridItems, "GRATITUDE", utkarshStartCol + 1, 3, utkarshRow)
    addVerticalWord(gridItems, "KINDNESS", utkarshStartCol + 2, 0, utkarshRow)
    addVerticalWord(gridItems, "CALM", utkarshStartCol + 3, 1, utkarshRow)
    addVerticalWord(gridItems, "CARE", utkarshStartCol + 4, 2, utkarshRow)
    addVerticalWord(gridItems, "BLISS", utkarshStartCol + 5, 3, utkarshRow)
    addVerticalWord(gridItems, "HOPE", utkarshStartCol + 6, 0, utkarshRow)

    // Center the grid within its own box
    Box(modifier = Modifier.size(cellSize * 8, cellSize * 12)) {
        for (item in gridItems) {
            val cellModifier = Modifier
                .offset(x = (item.col * cellSize.value).dp, y = (item.row * cellSize.value).dp)
                .size(cellSize - 1.dp) // Thinner gap for bigger appearance
                .background(
                    if (item.isUtkarsh) MaterialTheme.colorScheme.primary else Color.White,
                    shape = RoundedCornerShape(2.dp)
                )
                .border(
                    width = 0.5.dp, // Thinner border for a cleaner, larger look
                    color = if (item.isUtkarsh) Color.Transparent else Color.LightGray,
                    shape = RoundedCornerShape(2.dp)
                )

            Box(
                modifier = cellModifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.char.toString(),
                    fontSize = (cellSize.value * 0.6).sp, // Increased font size relative to cell
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
