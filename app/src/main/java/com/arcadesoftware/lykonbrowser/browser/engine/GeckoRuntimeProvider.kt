package com.arcadesoftware.lykonbrowser.browser.engine

import android.content.Context
import org.mozilla.geckoview.GeckoRuntime

object GeckoRuntimeProvider {
    private var runtime: GeckoRuntime? = null

    fun getRuntime(context: Context): GeckoRuntime {
        if (runtime == null) {
            runtime = GeckoRuntime.create(context.applicationContext)
        }
        return runtime!!
    }
}
