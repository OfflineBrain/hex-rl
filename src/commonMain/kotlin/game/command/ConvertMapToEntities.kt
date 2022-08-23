package game.command

import com.offlinebrain.command.Command
import com.offlinebrain.ecs.Entity
import game.map.generation.CellType
import hex.Hex

data class ConvertMapToEntities(val map: Map<Hex, CellType>, val callback: (Entity) -> Unit) : Command