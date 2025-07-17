package org.verovio.android.demo

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import android.content.Context
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.onSizeChanged

class ContentView(resourcePath: String) : ViewModel() {

    // Native wrapper instance
    private val toolkitWrapper = VerovioToolkitWrapper(resourcePath)

    var svgString = mutableStateOf("<svg></svg>")
    val fontOptions = listOf("Leipzig", "Bravura", "Leland", "Petaluma")

    private var viewSize by mutableStateOf(IntSize.Zero)
    private var currentPage by mutableStateOf(1)
    private var scaleIndex by mutableStateOf(3)
    private val scaleValues = listOf(50, 60, 80, 100, 150, 200)
    private var selectedFont = "Leipzig"

    fun destroyWrapper() {
        toolkitWrapper.release()
    }

    fun onPrevious() {
        if (currentPage > 1) {
            currentPage--
            svgString.value = toolkitWrapper.renderToSVG(currentPage)
        }
    }

    fun onNext() {
        if (currentPage < toolkitWrapper.getPageCount()) {
            currentPage++
            svgString.value = toolkitWrapper.renderToSVG(currentPage)
        }
    }

    fun onZoomIn() {
        if (scaleIndex < scaleValues.size - 1) {
            scaleIndex++
            applyZoom()
        }
    }

    fun onZoomOut() {
        if (scaleIndex > 0) {
            scaleIndex--
            applyZoom()
        }
    }

    fun onFontSelect(font: String) {
        if (selectedFont != font) {
            selectedFont = font
            applyFont()
        }
    }

    fun onSize(size: IntSize) {
        if (viewSize == size) return
        viewSize = size;
        applySize()
    }

    fun loadDefaultFile(context: Context) {
        val inputStream = context.assets.open("test-01.mei")
        val mei = inputStream.bufferedReader().use { it.readText() }
        toolkitWrapper.loadData(mei)
        svgString.value = toolkitWrapper.renderToSVG(1)
    }

    private fun applyFont() {
        val scaleOptionsJSON = """{"font": "$selectedFont"}"""
        toolkitWrapper.setOptions(scaleOptionsJSON)
        toolkitWrapper.redoLayout()
        if (toolkitWrapper.getPageCount() < currentPage) {
            currentPage = toolkitWrapper.getPageCount()
        }
        svgString.value = toolkitWrapper.renderToSVG(currentPage)
    }

    private fun applyZoom() {
        val scaleOptionsJSON = """{"scale": ${scaleValues[scaleIndex]}}"""
        toolkitWrapper.setOptions(scaleOptionsJSON)
        toolkitWrapper.redoLayout()
        if (toolkitWrapper.getPageCount() < currentPage) {
            currentPage = toolkitWrapper.getPageCount()
        }
        svgString.value = toolkitWrapper.renderToSVG(currentPage)
    }

    private fun applySize() {
        val height = 2100f * viewSize.height / viewSize.width
        val sizeJSON = """{"pageHeight": $height}"""
        toolkitWrapper.setOptions(sizeJSON)
        applyZoom()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithMenu(viewModel: ContentView) {
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