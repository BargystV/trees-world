package com.bargystvelp.common

/** Размер в клетках или пикселях (ширина × высота). */
data class Size(
    val width: Int,
    val height: Int
)

/** Поэлементное деление: width/size.width, height/size.height. */
fun Size.div(size: Size): Size {
    return Size(width = width.div(size.width), height = height.div(size.height))
}

/** Поэлементное умножение: width*size.width, height*size.height. */
fun Size.times(size: Size): Size {
    return Size(width = width.times(size.width), height = height.times(size.height))
}
