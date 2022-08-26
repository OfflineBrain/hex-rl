package game.entity.component

import hex.Hex

data class Vision(val radius: Int) : SComponent()

data class FoV(val visible: Set<Hex>) : SComponent()
