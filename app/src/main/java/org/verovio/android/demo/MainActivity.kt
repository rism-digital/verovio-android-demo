package org.verovio.android.demo

import java.io.File
import java.io.FileOutputStream

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

import org.verovio.android.demo.ui.theme.VerovioMEIViewerTheme

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
    // State of the font dropdown and the about dialog
    var showFontDropdown by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Create a launcher to pick files
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                // Since Verovio does not support URIs
                val tempFile = copyUriToTempFile(context, it)
                tempFile?.let { file ->
                    // Now we can pass it to Verovio
                    viewModel.onLoadFile(file.absolutePath)
                }
            }
        }
    )

    TopAppBar(
        title = { },
        actions = {
            IconButton(onClick = {
                viewModel.onPrevious() },
                enabled = viewModel.canPrevious()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous"
                )
            }
            IconButton(
                onClick = { viewModel.onNext() },
                enabled = viewModel.canNext()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
            IconButton(
                onClick = { viewModel.onZoomOut() },
                enabled = viewModel.canZoomOut()
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomOut,
                    contentDescription = "Zoom Out"
                )
            }
            IconButton(
                onClick = { viewModel.onZoomIn() },
                enabled = viewModel.canZoomIn()
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = "Zoom In"
                )
            }
            Box {
                IconButton(onClick = { showFontDropdown = true }) {
                    Text("Font")
                }
                DropdownMenu(
                    expanded = showFontDropdown,
                    onDismissRequest = { showFontDropdown = false }
                ) {
                    viewModel.fontOptions.forEach { font ->
                        DropdownMenuItem(
                            text = { Text(font) },
                            onClick = {
                                viewModel.onFontSelect(font)
                                showFontDropdown = false
                            }
                        )
                    }
                }
            }
            // New button for file picker
            TextButton(onClick = {
                // Launch file picker for MEI, MusicXML, or MXL mime types
                launcher.launch(arrayOf(
                    "application/vnd.recordare.musicxml",
                    "application/xml",
                    "text/xml",
                    "application/octet-stream",  // For .mxl binary zipped files
                    "application/x-zip-compressed" // Sometimes .mxl is zipped
                ))
            }) {
                Text("Load File")
            }

            IconButton(onClick = { showAboutDialog = true }) {
                Icon(Icons.Default.Info, contentDescription = "About")
            }
        }
    )

    AboutDialog(show = showAboutDialog, onDismiss = { showAboutDialog = false }, version = viewModel.getVersion())
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

@Composable
fun AboutDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    version: String
) {
    if (show) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            title = { Text("About") },
            text = {
                val msg = """Verovio version $version"""
                Text(msg)
            }
        )
    }
}

private fun copyUriToTempFile(context: Context, uri: Uri): File? {
    return try {
        val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "tempfile"
        val tempFile = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}