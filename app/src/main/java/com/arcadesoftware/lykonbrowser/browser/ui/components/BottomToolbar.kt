package com.arcadesoftware.lykonbrowser.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcadesoftware.lykonbrowser.R

/**
 * Bottom navigation toolbar.
 * Layout: Home | Bookmark | New Tab (+) | All Tabs | More (3 dots)
 * All icons are XML drawables from res/drawable for easy customization.
 */
@Composable
fun BottomToolbar(
    backgroundColor: Color,
    iconColor: Color,
    height: Dp,
    tabCount: Int,
    onHomeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onNewTabClick: () -> Unit,
    onTabsClick: () -> Unit,
    onMoreClick: () -> Unit,
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
        // Home
        IconButton(onClick = onHomeClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        // Bookmark
        IconButton(onClick = onBookmarkClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bookmark),
                contentDescription = "Bookmarks",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        // New Tab (center, add icon)
        IconButton(onClick = onNewTabClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_new_tab),
                contentDescription = "New Tab",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        // All Tabs (with counter badge)
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
        // More / Settings (3 vertical dots)
        IconButton(onClick = onMoreClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "More options",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
