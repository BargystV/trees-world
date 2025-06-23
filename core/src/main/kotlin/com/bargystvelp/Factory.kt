package com.bargystvelp

import com.bargystvelp.component.Genome
import com.bargystvelp.component.Position
import com.bargystvelp.component.Vitality

object Factory {
    fun organism(position: Position): Entity = Entity()
        .add(position)
        .add(Vitality())
        .add(Genome())
}
