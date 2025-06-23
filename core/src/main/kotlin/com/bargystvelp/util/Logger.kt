package com.bargystvelp.util

object Logger {

    private const val RESET = "\u001B[0m"
    private const val RED = "\u001B[31m"
    private const val GREEN = "\u001B[32m"
    private const val CYAN = "\u001B[36m"

    fun debug(message: String) = log("DEBUG", message, CYAN)
    fun info(message: String) = log("INFO", message, GREEN)
    fun error(message: String, throwable: Throwable? = null) {
        log("ERROR", message, RED)
        throwable?.printStackTrace()
    }

    private fun log(level: String, message: String, color: String) {
        val (className, methodName) = getCallerInfo()
        val paddedLevel = "[${level.padEnd(5)}]"  // выравнивание только по тегу
        val location = "$className::$methodName"
        println("$color$paddedLevel $location: $message$RESET")
    }

    private fun getCallerInfo(): Pair<String, String> {
        val stackTrace = Throwable().stackTrace
        val caller = stackTrace.firstOrNull {
            it.className != Logger::class.java.name && !it.className.contains("kotlin.io.")
        }
        return if (caller != null) {
            Pair(caller.className.substringAfterLast('.'), caller.methodName)
        } else {
            Pair("UnknownClass", "unknownMethod")
        }
    }
}
