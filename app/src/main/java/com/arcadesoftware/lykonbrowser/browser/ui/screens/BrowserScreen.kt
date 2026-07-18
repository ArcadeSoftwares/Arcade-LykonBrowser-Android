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
    val browserMode by viewModel.browserMode.collectAsState()
    
    val normalSession = remember { GeckoSessionManager.createSession(context, com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.NORMAL) }
    val privateSession = remember { GeckoSessionManager.createSession(context, com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE) }
    val torSession = remember { GeckoSessionManager.createSession(context, com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.TOR) }
    
    val session = when (browserMode) {
        com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.NORMAL -> normalSession
        com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE -> privateSession
        com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.TOR -> torSession
    }

    val currentUrl by viewModel.currentUrl.collectAsState()
    val canGoBack by viewModel.canGoBack.collectAsState()
    val canGoForward by viewModel.canGoForward.collectAsState()
    val openTabCount by viewModel.openTabCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val pageError by viewModel.pageError.collectAsState()

    var activeSheet by remember { mutableStateOf(BottomSheetType.NONE) }
    var showSearchOverlay by remember { mutableStateOf(false) }
    var showTabManager by remember { mutableStateOf(false) }
    var pendingMode by remember { mutableStateOf<com.arcadesoftware.lykonbrowser.browser.state.BrowserMode?>(null) }

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
            
            Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)

            // Web Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Keep GeckoView in the tree but hide it if not on a web page
                Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = if (currentUrl != "about:home" && !pageError) 1f else 0f }) {
                    GeckoViewContainer(session = session, modifier = Modifier.fillMaxSize())
                    
                    // Loading overlay
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                            androidx.compose.material3.LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

                if (currentUrl == "about:home") {
                    CustomLandingPage(
                        searchHistory = searchHistory,
                        mode = browserMode,
                        onHistoryItemClick = { url -> 
                            showSearchOverlay = false
                            viewModel.loadUrl(session, url) 
                        }
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
                onTabsClick = { showTabManager = true },
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

        // Tab Manager Overlay
        com.arcadesoftware.lykonbrowser.browser.ui.components.TabManagerOverlay(
            visible = showTabManager,
            currentMode = browserMode,
            onModeSwitch = { newMode ->
                if (newMode != browserMode) {
                    viewModel.setBrowserMode(newMode)
                    if (newMode != com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.NORMAL) {
                        val intentClass = if (newMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE) 
                            com.arcadesoftware.lykonbrowser.browser.engine.PrivateNotificationService::class.java 
                        else 
                            com.arcadesoftware.lykonbrowser.browser.engine.TorNotificationService::class.java
                            
                        val intent = android.content.Intent(context, intentClass)
                        if (android.os.Build.VERSION.SDK_INT >= 26) context.startForegroundService(intent) else context.startService(intent)
                    }
                }
            },
            onClose = { showTabManager = false },
            onNewTab = { 
                viewModel.loadUrl(session, "about:home")
                showTabManager = false
            }
        )
    }

    // Handle Screenshots (FLAG_SECURE) for Private Mode
    LaunchedEffect(browserMode) {
        val activity = context as? android.app.Activity
        if (activity != null) {
            if (browserMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE) {
                activity.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                activity.window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    if (pendingMode != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { pendingMode = null },
            title = { Text("Close current session?") },
            text = { Text("Opening this will close your active ${if (browserMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE) "Private" else "Tor"} session and all its tabs.") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val modeToSet = pendingMode!!
                    pendingMode = null
                    if (modeToSet == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.NORMAL) {
                        val restartIntent = android.content.Intent(context, com.arcadesoftware.lykonbrowser.MainActivity::class.java).apply {
                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        context.startActivity(restartIntent)
                    } else {
                        viewModel.setBrowserMode(modeToSet)
                        if (modeToSet == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE) {
                            val intent = android.content.Intent(context, com.arcadesoftware.lykonbrowser.browser.engine.PrivateNotificationService::class.java)
                            if (android.os.Build.VERSION.SDK_INT >= 26) context.startForegroundService(intent) else context.startService(intent)
                        } else if (modeToSet == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.TOR) {
                            val intent = android.content.Intent(context, com.arcadesoftware.lykonbrowser.browser.engine.TorNotificationService::class.java)
                            if (android.os.Build.VERSION.SDK_INT >= 26) context.startForegroundService(intent) else context.startService(intent)
                        }
                        activeSheet = BottomSheetType.NONE
                    }
                }) {
                    Text("Close & Switch")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { pendingMode = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (activeSheet != BottomSheetType.NONE) {
        ModalBottomSheet(
            onDismissRequest = { activeSheet = BottomSheetType.NONE },
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                when (activeSheet) {
                    BottomSheetType.SETTINGS -> {
                        SettingsDrawerContent(
                            currentMode = browserMode,
                            onPrivateClick = {
                                if (browserMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE) return@SettingsDrawerContent
                                if (browserMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.TOR) {
                                    pendingMode = com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE
                                } else {
                                    viewModel.setBrowserMode(com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE)
                                    val intent = android.content.Intent(context, com.arcadesoftware.lykonbrowser.browser.engine.PrivateNotificationService::class.java)
                                    if (android.os.Build.VERSION.SDK_INT >= 26) context.startForegroundService(intent) else context.startService(intent)
                                    activeSheet = BottomSheetType.NONE
                                }
                            },
                            onTorClick = {
                                if (browserMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.TOR) return@SettingsDrawerContent
                                if (browserMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE) {
                                    pendingMode = com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.TOR
                                } else {
                                    viewModel.setBrowserMode(com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.TOR)
                                    val intent = android.content.Intent(context, com.arcadesoftware.lykonbrowser.browser.engine.TorNotificationService::class.java)
                                    if (android.os.Build.VERSION.SDK_INT >= 26) context.startForegroundService(intent) else context.startService(intent)
                                    activeSheet = BottomSheetType.NONE
                                }
                            }
                        )
                    }
                    BottomSheetType.SECURITY -> {
                        SecurityDrawerContent(currentUrl)
                    }
                    BottomSheetType.SHIELD -> {
                        ShieldDrawerContent()
                    }
                    BottomSheetType.NONE -> {}
                }
            }
        }
    }
}

@Composable
private fun SettingsDrawerContent(
    isDefaultBrowser: Boolean = false, 
    currentMode: com.arcadesoftware.lykonbrowser.browser.state.BrowserMode,
    onPrivateClick: () -> Unit = {}, 
    onTorClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Banner
        if (!isDefaultBrowser) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Make Lykon default", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(4.dp))
                    Text("For faster, safer browsing", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                }
                Icon(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_shield), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(12.dp))
        }
        
        // Private & Tor Windows (Side by Side)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val isPrivateActive = currentMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.PRIVATE
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isPrivateActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), 
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onPrivateClick() }
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_private), 
                    contentDescription = null, 
                    tint = if (isPrivateActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, 
                    modifier = Modifier.size(20.dp)
                ) 
                Spacer(Modifier.width(8.dp))
                Text(
                    "Private", 
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, 
                    color = if (isPrivateActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, 
                    fontSize = 15.sp
                )
            }
            
            val isTorActive = currentMode == com.arcadesoftware.lykonbrowser.browser.state.BrowserMode.TOR
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isTorActive) Color(0xFFB388FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), 
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { 
                        android.widget.Toast.makeText(context, "Tor Window is coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_tor), 
                    contentDescription = null, 
                    tint = if (isTorActive) Color.Black else MaterialTheme.colorScheme.onSurface, 
                    modifier = Modifier.size(20.dp)
                ) 
                Spacer(Modifier.width(8.dp))
                Text(
                    "Tor", 
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, 
                    color = if (isTorActive) Color.Black else MaterialTheme.colorScheme.onSurface, 
                    fontSize = 15.sp
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        
        // Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsGridItem(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_history), "History", Modifier.weight(1f))
            SettingsGridItem(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_bookmark), "Bookmarks", Modifier.weight(1f))
            SettingsGridItem(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_downloads), "Downloads", Modifier.weight(1f))
            SettingsGridItem(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_passwords), "Passwords", Modifier.weight(1f))
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
            Icon(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_settings), contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(16.dp))
            Text("Settings", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun SettingsGridItem(icon: androidx.compose.ui.graphics.painter.Painter, label: String, modifier: Modifier) {
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSecure) Icons.Filled.Lock else Icons.Filled.Warning,
            contentDescription = null,
            tint = if (isSecure) Color(0xFF00E676) else Color(0xFFFF5252),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = domain.ifEmpty { "Website" },
            fontSize = 20.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isSecure) "Connection is secure" else "Connection is not secure",
            fontSize = 14.sp,
            color = if (isSecure) Color(0xFF00E676) else Color(0xFFFF5252)
        )
        Spacer(Modifier.height(24.dp))
        
        // Single simple row for cookies
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(16.dp))
            Text("Clear cookies and site data", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ShieldDrawerContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF6200EA), Color(0xFFB388FF))),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(androidx.compose.ui.res.painterResource(com.arcadesoftware.lykonbrowser.R.drawable.ic_shield), contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Lykon Shield", fontSize = 22.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text("Your privacy is protected.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(24.dp))

        // Creative Dummy Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Trackers stat
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("42", fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFFFF5252))
                Spacer(Modifier.height(4.dp))
                Text("Trackers Blocked", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
            
            // Ads stat
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("12", fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFFFFB300))
                Spacer(Modifier.height(4.dp))
                Text("Ads Blocked", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        Spacer(Modifier.height(16.dp))

        // Shield Status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Shields are UP", color = Color(0xFF00E676), fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text("Active on this site", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            androidx.compose.material3.Switch(checked = true, onCheckedChange = {})
        }
        Spacer(Modifier.height(16.dp))
        
        // Advanced Option
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
            Text("Advanced Shield Options", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.height(16.dp))
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
