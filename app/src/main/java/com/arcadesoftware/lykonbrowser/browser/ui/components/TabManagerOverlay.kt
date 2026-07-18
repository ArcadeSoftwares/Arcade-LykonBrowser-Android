package com.arcadesoftware.lykonbrowser.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcadesoftware.lykonbrowser.R
import com.arcadesoftware.lykonbrowser.browser.state.BrowserMode

@Composable
fun TabManagerOverlay(
    visible: Boolean,
    currentMode: BrowserMode,
    tabs: List<String>,
    onModeSwitch: (BrowserMode) -> Unit,
    onClose: () -> Unit,
    onNewTab: () -> Unit
) {
    if (!visible) return
    val context = androidx.compose.ui.platform.LocalContext.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val pillBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = 40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Mode Switcher Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(pillBgColor)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Normal Mode Button
                Box(
                    modifier = Modifier
                        .size(48.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (currentMode == BrowserMode.NORMAL) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { onModeSwitch(BrowserMode.NORMAL) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(2.dp, if (currentMode == BrowserMode.NORMAL) onSurfaceColor else onSurfaceVariantColor, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = if (currentMode == BrowserMode.NORMAL) onSurfaceColor else onSurfaceVariantColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Private Mode Button
                Box(
                    modifier = Modifier
                        .size(48.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (currentMode == BrowserMode.PRIVATE) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { onModeSwitch(BrowserMode.PRIVATE) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_private),
                        contentDescription = "Private Tabs",
                        tint = if (currentMode == BrowserMode.PRIVATE) onSurfaceColor else onSurfaceVariantColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Tor Mode Button
                Box(
                    modifier = Modifier
                        .size(48.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (currentMode == BrowserMode.TOR) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { 
                            android.widget.Toast.makeText(context, "Tor Window is coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tor),
                        contentDescription = "Tor Tabs",
                        tint = if (currentMode == BrowserMode.TOR) Color(0xFFB388FF) else onSurfaceVariantColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Active Tab Card Grid (Chrome-style previews)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tabs.size) { index ->
                    val title = if (tabs[index] == "about:home" || tabs[index].isEmpty()) "New Tab" else tabs[index]
                    val bgColor = if (currentMode == BrowserMode.PRIVATE) Color(0xFF1E1E2E) else if (currentMode == BrowserMode.TOR) Color(0xFF2A1C3D) else MaterialTheme.colorScheme.surfaceVariant
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.7f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { onClose() }
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Top Bar of the Tab Preview
                            Row(
                                verticalAlignment = Alignment.CenterVertically, 
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.ic_shield), 
                                    contentDescription = null, 
                                    tint = onSurfaceColor, 
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title, 
                                    color = onSurfaceColor, 
                                    fontWeight = FontWeight.Medium, 
                                    fontSize = 12.sp, 
                                    maxLines = 1, 
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    painterResource(id = R.drawable.ic_close), 
                                    contentDescription = "Close Tab", 
                                    tint = onSurfaceColor, 
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            
                            // Body of Tab Preview
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Tap to switch", color = onSurfaceColor.copy(alpha = 0.5f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Add Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onNewTab() },
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 28.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Light)
        }
    }
}
