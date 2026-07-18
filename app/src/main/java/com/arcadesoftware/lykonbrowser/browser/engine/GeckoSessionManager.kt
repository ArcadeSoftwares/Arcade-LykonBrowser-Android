package com.arcadesoftware.lykonbrowser.browser.engine

import android.content.Context
import org.mozilla.geckoview.GeckoSession

object GeckoSessionManager {
    fun createSession(context: Context): GeckoSession {
        val session = GeckoSession()
        // Provide a modern User-Agent to prevent sites from showing "Update browser" warnings
        session.settings.userAgentOverride = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
        val runtime = GeckoRuntimeProvider.getRuntime(context)
        session.open(runtime)
        return session
    }
}
