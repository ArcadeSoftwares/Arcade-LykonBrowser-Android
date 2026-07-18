package com.arcadesoftware.lykonbrowser.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import com.arcadesoftware.lykonbrowser.R

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
    val focusManager = LocalFocusManager.current
    var textFieldValue by remember(url) { 
        mutableStateOf(TextFieldValue(text = if (url == "about:home") "" else url))
    }
    var isFocused by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = horizontalPadding)
            .background(backgroundColor, shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Tune/settings icon
        IconButton(onClick = onShieldClick) {
            Icon(
                Icons.Filled.Tune,
                contentDescription = "Site settings",
                tint = iconColor,
                modifier = Modifier.size(iconSize * 0.85f)
            )
        }
        // Center: URL text field
        BasicTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && !isFocused) {
                        textFieldValue = textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
                    }
                    isFocused = focusState.isFocused
                },
            textStyle = TextStyle(color = textColor, fontSize = 14.sp),
            singleLine = true,
            cursorBrush = SolidColor(textColor),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
                onGo = {
                    val text = textFieldValue.text.trim()
                    if (text.isNotEmpty()) {
                        onUrlSubmitted(text)
                        focusManager.clearFocus()
                    }
                }
            ),
            decorationBox = { innerTextField ->
                if (textFieldValue.text.isEmpty()) {
                    Text("Search or type web address", color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                }
                innerTextField()
            }
        )
        // Right: Shield icon
        IconButton(onClick = { /* Shield/protection info */ }) {
            Icon(
                Icons.Filled.Shield,
                contentDescription = "Protection",
                tint = iconColor.copy(alpha = 0.7f),
                modifier = Modifier.size(iconSize * 0.85f)
            )
        }
        // Right: Lykon brand icon
        IconButton(onClick = { /* Brand action */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Lykon",
                tint = Color.Unspecified,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
