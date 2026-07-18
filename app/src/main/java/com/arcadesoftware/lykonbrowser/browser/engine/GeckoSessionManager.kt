package com.arcadesoftware.lykonbrowser.browser.engine

import android.content.Context
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import com.arcadesoftware.lykonbrowser.browser.state.BrowserMode

object GeckoSessionManager {
    fun createSession(context: Context, mode: BrowserMode = BrowserMode.NORMAL): GeckoSession {
        val isPrivate = mode == BrowserMode.PRIVATE || mode == BrowserMode.TOR
        val settings = GeckoSessionSettings.Builder()
            .usePrivateMode(isPrivate)
            .build()
            
        val session = GeckoSession(settings)
        // Provide a modern User-Agent to prevent sites from showing "Update browser" warnings
        session.settings.userAgentOverride = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
        
        val runtime = GeckoRuntimeProvider.getRuntime(context)
        
        // Note: Full Tor proxy configuration requires modifying GeckoRuntime settings via about:config overrides.
        // For now, we launch the session in Private Mode which provides the isolation layer for Tor.
        // Proper proxying (e.g. 127.0.0.1:9050) would be applied to the runtime or Android system.
        
        session.open(runtime)
        return session
    }
}
