package com.bargystvelp.logic.ecosystem

import com.bargystvelp.logic.common.EnergyGenerator
import com.bargystvelp.logic.common.Size

class Sun(val size: Size): EnergyGenerator {

    var energy = 0

    override fun tick() {
        energy += size.height + size.width
    }
}
