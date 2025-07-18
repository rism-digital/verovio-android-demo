package org.verovio.android.demo

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import org.verovio.android.demo.ui.theme.VerovioMEIViewerTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        System.loadLibrary("verovio-android")

        enableEdgeToEdge()

        setContent {

            val verovioModelView: VerovioModelView = viewModel()
            val context = LocalContext.current
            verovioModelView.initIfNeeded(context)

            VerovioMEIViewerTheme {
                Scaffold(
                    topBar = {
                        TopAppBarWithMenu(verovioModelView)
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        SvgWebView(svgContent = verovioModelView.svgString.value, onSizeChanged = verovioModelView::onSize)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithMenu(viewModel: VerovioModelView) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { },
        actions = {
            IconButton(onClick = { viewModel.onPrevious() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous"
                )
            }
            IconButton(onClick = { viewModel.onNext() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
            IconButton(onClick = { viewModel.onZoomIn() }) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = "Zoom In"
                )
            }
            IconButton(onClick = { viewModel.onZoomOut() }) {
                Icon(
                    imageVector = Icons.Filled.ZoomOut,
                    contentDescription = "Zoom Out"
                )
            }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Text("Font")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    viewModel.fontOptions.forEach { font ->
                        DropdownMenuItem(
                            text = { Text(font) },
                            onClick = {
                                viewModel.onFontSelect(font)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SvgWebView(
    svgContent: String,
    onSizeChanged: (IntSize) -> Unit // callback to report size
) {
    val html = """
        <!DOCTYPE html>
        <html>
        <head><meta name="viewport" content="width=device-width, initial-scale=1.0"></head>
        <body style="margin:0">
        $svgContent
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false // enable if needed
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                null,
                html,
                "text/html",
                "UTF-8",
                null
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                onSizeChanged(size)
            }
    )
}