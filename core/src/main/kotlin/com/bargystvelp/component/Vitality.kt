package com.bargystvelp.component

private const val REPRODUCTION_THRESHOLD = 2000

data class Vitality(var energy: Int = 100, var age: Int = 0, var isDead: Boolean = false): Component

fun Vitality.readyToReproduce(): Boolean = energy > REPRODUCTION_THRESHOLD
