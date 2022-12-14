package game.command

import com.offlinebrain.command.Command
import com.offlinebrain.ecs.Entity

data class SetTileTexture(val entity: Entity) : Command

data class SetEntityTexture(val entity: Entity) : Command