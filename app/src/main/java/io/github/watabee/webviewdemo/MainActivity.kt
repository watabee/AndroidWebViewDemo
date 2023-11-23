package io.github.watabee.webviewdemo

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.webkit.WebViewAssetLoader
import com.squareup.moshi.Moshi
import java.io.File

private const val URL = "https://${WebViewAssetLoader.DEFAULT_DOMAIN}"
private const val CACHE_IMAGES_PATH = "cache_images"

class MainActivity : ComponentActivity() {

    private val moshi: Moshi = createMoshi()

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            return@registerForActivityResult
        }

        val file = getImageFile()
        if (!file.exists()) {
            return@registerForActivityResult
        }

        val webView: DemoWebView = findViewById(R.id.web_view)
        val src = "${URL}/${CACHE_IMAGES_PATH}/image.jpeg?t=${System.currentTimeMillis()}"
        webView.evaluateJavascript("main.setImageSrc('$src')", null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // デバッグの設定: https://developer.chrome.com/docs/devtools/remote-debugging/
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        setupWebView()
    }

    private fun setupWebView() {
        val webView: DemoWebView = findViewById(R.id.web_view)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(this))
            .addPathHandler("/${CACHE_IMAGES_PATH}/", WebViewAssetLoader.InternalStoragePathHandler(this, getCacheImagesDirectory()))
            .build()

        val webViewClient = DemoWebViewClient(assetLoader)
        webView.webViewClient = webViewClient

        createJsObject(webView, jsObjName = "jsObject", allowedOriginRules = setOf(URL), ::handleJsOutputMessage)

        webView.loadUrl("${URL}/assets/sample.html")
    }

    /**
     * JavaScript から通知されるメッセージを処理する.
     */
    private fun handleJsOutputMessage(message: String?) {
        Log.d("MainActivity", "*** message = $message")
        val jsOutputEvent = moshi.adapter(JsOutputEvent::class.java)
            .fromJson(message.orEmpty()) ?: return

        when (jsOutputEvent) {
            is JsOutputEvent.OnLoadScript -> {
                Toast.makeText(this, "Loaded script", Toast.LENGTH_SHORT).show()
            }

            is JsOutputEvent.OnCameraButtonClicked -> {
                openCamera()
            }
        }
    }

    private fun openCamera() {
        val uri = FileProvider.getUriForFile(this, "io.github.watabee.webviewdemo.fileprovider", getImageFile())
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, uri)
            .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        takePicture.launch(intent)
    }

    private fun getCacheImagesDirectory(): File {
        return File(cacheDir, "images").apply { mkdirs() }
    }

    private fun getImageFile(): File {
        return getCacheImagesDirectory().resolve("image.jpeg")
    }
}
