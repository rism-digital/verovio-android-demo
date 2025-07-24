package org.verovio.android.demo

import java.io.File

import android.content.Context
import android.content.res.AssetManager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel

import org.verovio.lib.toolkit

class VerovioModelView : ViewModel() {

    // Native wrapper instance
    private var initialized = false
    private lateinit var resourcePath: String
    private lateinit var toolkit: toolkit

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
        toolkit = toolkit(false)
        toolkit.setResourcePath(resourcePath)
        toolkit.setOptions("{'svgViewBox': 'true'}")
        toolkit.setOptions("{'scaleToPageSize': 'true'}")
        toolkit.setOptions("{'adjustPageHeight': 'true'}")

        loadDefaultFile(context)
    }

    override fun onCleared() {
        super.onCleared()
        toolkit.delete()
    }

    fun canPrevious(): Boolean { return (currentPage > 1) }
    fun canNext(): Boolean { return (currentPage < toolkit.getPageCount()) }
    fun canZoomOut(): Boolean { return (scaleIndex > 1) }
    fun canZoomIn(): Boolean { return (scaleIndex < scaleValues.size - 1) }
    fun getVersion(): String { return toolkit.getVersion() }

    fun onPrevious() {
        if (canPrevious()) {
            currentPage--
            svgString.value = toolkit.renderToSVG(currentPage)
        }
    }

    fun onNext() {
        if (canNext()) {
            currentPage++
            svgString.value = toolkit.renderToSVG(currentPage)
        }
    }

    fun onZoomOut() {
        if (canZoomOut()) {
            scaleIndex--
            applyZoom()
        }
    }

    fun onZoomIn() {
        if (canZoomIn()) {
            scaleIndex++
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
        viewSize = size
        applySize()
    }

    fun onLoadFile(filename: String) {
        if (toolkit.loadFile(filename)) {
            currentPage = 1
            updateSvg()
        }
    }

    private fun loadDefaultFile(context: Context) {
        val inputStream = context.assets.open("test-01.mei")
        val mei = inputStream.bufferedReader().use { it.readText() }
        toolkit.loadData(mei)
        updateSvg()
    }

    private fun applyFont() {
        val scaleOptionsJSON = """{"font": "$selectedFont"}"""
        toolkit.setOptions(scaleOptionsJSON)
        toolkit.redoLayout()
        if (toolkit.getPageCount() < currentPage) {
            currentPage = toolkit.getPageCount()
        }
        updateSvg()
    }

    private fun applySize() {
        val height = 2100f * viewSize.height / viewSize.width
        val sizeJSON = """{"pageHeight": $height}"""
        toolkit.setOptions(sizeJSON)
        applyZoom()
    }

    private fun applyZoom() {
        val scaleOptionsJSON = """{"scale": ${scaleValues[scaleIndex]}}"""
        toolkit.setOptions(scaleOptionsJSON)
        toolkit.redoLayout()
        if (toolkit.getPageCount() < currentPage) {
            currentPage = toolkit.getPageCount()
        }
        updateSvg()
    }

    private fun updateSvg() {
        svgString.value = toolkit.renderToSVG(currentPage)
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