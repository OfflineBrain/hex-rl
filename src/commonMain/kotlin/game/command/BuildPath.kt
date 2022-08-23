package game.command

import com.offlinebrain.command.Command
import com.offlinebrain.ecs.Entity
import hex.Hex

data class BuildPath(val entity: Entity, val destination: Hex) : Command
