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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
    onUrlSubmitted: (String) -> Unit,
    onShieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(url) { mutableStateOf(url) }

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
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            textStyle = TextStyle(color = textColor, fontSize = 14.sp),
            singleLine = true,
            cursorBrush = SolidColor(textColor),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onUrlSubmitted(text)
                }
            )
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
