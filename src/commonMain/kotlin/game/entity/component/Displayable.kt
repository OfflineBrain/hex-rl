package game.entity.component

import com.offlinebrain.ecs.BaseMapQuery
import hex.Hex

data class Displayable(val type: String) : SComponent()

val displayableQuery by lazy { BaseMapQuery(key = Hex::class, include = setOf(Hex::class, Displayable::class)) }