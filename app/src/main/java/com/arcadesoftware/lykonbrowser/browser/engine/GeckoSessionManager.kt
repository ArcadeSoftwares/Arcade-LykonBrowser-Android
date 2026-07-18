package com.arcadesoftware.lykonbrowser.browser.engine

import android.content.Context
import org.mozilla.geckoview.GeckoSession

object GeckoSessionManager {
    fun createSession(context: Context): GeckoSession {
        val session = GeckoSession()
        val runtime = GeckoRuntimeProvider.getRuntime(context)
        session.open(runtime)
        return session
    }
}
