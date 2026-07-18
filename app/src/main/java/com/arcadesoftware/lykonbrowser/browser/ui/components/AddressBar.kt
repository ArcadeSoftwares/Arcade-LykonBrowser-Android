package com.arcadesoftware.lykonbrowser.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcadesoftware.lykonbrowser.R
import java.net.URL

@Composable
fun AddressBar(
    url: String,
    backgroundColor: Color,
    textColor: Color,
    iconColor: Color,
    shape: Shape,
    height: Dp,
    onClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onShieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHttps = url.startsWith("https://")
    val isHttp = url.startsWith("http://")
    val displayUrl = if (url.isEmpty() || url == "about:home") {
        "Search or type web address"
    } else {
        url.removePrefix("https://").removePrefix("http://").removePrefix("www.")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Security Indicator Area (Clickable separately)
        Row(
            modifier = Modifier.clickable { onSecurityClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isHttps) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Secure",
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            } else if (isHttp) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Not Secure",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Not secure",
                    color = Color.Red,
                    fontSize = 11.sp
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // URL text or placeholder
        Text(
            text = displayUrl,
            color = if (url.isEmpty() || url == "about:home") textColor.copy(alpha = 0.5f) else textColor,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Shield Icon (Permanently on the right side)
        Icon(
            painter = painterResource(id = R.drawable.ic_shield),
            contentDescription = "Shield",
            tint = MaterialTheme.colorScheme.primary, // Using primary color to stand out, like Brave
            modifier = Modifier
                .size(18.dp)
                .clickable { onShieldClick() }
        )
    }
}
