package org.verovio.android.demo

class VerovioToolkitWrapper(private var ptr: Long = 0) {
    init {
        ptr = constructor()
    }

    fun getVersion(): String = GetVersion(ptr)

    fun release() {
        if (ptr != 0L) {
            destructor(ptr)
            ptr = 0L
        }
    }

    private external fun constructor(): Long
    @Suppress("FunctionName")
    private external fun GetVersion(ptr: Long): String
    private external fun destructor(ptr: Long)
}