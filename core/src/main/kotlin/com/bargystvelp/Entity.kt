package com.bargystvelp

import com.bargystvelp.component.Component

class Entity {
    private val data = HashMap<Class<out Component>, Component>()
    fun <T : Component> add(c: T): Entity = apply { data[c.javaClass] = c }
    fun <T : Component> get(type: Class<T>): T? = data[type] as? T
    fun <T : Component> has(type: Class<T>) = data.containsKey(type)
}
