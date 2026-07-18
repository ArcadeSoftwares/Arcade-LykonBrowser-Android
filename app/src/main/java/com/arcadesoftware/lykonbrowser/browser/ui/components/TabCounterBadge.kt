package com.arcadesoftware.lykonbrowser.browser.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun TabCounterBadge(
    count: Int,
    color: Color,
    size: Dp,
    borderWidth: Dp,
    cornerRadius: Dp,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .border(borderWidth, color, RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = color,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
