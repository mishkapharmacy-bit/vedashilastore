package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.VedashilaGold

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun StorefrontWebView(
  initialUrl: String = "https://vedashila.com",
  currentRequestedUrl: String? = null,
  modifier: Modifier = Modifier,
  onUrlChanged: (String) -> Unit = {},
  onTitleChanged: (String) -> Unit = {}
) {
  val context = LocalContext.current
  var webViewRef by remember { mutableStateOf<WebView?>(null) }
  var pageProgress by remember { mutableIntStateOf(0) }
  var isLoading by remember { mutableStateOf(true) }
  var canGoBack by remember { mutableStateOf(false) }

  // Intercept system back button to navigate back in WebView history if available
  BackHandler(enabled = canGoBack) {
    webViewRef?.let {
      if (it.canGoBack()) {
        it.goBack()
      }
    }
  }

  // Load new requested URL if passed
  androidx.compose.runtime.LaunchedEffect(currentRequestedUrl) {
    if (!currentRequestedUrl.isNullOrBlank() && currentRequestedUrl != webViewRef?.url) {
      webViewRef?.loadUrl(currentRequestedUrl)
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { ctx ->
        WebView(ctx).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )

          settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            cacheMode = WebSettings.LOAD_DEFAULT
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
          }

          webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
              pageProgress = newProgress
              isLoading = newProgress < 100
              canGoBack = view?.canGoBack() == true
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
              super.onReceivedTitle(view, title)
              if (!title.isNullOrBlank()) {
                onTitleChanged(title)
              }
            }
          }

          webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              super.onPageStarted(view, url, favicon)
              isLoading = true
              url?.let(onUrlChanged)
              canGoBack = view?.canGoBack() == true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              super.onPageFinished(view, url)
              isLoading = false
              canGoBack = view?.canGoBack() == true
            }

            override fun onReceivedError(
              view: WebView?,
              request: WebResourceRequest?,
              error: WebResourceError?
            ) {
              super.onReceivedError(view, request, error)
              // Ignore subresource load errors (trackers/fonts), handle main frame only
              if (request?.isForMainFrame == true) {
                isLoading = false
              }
            }

            override fun shouldOverrideUrlLoading(
              view: WebView?,
              request: WebResourceRequest?
            ): Boolean {
              val url = request?.url?.toString() ?: return false

              // Handle Payment Gateway UPI / External App Intents (PhonePe, Paytm, GPay, WhatsApp, UPI)
              val isPaymentOrExternalApp = url.startsWith("upi://") ||
                  url.startsWith("phonepe://") ||
                  url.startsWith("paytmmp://") ||
                  url.startsWith("gpay://") ||
                  url.startsWith("whatsapp://") ||
                  url.startsWith("intent://")

              if (isPaymentOrExternalApp) {
                try {
                  val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                  context.startActivity(intent)
                  return true
                } catch (e: Exception) {
                  Toast.makeText(
                    context,
                    "Redirecting to payment app...",
                    Toast.LENGTH_SHORT
                  ).show()
                  return false
                }
              }

              // Normal HTTP/HTTPS navigation stays inside WebView
              return false
            }
          }

          loadUrl(initialUrl)
          webViewRef = this
        }
      },
      update = {
        // Updated on recomposition if needed
      }
    )

    if (isLoading && pageProgress in 1..99) {
      LinearProgressIndicator(
        progress = { pageProgress / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .align(Alignment.TopCenter),
        color = VedashilaGold
      )
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      webViewRef?.stopLoading()
    }
  }
}
