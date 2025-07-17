package org.verovio.android.demo

class VerovioToolkitWrapper(resourcePath: String) {

    private var toolkit: Long = 0

    init {
        toolkit = constructor(resourcePath)
    }

    fun release() {
        if (toolkit != 0L) {
            destructor(toolkit)
            toolkit = 0L
        }
    }

    fun getVersion(): String = GetVersion(toolkit)
    fun getPageCount(): Int = GetPageCount(toolkit)
    fun loadData(data: String): Boolean = LoadData(toolkit, data)
    fun renderToSVG(page: Int): String = RenderToSVG(toolkit, page)
    fun redoLayout() = RedoLayout(toolkit)
    fun setOptions(jsonOptions: String): Boolean = SetOptions(toolkit, jsonOptions)

    private external fun constructor(resourcePath: String): Long
    private external fun destructor(ptr: Long)

    @Suppress("FunctionName")
    private external fun GetVersion(ptr: Long): String
    @Suppress("FunctionName")
    private external fun GetPageCount(ptr: Long): Int
    @Suppress("FunctionName")
    private external fun LoadData(ptr: Long, data: String): Boolean
    @Suppress("FunctionName")
    private external fun RedoLayout(ptr: Long)
    @Suppress("FunctionName")
    private external fun RenderToSVG(ptr: Long, page: Int): String
    @Suppress("FunctionName")
    private external fun SetOptions(ptr: Long, jsonOptions: String): Boolean

}