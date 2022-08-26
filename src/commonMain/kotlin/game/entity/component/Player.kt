package game.entity.component

import com.offlinebrain.ecs.toInclusiveQuery

object Player : SComponent()

val playerQuery = setOf(Player::class).toInclusiveQuery()