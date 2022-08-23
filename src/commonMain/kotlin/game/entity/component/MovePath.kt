package game.entity.component

import com.offlinebrain.ecs.Component
import com.offlinebrain.ecs.Entity
import hex.Hex

data class MovePath(val path: List<Hex>) : SComponent()

data class MovePathNode(val parent: Entity) : SComponent()
