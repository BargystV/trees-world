package com.bargystvelp.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.bargystvelp.Main
import org.jetbrains.annotations.Contract

/** Launches the desktop (LWJGL3) application.  */
object Lwjgl3Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        if (StartupHelper.startNewJvmIfRequired()) return  // This handles macOS support and helps on Windows.

        createApplication()
    }

    @Contract(" -> new")
    private fun createApplication(): Lwjgl3Application {
        return Lwjgl3Application(Main(), defaultConfiguration)
    }

    private val defaultConfiguration: Lwjgl3ApplicationConfiguration
        get() {
            val displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode()

            return Lwjgl3ApplicationConfiguration().apply {
                setTitle("SimulationOfLife")
                useVsync(true)
                setForegroundFPS(60)
                setWindowedMode(displayMode.width, displayMode.height) // окно во весь экран
                setDecorated(false) // убирает рамки окна
                setResizable(false)
            }
        }
}
