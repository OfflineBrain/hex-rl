package game.command

import com.offlinebrain.command.Command
import com.offlinebrain.ecs.Entity

data class CalculateFoV(val entity: Entity) : Command
