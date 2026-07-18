package com.arcadesoftware.lykonbrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.arcadesoftware.lykonbrowser.browser.ui.screens.BrowserScreen
import com.arcadesoftware.lykonbrowser.ui.theme.LykonbrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LykonbrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BrowserScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}