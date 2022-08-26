package game.entity.component

import com.offlinebrain.ecs.BaseMapQuery
import hex.Hex

data class Displayable private constructor(val type: String) : SComponent() {
    companion object {
        val player = Displayable("player")
        val monster = Displayable("monster")
        val path = Displayable("path")
        val access = Displayable("access")
    }
}

val displayableQuery by lazy { BaseMapQuery(key = Hex::class, include = setOf(Hex::class, Displayable::class)) }