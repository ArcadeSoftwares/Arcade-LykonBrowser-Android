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
import androidx.compose.foundation.border
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush

// ...

private val envImages = listOf(
    "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?q=80&w=1080&auto=format&fit=crop", // Beautiful nature
    "https://images.unsplash.com/photo-1501854140801-50d01698950b?q=80&w=1080&auto=format&fit=crop", // Mountains
    "https://images.unsplash.com/photo-1426604966848-d7adac402bff?q=80&w=1080&auto=format&fit=crop", // Forest
    "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=1080&auto=format&fit=crop"  // Nature road
)

@Composable
fun CustomLandingPage(
    modifier: Modifier = Modifier
) {
    val textPrimary = Color.White
    val textSecondary = Color.White.copy(alpha = 0.7f)
    val accentColor = MaterialTheme.colorScheme.primary

    // Pick a random environment image from Unsplash
    val wallpaperUrl = remember { envImages.random() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background Image (No dark overlay anymore)
        coil.compose.AsyncImage(
            model = wallpaperUrl,
            contentDescription = "Background",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            error = null,
            placeholder = null
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
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Private. Fast. Secure.",
                color = textSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            // Push the privacy card to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Blur Morph Privacy Stats Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Blur morph aura behind the card
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 8.dp)
                        .blur(32.dp) // The blur morph effect!
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4FC3F7), Color(0xFF7C4DFF))
                            ),
                            RoundedCornerShape(24.dp)
                        )
                )

                // The actual card content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.1f)) // Extremely subtle frosting over the aurora
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(24.dp),
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

                Spacer(modifier = Modifier.height(24.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = "0",
                        label = "Trackers &\nads blocked",
                        accentColor = Color(0xFF4FC3F7),
                        labelColor = textSecondary
                    )
                    // Vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                    StatItem(
                        value = "0 KB",
                        label = "Data\nsaved",
                        accentColor = Color(0xFF4FC3F7),
                        labelColor = textSecondary
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                    StatItem(
                        value = "0s",
                        label = "Time\nsaved",
                        accentColor = Color(0xFF4FC3F7),
                        labelColor = textSecondary
                    )
                }
            }
            } // Close the Box we added for the blur morph!

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