package utils

import com.soywiz.klogger.*
import com.soywiz.klogger.Logger.Level
import com.soywiz.korio.async.*
import kotlinx.coroutines.*

class AsyncLogger(private val logger: Logger) {

    companion object {
        operator fun invoke(name: String) = AsyncLogger(Logger(name))

        operator fun invoke(logger: Logger) = AsyncLogger(logger)

        inline operator fun <reified T : Any> invoke() = Logger.invoke(T::class.simpleName ?: "NoClassName")
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun log(level: Level, msg: () -> Any?) {
        GlobalScope.launchImmediately { logger.log(level, msg) }
    }

    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.FATAL] */
    fun fatal(msg: () -> Any?) = log(Level.FATAL, msg)

    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.ERROR] */
    fun error(msg: () -> Any?) = log(Level.ERROR, msg)

    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.WARN] */
    fun warn(msg: () -> Any?) = log(Level.WARN, msg)

    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.INFO] */
    fun info(msg: () -> Any?) = log(Level.INFO, msg)

    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.DEBUG] */
    fun debug(msg: () -> Any?) = log(Level.DEBUG, msg)

    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.TRACE] */
    fun trace(msg: () -> Any?) = log(Level.TRACE, msg)
}
