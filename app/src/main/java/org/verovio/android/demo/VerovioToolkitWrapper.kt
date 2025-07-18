package org.verovio.android.demo

class VerovioToolkitWrapper(resourcePath: String) {

    private var toolkitOld: Long = 0

    init {
        //toolkit = constructor(resourcePath)


    }

    fun release() {
        if (toolkitOld != 0L) {
            destructor(toolkitOld)
            toolkitOld = 0L
        }
    }


    fun getVersion(): String = GetVersion(toolkitOld)
    fun getPageCount(): Int = GetPageCount(toolkitOld)
    fun loadData(data: String): Boolean = LoadData(toolkitOld, data)
    fun renderToSVG(page: Int): String = RenderToSVG(toolkitOld, page)
    fun redoLayout() = RedoLayout(toolkitOld)
    fun setOptions(jsonOptions: String): Boolean = SetOptions(toolkitOld, jsonOptions)

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