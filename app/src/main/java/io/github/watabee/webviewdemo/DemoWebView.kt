package io.github.watabee.webviewdemo

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class DemoWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

    init {
        @SuppressLint("SetJavaScriptEnabled")
        // JavaScript の有効化
        settings.javaScriptEnabled = true
    }
}
