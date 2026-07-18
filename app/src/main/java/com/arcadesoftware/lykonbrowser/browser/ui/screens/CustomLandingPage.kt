package com.arcadesoftware.lykonbrowser.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcadesoftware.lykonbrowser.R

// Lykon brand colors
private val LykonCyan = Color(0xFF00E5FF)
private val LykonPurple = Color(0xFF7C4DFF)
private val LykonDarkBg = Color(0xFF0F0F14)
private val LykonCardBg = Color(0xFF1A1A24)
private val LykonCardBorder = Color(0xFF2A2A3A)
private val LykonTextPrimary = Color(0xFFE8E8EC)
private val LykonTextSecondary = Color(0xFF8888A0)

@Composable
fun CustomLandingPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LykonDarkBg)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // App branding — Shield icon + name
        Icon(
            painter = painterResource(id = R.drawable.ic_shield),
            contentDescription = "Lykon",
            tint = LykonCyan,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Lykon Browser",
            color = LykonTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Private. Fast. Secure.",
            color = LykonTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Privacy stats card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LykonCardBg)
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
                    tint = LykonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Privacy Stats",
                    color = LykonTextPrimary,
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
                    accentColor = Color(0xFFFF6D00)
                )
                // Subtle vertical divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(LykonCardBorder)
                )
                StatItem(
                    value = "0 KB",
                    label = "Data\nsaved",
                    accentColor = LykonCyan
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(LykonCardBorder)
                )
                StatItem(
                    value = "0s",
                    label = "Time\nsaved",
                    accentColor = LykonPurple
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
                modifier = Modifier.weight(1f)
            )
            QuickShortcut(
                icon = R.drawable.ic_tabs,
                label = "Tabs",
                modifier = Modifier.weight(1f)
            )
            QuickShortcut(
                icon = R.drawable.ic_more,
                label = "Settings",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom tagline
        Text(
            text = "Powered by Arcade Software",
            color = LykonTextSecondary.copy(alpha = 0.5f),
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    accentColor: Color
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
            color = LykonTextSecondary,
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LykonCardBg)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = LykonTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = LykonTextSecondary,
            fontSize = 11.sp
        )
    }
}
