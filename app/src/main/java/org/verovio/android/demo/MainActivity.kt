package org.verovio.android.demo

import android.util.Log
import java.io.File
import java.io.InputStream
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import org.verovio.android.demo.ui.theme.VerovioMEIViewerTheme
import android.content.res.AssetManager

class MainActivity : ComponentActivity() {
    private lateinit var contentView: ContentView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        System.loadLibrary("verovio-android")

        enableEdgeToEdge()

        setContent {

            val resourcePath = "${applicationContext.filesDir.absolutePath}/verovio/data"
            val targetDir = File(applicationContext.filesDir, "verovio/data")
            copyAssetFolder(applicationContext.assets, "verovio/data", targetDir)

            contentView = ContentView(resourcePath)
            contentView.loadDefaultFile(applicationContext)

            VerovioMEIViewerTheme {
                Scaffold(
                    topBar = {
                        TopAppBarWithMenu(contentView)
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        SvgWebView(svgContent = contentView.svgString.value, onSizeChanged = contentView::onSize)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        contentView.destroyWrapper()
        super.onDestroy()
    }

    fun copyAssetFolder(assetManager: AssetManager, fromAssetPath: String, toPath: File) {
        val files = assetManager.list(fromAssetPath) ?: return
        if (!toPath.exists()) toPath.mkdirs()
        for (filename in files) {
            val assetPath = "$fromAssetPath/$filename"
            val outFile = File(toPath, filename)
            if (assetManager.list(assetPath)?.isNotEmpty() == true) {
                // It's a folder
                copyAssetFolder(assetManager, assetPath, outFile)
            } else {
                assetManager.open(assetPath).use { input ->
                    outFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }
}