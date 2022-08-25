package game.entity.component

import com.offlinebrain.ecs.BaseMapQuery
import hex.Hex


data class Tile(val type: String) : SComponent()

val tileQuery by lazy { BaseMapQuery(key = Hex::class, include = setOf(Hex::class, Tile::class)) }