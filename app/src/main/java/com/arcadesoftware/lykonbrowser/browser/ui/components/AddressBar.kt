package com.arcadesoftware.lykonbrowser.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddressBar(
    url: String,
    backgroundColor: Color,
    textColor: Color,
    iconColor: Color,
    shape: Shape,
    height: Dp,
    horizontalPadding: Dp,
    iconSize: Dp,
    shieldBackgroundAlpha: Float,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onUrlClick: () -> Unit,
    onShieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = horizontalPadding)
            .background(backgroundColor, shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick, enabled = canGoBack) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = if (canGoBack) iconColor else iconColor.copy(alpha = 0.5f)
            )
        }
        IconButton(onClick = onForwardClick, enabled = canGoForward) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Forward",
                tint = if (canGoForward) iconColor else iconColor.copy(alpha = 0.5f)
            )
        }
        Text(
            text = url,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onUrlClick)
                .padding(horizontal = 8.dp),
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp
        )
        IconButton(onClick = onShieldClick) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(iconColor.copy(alpha = shieldBackgroundAlpha), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Warning, contentDescription = "Shield", tint = iconColor, modifier = Modifier.size(iconSize * 0.66f))
            }
        }
    }
}
