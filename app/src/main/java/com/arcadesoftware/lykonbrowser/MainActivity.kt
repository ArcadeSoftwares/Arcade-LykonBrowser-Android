package com.arcadesoftware.lykonbrowser

import android.os.Bundle
import android.os.Build
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.arcadesoftware.lykonbrowser.browser.ui.screens.BrowserScreen
import com.arcadesoftware.lykonbrowser.ui.theme.LykonbrowserTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        // Handle intent if launched from notification
        handleIntent(intent)

        setContent {
            LykonbrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BrowserScreen(
                        modifier = Modifier.padding(innerPadding),
                        intentActionState = intentAction
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private val _intentAction = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val intentAction: kotlinx.coroutines.flow.StateFlow<String?> = _intentAction.asStateFlow()

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            com.arcadesoftware.lykonbrowser.browser.engine.PrivateNotificationService.ACTION_CLOSE_PRIVATE,
            com.arcadesoftware.lykonbrowser.browser.engine.TorNotificationService.ACTION_CLOSE_TOR -> {
                _intentAction.value = intent.action
            }
        }
    }
}