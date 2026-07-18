package com.arcadesoftware.lykonbrowser.browser.engine

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

@Composable
fun GeckoViewContainer(
    session: GeckoSession,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            GeckoView(context).apply {
                setSession(session)
            }
        },
        modifier = modifier,
        update = { geckoView ->
            if (geckoView.session != session) {
                geckoView.releaseSession()
                geckoView.setSession(session)
            }
        }
    )
}
