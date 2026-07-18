package com.arcadesoftware.lykonbrowser.browser.ui.screens

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import com.arcadesoftware.lykonbrowser.browser.engine.GeckoSessionManager
import com.arcadesoftware.lykonbrowser.browser.engine.GeckoViewContainer
import com.arcadesoftware.lykonbrowser.browser.state.BrowserViewModel
import com.arcadesoftware.lykonbrowser.browser.ui.components.AddressBar
import com.arcadesoftware.lykonbrowser.browser.ui.components.BottomToolbar
import com.arcadesoftware.lykonbrowser.browser.ui.components.SearchOverlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning

enum class BottomSheetType {
    NONE, SETTINGS, SECURITY, SHIELD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    modifier: Modifier = Modifier,
    viewModel: BrowserViewModel = viewModel()
) {
    val context = LocalContext.current
    val session = remember { GeckoSessionManager.createSession(context) }
    
    val currentUrl by viewModel.currentUrl.collectAsState()
    val canGoBack by viewModel.canGoBack.collectAsState()
    val canGoForward by viewModel.canGoForward.collectAsState()
    val openTabCount by viewModel.openTabCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val pageError by viewModel.pageError.collectAsState()

    var activeSheet by remember { mutableStateOf(BottomSheetType.NONE) }
    var showSearchOverlay by remember { mutableStateOf(false) }

    val bottomBarHeight = 48.dp
    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
    var bottomBarOffsetHeightPx by remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = bottomBarOffsetHeightPx - delta
                bottomBarOffsetHeightPx = newOffset.coerceIn(0f, bottomBarHeightPx)
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(session) {
        session.navigationDelegate = viewModel.navigationDelegate
        session.progressDelegate = viewModel.progressDelegate
        // loadUrl("about:home") sets currentUrl = "about:home" and does NOT call session.loadUri
        viewModel.loadUrl(session, "about:home")
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Main browser content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(nestedScrollConnection)
        ) {
            // Top Bar Area — AddressBar (display only, tapping opens overlay)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                AddressBar(
                    url = if (currentUrl == "about:home") "" else currentUrl,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    height = 40.dp,
                    onClick = { showSearchOverlay = true },
                    onSecurityClick = { 
                        if (currentUrl != "about:home" && currentUrl.isNotEmpty()) {
                            activeSheet = BottomSheetType.SECURITY 
                        }
                    },
                    onShieldClick = { activeSheet = BottomSheetType.SHIELD },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            // Gradient Loader
            if (isLoading) {
                val infiniteTransition = rememberInfiniteTransition(label = "loader")
                val offset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1000f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "loader_offset"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF00FFFF), Color(0xFF8A2BE2), Color(0xFFFF00FF), Color(0xFF00FFFF)),
                                startX = offset - 1000f,
                                endX = offset
                            )
                        )
                )
            }
            
            // Content Area
            Box(modifier = Modifier.weight(1f)) {
                if (currentUrl == "about:home") {
                    CustomLandingPage()
                } else if (pageError) {
                    CustomErrorPage(
                        url = currentUrl,
                        onRetry = { viewModel.loadUrl(session, currentUrl) }
                    )
                } else {
                    GeckoViewContainer(
                        session = session,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Bottom Toolbar
            BottomToolbar(
                modifier = Modifier.offset { IntOffset(x = 0, y = bottomBarOffsetHeightPx.roundToInt()) },
                backgroundColor = MaterialTheme.colorScheme.surface,
                iconColor = MaterialTheme.colorScheme.onSurface,
                height = 48.dp,
                tabCount = openTabCount,
                onHomeClick = { viewModel.loadUrl(session, "about:home") },
                onBookmarkClick = { /* Handle bookmarks click */ },
                onNewTabClick = { /* Handle new tab */ },
                onTabsClick = { /* Handle tabs click */ },
                onMoreClick = { activeSheet = BottomSheetType.SETTINGS }
            )
        }

        // Search Overlay (full screen, on top of everything)
        SearchOverlay(
            visible = showSearchOverlay,
            currentUrl = currentUrl,
            searchHistory = searchHistory,
            onSubmit = { query ->
                showSearchOverlay = false
                viewModel.loadUrl(session, query)
            },
            onDismiss = { showSearchOverlay = false },
            onRemoveHistoryItem = { viewModel.removeFromHistory(it) }
        )
    }


    if (activeSheet != BottomSheetType.NONE) {
        ModalBottomSheet(
            onDismissRequest = { activeSheet = BottomSheetType.NONE }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                when (activeSheet) {
                    BottomSheetType.SETTINGS -> {
                        Text(
                            text = "Settings", 
                            fontSize = 20.sp, 
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, 
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        ListItem(
                            headlineContent = { Text("History") },
                            leadingContent = { Icon(Icons.Filled.List, contentDescription = null) },
                            modifier = Modifier.clickable { }
                        )
                        ListItem(
                            headlineContent = { Text("Bookmarks") },
                            leadingContent = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                            modifier = Modifier.clickable { }
                        )
                        ListItem(
                            headlineContent = { Text("App Settings") },
                            leadingContent = { Icon(Icons.Filled.Settings, contentDescription = null) },
                            modifier = Modifier.clickable { }
                        )
                    }
                    BottomSheetType.SECURITY -> {
                        val isSecure = currentUrl.startsWith("https://")
                        val infiniteTransition = rememberInfiniteTransition()
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        val iconColor = if (isSecure) Color(0xFF00E676) else Color(0xFFFF3D00)
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                // Glowing pulsing ring
                                Box(
                                    modifier = Modifier
                                        .size(80.dp * scale)
                                        .background(iconColor.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(iconColor.copy(alpha = 0.25f), androidx.compose.foundation.shape.CircleShape)
                                )
                                Icon(
                                    imageVector = if (isSecure) Icons.Filled.Lock else Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = if (isSecure) "Connection is Secure" else "Connection Not Secure", 
                                fontSize = 22.sp, 
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = if (isSecure) "Your information (for example, passwords or credit card numbers) is private when it is sent to this site." 
                                       else "You should not enter any sensitive information on this site because it could be stolen by attackers.", 
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Domain chip
                            val domain = try { java.net.URL(currentUrl).host } catch (e: Exception) { currentUrl.take(20) }
                            Row(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Done, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(domain ?: "Local", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    BottomSheetType.SHIELD -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Shields up for this site", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Done, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("0 Trackers & ads blocked", fontSize = 16.sp)
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Done, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Connections upgraded to HTTPS", fontSize = 16.sp)
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.List, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Block Scripts", fontSize = 16.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("Disabled", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    BottomSheetType.NONE -> {}
                }
            }
        }
    }
}

@Composable
fun CustomErrorPage(url: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Site cannot be reached",
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Check if there is a typo in $url, or check your internet connection.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        androidx.compose.material3.Button(
            onClick = onRetry,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Reload")
        }
    }
}
