package com.arcadesoftware.lykonbrowser.browser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcadesoftware.lykonbrowser.browser.engine.GeckoSessionManager
import com.arcadesoftware.lykonbrowser.browser.engine.GeckoViewContainer
import com.arcadesoftware.lykonbrowser.browser.state.BrowserViewModel
import com.arcadesoftware.lykonbrowser.browser.ui.components.AddressBar
import com.arcadesoftware.lykonbrowser.browser.ui.components.BottomToolbar

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

    LaunchedEffect(session) {
        session.navigationDelegate = viewModel.navigationDelegate
        session.progressDelegate = viewModel.progressDelegate
        viewModel.loadUrl(session, "about:home")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar Area (solid background so it doesn't overlap content)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 16.dp, bottom = 8.dp)
        ) {
            AddressBar(
                url = if (currentUrl == "about:home") "" else currentUrl,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(26.dp), // Capsule
                height = 52.dp,
                horizontalPadding = 12.dp,
                iconSize = 24.dp,
                shieldBackgroundAlpha = 0.1f,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                onBackClick = { viewModel.goBack(session) },
                onForwardClick = { viewModel.goForward(session) },
                onUrlClick = { /* Handle URL click */ },
                onShieldClick = { /* Handle Shield click */ },
                modifier = Modifier.align(Alignment.Center)
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
            backgroundColor = MaterialTheme.colorScheme.surface,
            iconColor = MaterialTheme.colorScheme.onSurface,
            height = 56.dp,
            tabCount = openTabCount,
            onHomeClick = { viewModel.loadUrl(session, "about:home") },
            onBookmarksClick = { /* Handle bookmarks click */ },
            onTabsClick = { /* Handle tabs click */ },
            onMenuClick = { /* Handle menu click */ }
        )
    }
}

@Composable
fun CustomLandingPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Lykon",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "A faster, more private web.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
