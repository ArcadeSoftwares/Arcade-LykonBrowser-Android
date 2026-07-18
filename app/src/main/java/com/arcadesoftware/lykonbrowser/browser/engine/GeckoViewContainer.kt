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
            val swipeRefreshLayout = androidx.swiperefreshlayout.widget.SwipeRefreshLayout(context)
            val geckoView = GeckoView(context).apply {
                setSession(session)
            }
            swipeRefreshLayout.addView(geckoView)
            
            swipeRefreshLayout.setOnRefreshListener {
                session.reload()
                // Stop the refreshing animation after a short delay or state callback, 
                // but since GeckoView doesn't easily expose page load end immediately in a simple callback here,
                // we'll just stop the animation after 1 second as a simple UI feedback.
                swipeRefreshLayout.postDelayed({
                    swipeRefreshLayout.isRefreshing = false
                }, 1000)
            }
            swipeRefreshLayout
        },
        modifier = modifier,
        update = { swipeRefreshLayout ->
            var geckoView: GeckoView? = null
            for (i in 0 until swipeRefreshLayout.childCount) {
                val child = swipeRefreshLayout.getChildAt(i)
                if (child is GeckoView) {
                    geckoView = child
                    break
                }
            }
            
            geckoView?.let { view ->
                if (view.session != session) {
                    view.setSession(session)
                }
            }
        }
    )
}
