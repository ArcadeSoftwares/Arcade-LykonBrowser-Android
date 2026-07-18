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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.arcadesoftware.lykonbrowser.R

private val envImages = listOf(
    "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?q=80&w=1080&auto=format&fit=crop", 
    "https://images.unsplash.com/photo-1501854140801-50d01698950b?q=80&w=1080&auto=format&fit=crop", 
    "https://images.unsplash.com/photo-1426604966848-d7adac402bff?q=80&w=1080&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=1080&auto=format&fit=crop"
)

// Define some standard fallback "Most Visited" sites if history is empty
private val fallbackSites = listOf(
    "google.com", "wikipedia.org", "amazon.com", "reddit.com", "youtube.com"
)

@Composable
fun CustomLandingPage(
    searchHistory: List<String>,
    onHistoryItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val wallpaperUrl = remember { envImages.random() }
    
    // Extract domains/queries from history, or use fallback
    val displaySites = remember(searchHistory) {
        val sites = searchHistory.map { 
            it.removePrefix("https://").removePrefix("http://").removePrefix("www.").substringBefore("/")
        }.distinct().take(8)
        
        if (sites.isEmpty()) fallbackSites else sites
    }

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
            // Push content down slightly
            Spacer(modifier = Modifier.height(100.dp))

            // Privacy Stats Card matching the image design but without the blur morph
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // The Card (Black background, low alpha)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.2f)) // dark background card with less alpha
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
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
                        // Eye cross icon placeholder
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

            // Shortcuts Row (User search sites / most visited)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displaySites) { site ->
                    val initial = site.firstOrNull()?.uppercase() ?: "?"
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                .clickable { onHistoryItemClick(site) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initial,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = site.take(10) + if (site.length > 10) "..." else "",
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