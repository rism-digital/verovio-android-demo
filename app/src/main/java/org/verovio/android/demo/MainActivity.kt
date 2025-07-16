package org.verovio.android.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.verovio.android.demo.ui.theme.VerovioMEIViewerTheme

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.Icons

class MainActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val urls = listOf(
            "https://www.example.com",
            "https://www.wikipedia.org",
            "https://news.ycombinator.com"
        )

        setContent {
            VerovioMEIViewerTheme {
                var currentIndex by remember { mutableStateOf(0) }
                var currentZoom by remember { mutableStateOf(0) }
                var selectedFont by remember { mutableStateOf("Leipzig") }

                Scaffold(
                    topBar = {
                        TopAppBarWithMenu(
                            onPrevious = {
                                if (currentIndex > 0) currentIndex--
                            },
                            onNext = {
                                if (currentIndex < urls.size - 1) currentIndex++
                            },
                            onZoomOut = {
                                if (currentZoom > 0) currentZoom--
                            },
                            onZoomIn = {
                                if (currentZoom < urls.size - 1) currentZoom++
                            },
                            selectedFont = selectedFont,
                            onFontSelected = { selectedFont = it }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        WebPage(url = urls[currentIndex])
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithMenu(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    selectedFont: String,
    onFontSelected: (String) -> Unit
) {
    val fonts = listOf("Leipzig", "Bravura", "Leland", "Petaluma")
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { },
        actions = {
            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous"
                )
            }
            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
            IconButton(onClick = onZoomIn) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = "Zoom In"
                )
            }
            IconButton(onClick = onZoomOut) {
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
                    fonts.forEach { font ->
                        DropdownMenuItem(
                            text = { Text(font) },
                            onClick = {
                                onFontSelected(font)
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
fun WebPage(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        update = { it.loadUrl(url) },
        modifier = Modifier.fillMaxSize()
    )
}
