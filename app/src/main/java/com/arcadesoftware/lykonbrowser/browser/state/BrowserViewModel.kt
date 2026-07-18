package com.arcadesoftware.lykonbrowser.browser.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mozilla.geckoview.GeckoSession

class BrowserViewModel : ViewModel() {
    private val _currentUrl = MutableStateFlow("about:home")
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    private val _openTabCount = MutableStateFlow(1)
    val openTabCount: StateFlow<Int> = _openTabCount.asStateFlow()

    val navigationDelegate = object : GeckoSession.NavigationDelegate {
        override fun onLocationChange(session: GeckoSession, url: String?, perms: MutableList<GeckoSession.PermissionDelegate.ContentPermission>) {
            url?.let { 
                if (it != "about:blank") {
                    _currentUrl.value = it 
                }
            }
        }

        override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
            _canGoBack.value = canGoBack
        }

        override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
            _canGoForward.value = canGoForward
        }
    }
    
    val progressDelegate = object : GeckoSession.ProgressDelegate {
        override fun onPageStart(session: GeckoSession, url: String) {
            _isLoading.value = true
        }

        override fun onPageStop(session: GeckoSession, success: Boolean) {
            _isLoading.value = false
        }
    }

    fun loadUrl(session: GeckoSession, url: String) {
        if (url == "about:home") {
            // Show custom landing page without loading a URI in GeckoView
            _currentUrl.value = "about:home"
            _canGoBack.value = false
            _canGoForward.value = false
            _isLoading.value = false
            return
        }
        val finalUrl = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.contains(".") && !url.contains(" ") -> "https://$url"
            else -> "https://search.brave.com/search?q=${java.net.URLEncoder.encode(url, "UTF-8")}"
        }
        session.loadUri(finalUrl)
    }
    
    fun goBack(session: GeckoSession) {
        session.goBack()
    }
    
    fun goForward(session: GeckoSession) {
        session.goForward()
    }
}
