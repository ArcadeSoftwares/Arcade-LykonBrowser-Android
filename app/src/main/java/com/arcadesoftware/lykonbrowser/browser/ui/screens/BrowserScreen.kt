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
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Delete

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
            // Top Bar Area — AddressBar
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
                // Keep GeckoView in the tree at all times so it never detaches, preventing the black screen bug.
                // We move it off-screen and make it transparent when on the homescreen.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            if (currentUrl == "about:home") {
                                alpha = 0f
                                translationX = 10000f
                            } else {
                                alpha = 1f
                                translationX = 0f
                            }
                        }
                ) {
                    GeckoViewContainer(
                        session = session,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (currentUrl == "about:home") {
                    CustomLandingPage(
                        searchHistory = searchHistory,
                        onHistoryItemClick = { query -> viewModel.loadUrl(session, query) }
                    )
                } else if (pageError) {
                    CustomErrorPage(
                        url = currentUrl,
                        onRetry = { viewModel.loadUrl(session, currentUrl) }
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
                        SettingsDrawerContent()
                    }
                    BottomSheetType.SECURITY, BottomSheetType.SHIELD -> {
                        SecurityDrawerContent(currentUrl)
                    }
                    BottomSheetType.NONE -> {}
                }
            }
        }
    }
}

@Composable
private fun SettingsDrawerContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Make Lykon your default", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text("Fast, private, and made for you.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.Done, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))
        
        // Extensions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_shield), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface) 
            Spacer(Modifier.width(16.dp))
            Text("Extensions", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.height(12.dp))
        
        // Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsGridItem(Icons.Filled.List, "History", Modifier.weight(1f))
            SettingsGridItem(Icons.Filled.Favorite, "Bookmarks", Modifier.weight(1f))
            SettingsGridItem(Icons.Filled.KeyboardArrowDown, "Downloads", Modifier.weight(1f))
            SettingsGridItem(Icons.Filled.Lock, "Passwords", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        
        // Sign in
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Sign in", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text("Synchronise passwords, bookmarks and more", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(12.dp))
        
        // Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(16.dp))
            Text("Settings", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun SettingsGridItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SecurityDrawerContent(currentUrl: String) {
    val isSecure = currentUrl.startsWith("https://")
    val domain = currentUrl.removePrefix("https://").removePrefix("http://").substringBefore("/")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header banner
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_shield), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Lykon is on guard", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text("You're protected. If we spot something, we'll let you know.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
            }
        }
        Spacer(Modifier.height(24.dp))
        
        // Protection toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Enhanced Tracking Protection", color = Color(0xFFB388FF), fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text("If something looks broken on this site, try turning it off.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            androidx.compose.material3.Switch(checked = true, onCheckedChange = {})
        }
        Spacer(Modifier.height(16.dp))
        
        SecurityDrawerRow(Icons.Filled.Done, "No trackers found")
        SecurityDrawerRow(if (isSecure) Icons.Filled.Lock else Icons.Filled.Warning, if (isSecure) "Secure connection" else "Connection not secure", if (isSecure) "Verified by Let's Encrypt" else null)
        SecurityDrawerRow(Icons.Filled.Delete, "Clear cookies and site data")
        
        Spacer(Modifier.height(8.dp))
        Text("Privacy Settings", color = Color(0xFFB388FF), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp).clickable { })
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SecurityDrawerRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    Spacer(Modifier.height(8.dp))
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
