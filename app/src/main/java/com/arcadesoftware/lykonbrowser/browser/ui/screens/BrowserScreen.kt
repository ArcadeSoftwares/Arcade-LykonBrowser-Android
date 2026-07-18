package com.arcadesoftware.lykonbrowser.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
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

    var showBottomSheet by remember { mutableStateOf(false) }
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
                    height = 48.dp,
                    onClick = { showSearchOverlay = true },
                    onSecurityClick = {
                        // For now, just show the settings bottom sheet, we can expand it later
                        showBottomSheet = true 
                    },
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
                onMoreClick = { showBottomSheet = true }
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

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Settings", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
                Text("History", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
