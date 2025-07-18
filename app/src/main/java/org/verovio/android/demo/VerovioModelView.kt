package org.verovio.android.demo


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.content.Context
import android.content.res.AssetManager
import androidx.compose.ui.unit.IntSize
import java.io.File

class VerovioModelView() : ViewModel() {

    // Native wrapper instance
    private var initialized = false
    private lateinit var resourcePath: String
    private lateinit var toolkitWrapper: VerovioToolkitWrapper

    var svgString = mutableStateOf("<svg></svg>")
    val fontOptions = listOf("Leipzig", "Bravura", "Leland", "Petaluma")

    private var viewSize by mutableStateOf(IntSize.Zero)
    private var currentPage by mutableStateOf(1)
    private var scaleIndex by mutableStateOf(3)
    private val scaleValues = listOf(50, 60, 80, 100, 150, 200)
    private var selectedFont = "Leipzig"

    fun initIfNeeded(context: Context) {
        if (initialized) return
        initialized = true

        // Copy assets
        val targetDir = File(context.filesDir, "verovio/data")
        resourcePath = "${context.filesDir.absolutePath}/verovio/data"
        copyAssetFolder(context.assets, "verovio/data", targetDir)

        // Init toolkit and load MEI
        toolkitWrapper = VerovioToolkitWrapper(resourcePath)
        loadDefaultFile(context)
    }

    override fun onCleared() {
        super.onCleared()
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

    private fun copyAssetFolder(assetManager: AssetManager, fromAssetPath: String, toPath: File) {
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