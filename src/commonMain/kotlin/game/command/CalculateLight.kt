package game.command

import com.offlinebrain.command.Command
import com.offlinebrain.ecs.Entity

data class CalculateLight(val source: Entity) : Command
