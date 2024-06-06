package com.ravenl.htmlannotator.core.util

expect fun defaultLogger(level: Logger.Level = Logger.Level.INFO): Logger

class Logger constructor(
    level: Level = Level.INFO,
    private val proxy: Proxy
) {

    companion object {
        const val TAG = "HtmlAnnotator"
    }


    var level: Level = level
        set(value) {
            if (value != field) {
                val oldLevel = field
                field = value
                val newLevel = value.name
                w(TAG, "Logger. setLevel. $oldLevel -> $newLevel")
            }
        }

    fun isLoggable(level: Level): Boolean {
        return level >= this.level
    }


    fun v(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.VERBOSE)) {
            proxy.v(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun v(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.VERBOSE)) {
            proxy.v(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun d(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.DEBUG)) {
            proxy.d(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun d(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.DEBUG)) {
            proxy.d(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun i(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.INFO)) {
            proxy.i(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun i(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.INFO)) {
            proxy.i(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun w(module: String, msg: String) {
        if (isLoggable(Level.WARNING)) {
            proxy.w(TAG, joinModuleAndMsg(module, msg), null)
        }
    }

    fun w(module: String, throwable: Throwable?, msg: String) {
        if (isLoggable(Level.WARNING)) {
            proxy.w(TAG, joinModuleAndMsg(module, msg), throwable)
        }
    }

    fun w(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.WARNING)) {
            proxy.w(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun w(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.WARNING)) {
            proxy.w(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun e(module: String, msg: String) {
        if (isLoggable(Level.ERROR)) {
            proxy.e(TAG, joinModuleAndMsg(module, msg), null)
        }
    }

    fun e(module: String, throwable: Throwable?, msg: String) {
        if (isLoggable(Level.ERROR)) {
            proxy.e(TAG, joinModuleAndMsg(module, msg), throwable)
        }
    }

    fun e(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.ERROR)) {
            proxy.e(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun e(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.ERROR)) {
            proxy.e(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun flush() {
        proxy.flush()
    }


    private fun joinModuleAndMsg(module: String?, msg: String): String = buildString {
        if (module?.isNotEmpty() == true) {
            append(module)
            if (isNotEmpty()) append(". ")
        }

        append(msg)
    }

    override fun toString(): String =
        "Logger(level=$level,proxy=$proxy)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Logger) return false

        if (proxy != other.proxy) return false
        if (level != other.level) return false

        return true
    }

    override fun hashCode(): Int {
        var result = proxy.hashCode()
        result = 31 * result + level.hashCode()
        return result
    }


    enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        NONE,
    }

    interface Proxy {
        fun v(tag: String, msg: String, tr: Throwable?)
        fun d(tag: String, msg: String, tr: Throwable?)
        fun i(tag: String, msg: String, tr: Throwable?)
        fun w(tag: String, msg: String, tr: Throwable?)
        fun e(tag: String, msg: String, tr: Throwable?)
        fun flush()
    }
}