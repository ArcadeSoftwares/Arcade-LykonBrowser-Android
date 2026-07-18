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
    onModeSwitch: (BrowserMode) -> Unit,
    onClose: () -> Unit,
    onNewTab: () -> Unit
) {
    if (!visible) return
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
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
                    .background(Color(0xFF202020))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Normal Mode Button
                Box(
                    modifier = Modifier
                        .size(48.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (currentMode == BrowserMode.NORMAL) Color(0xFF333333) else Color.Transparent)
                        .clickable { onModeSwitch(BrowserMode.NORMAL) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(2.dp, if (currentMode == BrowserMode.NORMAL) Color.White else Color.Gray, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = if (currentMode == BrowserMode.NORMAL) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Private Mode Button
                Box(
                    modifier = Modifier
                        .size(48.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (currentMode == BrowserMode.PRIVATE) Color(0xFF333333) else Color.Transparent)
                        .clickable { onModeSwitch(BrowserMode.PRIVATE) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_private),
                        contentDescription = "Private Tabs",
                        tint = if (currentMode == BrowserMode.PRIVATE) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Tor Mode Button
                Box(
                    modifier = Modifier
                        .size(48.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (currentMode == BrowserMode.TOR) Color(0xFF333333) else Color.Transparent)
                        .clickable { 
                            android.widget.Toast.makeText(context, "Tor Window is coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tor),
                        contentDescription = "Tor Tabs",
                        tint = if (currentMode == BrowserMode.TOR) Color(0xFFB388FF) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF202020))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("Search your tabs", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Active Tab Card (Dummy for now)
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(0.85f)
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (currentMode == BrowserMode.PRIVATE) Color(0xFF1E1E2E) else if (currentMode == BrowserMode.TOR) Color(0xFF2A1C3D) else Color(0xFFDCE2FA))
                    .clickable { onClose() }
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Icon(painterResource(id = R.drawable.ic_shield), null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Current Session", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(painterResource(id = R.drawable.ic_close), null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Tap to return", color = Color.Black.copy(alpha = 0.6f), fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.weight(1f))
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
                .background(Color(0xFFDCE2FA))
                .clickable { onNewTab() },
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 28.sp, color = Color.Black, fontWeight = FontWeight.Light)
        }
    }
}
