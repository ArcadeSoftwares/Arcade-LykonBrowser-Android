import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebRequestError

val delegate = object : GeckoSession.NavigationDelegate {
    override fun onLoadError(session: GeckoSession, uri: String?, error: WebRequestError): org.mozilla.geckoview.GeckoResult<String>? {
        return null
    }
}
