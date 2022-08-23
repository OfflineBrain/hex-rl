package game.command

import com.offlinebrain.command.Command
import com.offlinebrain.ecs.Entity

class CreatePlayer(val callback: (Entity) -> Unit = {}) : Command

class SpawnPlayer(val entity: Entity, val callback: (Entity) -> Unit = {}) : Command