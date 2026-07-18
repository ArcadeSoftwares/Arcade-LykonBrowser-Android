package com.arcadesoftware.lykonbrowser.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.arcadesoftware.lykonbrowser.R
import com.arcadesoftware.lykonbrowser.browser.ui.components.AddressBar

private val envImages = listOf(
    "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?q=80&w=1080&auto=format&fit=crop", 
    "https://images.unsplash.com/photo-1501854140801-50d01698950b?q=80&w=1080&auto=format&fit=crop", 
    "https://images.unsplash.com/photo-1426604966848-d7adac402bff?q=80&w=1080&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=1080&auto=format&fit=crop"
)

data class ShortcutItem(val name: String, val color: Color, val iconId: Int? = null)

private val shortcuts = listOf(
    ShortcutItem("Reddit", Color(0xFFFF4500)),
    ShortcutItem("YouTube", Color(0xFFFF0000)),
    ShortcutItem("X", Color(0xFF000000)),
    ShortcutItem("GitHub", Color(0xFF24292E)),
    ShortcutItem("Notion", Color(0xFF000000)),
    ShortcutItem("Pinterest", Color(0xFFE60023)),
    ShortcutItem("Medium", Color(0xFF000000))
)

@Composable
fun CustomLandingPage(
    onSearchClick: () -> Unit,
    onShieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wallpaperUrl = remember { envImages.random() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = wallpaperUrl,
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            error = null,
            placeholder = null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Fake floating AddressBar
            AddressBar(
                url = "about:home",
                backgroundColor = Color.Black.copy(alpha = 0.4f),
                textColor = Color.White,
                iconColor = Color.White.copy(alpha = 0.8f),
                shape = RoundedCornerShape(24.dp),
                height = 56.dp,
                onClick = onSearchClick,
                onSecurityClick = {}, // Handled by search overlay
                onShieldClick = onShieldClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Privacy Stats Card matching the image design
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Blur Morph aura
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 12.dp)
                        .blur(40.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4FC3F7).copy(alpha = 0.5f), Color(0xFF7C4DFF).copy(alpha = 0.5f))
                            ),
                            RoundedCornerShape(24.dp)
                        )
                )

                // The Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.45f)) // dark frosted glass
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shield),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy Stats",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        // Eye cross icon placeholder (using list icon or close for now)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Hide",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(label = "Trackers & Ads\nBlocked", value = "3,452", valueColor = Color(0xFFFF5722))
                        StatItem(label = "Est. Data\nSaved", value = "1.2 GB", valueColor = Color(0xFF29B6F6))
                        StatItem(label = "Est. Time\nSaved", value = "2h 47m", valueColor = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Shortcuts Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(shortcuts) { shortcut ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = shortcut.name.first().toString(),
                                color = shortcut.color.takeIf { it != Color.Black } ?: Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = shortcut.name,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}