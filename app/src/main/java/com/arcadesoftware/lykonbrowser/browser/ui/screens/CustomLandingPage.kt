package com.arcadesoftware.lykonbrowser.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcadesoftware.lykonbrowser.R
import kotlin.random.Random

/**
 * Returns a fresh random-image URL each call.
 * Uses Unsplash's keyless "source" redirect endpoint — no API key/registration required.
 * A cache-busting query param is appended because source.unsplash.com otherwise gets
 * aggressively cached by some HTTP clients/CDNs for the same URL.
 */
private fun randomUnsplashUrl(width: Int = 1080, height: Int = 1920): String {
    val cacheBust = Random.nextInt(0, Int.MAX_VALUE)
    return "https://source.unsplash.com/random/${width}x${height}?nature,architecture,minimal&sig=$cacheBust"
}

@Composable
fun CustomLandingPage(
    modifier: Modifier = Modifier
) {
    val bgColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surfaceVariant
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary

    // Picked once per composition entry (i.e. once per app/session launch onto this screen),
    // not re-rolled on every recomposition.
    val wallpaperUrl = remember { randomUnsplashUrl() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background Image
        coil.compose.AsyncImage(
            model = "https://picsum.photos/1080/1920?random=1",
            contentDescription = "Background",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            // Falls back to the theme background color while loading / on failure,
            // so a network hiccup never shows a broken/blank image.
            error = null,
            placeholder = null
        )
        // Dark overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App branding — Shield icon + name
            Icon(
                painter = painterResource(id = R.drawable.ic_shield),
                contentDescription = "Lykon",
                tint = accentColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Lykon Browser",
                color = textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Private. Fast. Secure.",
                color = textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Privacy stats card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardColor)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shield),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Privacy Stats",
                        color = textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = "0",
                        label = "Trackers &\nads blocked",
                        accentColor = Color(0xFFFF6D00),
                        labelColor = textSecondary
                    )
                    // Vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(dividerColor)
                    )
                    StatItem(
                        value = "0 KB",
                        label = "Data\nsaved",
                        accentColor = Color(0xFF00B0FF),
                        labelColor = textSecondary
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(dividerColor)
                    )
                    StatItem(
                        value = "0s",
                        label = "Time\nsaved",
                        accentColor = Color(0xFF7C4DFF),
                        labelColor = textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick shortcuts row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickShortcut(
                    icon = R.drawable.ic_bookmark,
                    label = "Bookmarks",
                    cardColor = cardColor,
                    iconColor = textSecondary,
                    modifier = Modifier.weight(1f)
                )
                QuickShortcut(
                    icon = R.drawable.ic_tabs,
                    label = "Tabs",
                    cardColor = cardColor,
                    iconColor = textSecondary,
                    modifier = Modifier.weight(1f)
                )
                QuickShortcut(
                    icon = R.drawable.ic_more,
                    label = "Settings",
                    cardColor = cardColor,
                    iconColor = textSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Fills remaining space now that the bottom logo block is gone,
            // keeping the shortcuts row from sitting awkwardly high on tall screens.
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    accentColor: Color,
    labelColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = accentColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = labelColor,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun QuickShortcut(
    icon: Int,
    label: String,
    cardColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(cardColor)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = iconColor,
            fontSize = 11.sp
        )
    }
}