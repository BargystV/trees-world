package com.bargystvelp.engine

import com.bargystvelp.World

abstract class Engine {
    abstract fun tick(world: World, delta: Float)
}
