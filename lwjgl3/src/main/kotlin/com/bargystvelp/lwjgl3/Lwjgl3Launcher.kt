package com.bargystvelp.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter
import com.bargystvelp.Main
import org.jetbrains.annotations.Contract
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack

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
        get() = Lwjgl3ApplicationConfiguration().apply {
            setTitle("TreesWorld")
            useVsync(true)
            setForegroundFPS(60)
            setDecorated(false)          // нужна рамка
            setResizable(false)

            setWindowListener(object : Lwjgl3WindowAdapter() {
                override fun created(window: Lwjgl3Window) {

                    /* 1 ─ work-area монитора */
                    val stack = MemoryStack.stackPush()
                    try {
                        val wx = stack.mallocInt(1)
                        val wy = stack.mallocInt(1)
                        val ww = stack.mallocInt(1)
                        val wh = stack.mallocInt(1)

                        val monitor = GLFW.glfwGetPrimaryMonitor()
                        GLFW.glfwGetMonitorWorkarea(monitor, wx, wy, ww, wh)

                        val handle = window.windowHandle
                        /* 2 ─ ставим наружный размер = work-area (пока ещё не знаем толщину рамки) */
                        GLFW.glfwSetWindowPos  (handle, wx[0], wy[0])
                        GLFW.glfwSetWindowSize (handle, ww[0], wh[0])

                        /* 3 ─ теперь рамка создана → можем померить её толщину */
                        val left   = stack.mallocInt(1)
                        val top    = stack.mallocInt(1)
                        val right  = stack.mallocInt(1)
                        val bottom = stack.mallocInt(1)
                        GLFW.glfwGetWindowFrameSize(handle, left, top, right, bottom)

                        /* 4 ─ уменьшаем окно на толщину рамки,
                               чтобы «наружка» уложилась в work-area */
                        val outerW = ww[0] - left[0] - right[0]
                        val outerH = wh[0] - top[0]  - bottom[0]
                        GLFW.glfwSetWindowSize(handle, outerW, outerH)
                        GLFW.glfwSetWindowPos  (handle,
                            wx[0] + left[0],
                            wy[0] + top[0])
                    } finally {
                        stack.close()
                    }
                }
            })
        }
}
