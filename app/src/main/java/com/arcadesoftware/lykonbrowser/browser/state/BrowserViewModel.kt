package com.arcadesoftware.lykonbrowser.browser.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebRequestError
import org.mozilla.geckoview.GeckoResult
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

enum class BrowserMode { NORMAL, PRIVATE, TOR }

class BrowserViewModel : ViewModel() {
    private val _browserMode = MutableStateFlow(BrowserMode.NORMAL)
    val browserMode: StateFlow<BrowserMode> = _browserMode.asStateFlow()

    fun setBrowserMode(mode: BrowserMode) {
        _browserMode.value = mode
    }

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

    // Search history for the overlay suggestions
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    // Track if the current page failed to load
    private val _pageError = MutableStateFlow(false)
    val pageError: StateFlow<Boolean> = _pageError.asStateFlow()

    val navigationDelegate = object : GeckoSession.NavigationDelegate {
        override fun onLocationChange(session: GeckoSession, url: String?, perms: MutableList<GeckoSession.PermissionDelegate.ContentPermission>) {
            url?.let { 
                if (it != "about:blank" && !it.startsWith("data:")) {
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
        
        override fun onLoadError(session: GeckoSession, uri: String?, error: WebRequestError): GeckoResult<String>? {
            _pageError.value = true
            // Load a blank background so the GeckoView doesn't show its default error,
            // allowing our Compose CustomErrorPage to render cleanly over it.
            return GeckoResult.fromValue("data:text/html,<html><body style='background-color: #121212;'></body></html>")
        }
    }
    
    val progressDelegate = object : GeckoSession.ProgressDelegate {
        override fun onPageStart(session: GeckoSession, url: String) {
            _isLoading.value = true
            if (!url.startsWith("data:")) {
                _pageError.value = false
            }
        }

        override fun onPageStop(session: GeckoSession, success: Boolean) {
            _isLoading.value = false
            if (!success) {
                _pageError.value = true
            }
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
        
        val isLikelyUrl = android.util.Patterns.WEB_URL.matcher(url).matches() || 
                          (url.contains(".") && !url.contains(" ") && (url.endsWith(".com") || url.endsWith(".in") || url.endsWith(".org") || url.endsWith(".net") || url.endsWith(".io")))
        
        val finalUrl = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            isLikelyUrl -> "https://$url"
            else -> "https://search.brave.com/search?q=${java.net.URLEncoder.encode(url, "UTF-8")}"
        }
        
        // Add to search history (avoid duplicates, keep most recent first)
        addToHistory(url)
        
        // CRITICAL FIX: Update currentUrl immediately so Compose state updates
        _currentUrl.value = finalUrl
        session.loadUri(finalUrl)
    }

    private fun addToHistory(query: String) {
        val current = _searchHistory.value.toMutableList()
        current.remove(query) // Remove duplicate if exists
        current.add(0, query) // Add to front
        if (current.size > 50) {
            current.removeAt(current.lastIndex) // Keep max 50 entries
        }
        _searchHistory.value = current
    }

    fun removeFromHistory(query: String) {
        _searchHistory.value = _searchHistory.value.filter { it != query }
    }
    
    fun goBack(session: GeckoSession) {
        session.goBack()
    }
    
    fun goForward(session: GeckoSession) {
        session.goForward()
    }
}
