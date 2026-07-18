package com.arcadesoftware.lykonbrowser.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomToolbar(
    backgroundColor: Color,
    iconColor: Color,
    height: Dp,
    tabCount: Int,
    canGoBack: Boolean = false,
    canGoForward: Boolean = false,
    onBackClick: () -> Unit = {},
    onForwardClick: () -> Unit = {},
    onHomeClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onTabsClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back navigation
        IconButton(onClick = onBackClick, enabled = canGoBack) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = if (canGoBack) iconColor else iconColor.copy(alpha = 0.35f)
            )
        }
        // Forward navigation
        IconButton(onClick = onForwardClick, enabled = canGoForward) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Forward",
                tint = if (canGoForward) iconColor else iconColor.copy(alpha = 0.35f)
            )
        }
        IconButton(onClick = onHomeClick) {
            Icon(Icons.Outlined.Home, contentDescription = "Home", tint = iconColor)
        }
        IconButton(onClick = onTabsClick) {
            TabCounterBadge(
                count = tabCount,
                color = iconColor,
                size = 24.dp,
                borderWidth = 2.dp,
                cornerRadius = 6.dp,
                fontSize = 12.sp
            )
        }
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = iconColor)
        }
    }
}
