package game.command.handler

import com.offlinebrain.command.CommandHandler
import com.offlinebrain.command.CommandResult
import com.offlinebrain.command.Success
import com.offlinebrain.ecs.ECSManager
import com.soywiz.korinject.AsyncInjector
import game.command.ConvertMapToEntities
import game.entity.component.MoveCost
import game.entity.component.Tile
import game.entity.component.Transparency
import game.map.generation.CellType
import utils.logger

class EntityHandler(
    private val ecs: ECSManager,
) : CommandHandler() {
    val log by logger()

    init {
        on(::createEntities)
    }

    private suspend fun createEntities(command: ConvertMapToEntities): CommandResult {
        log.info { "Creating entities, ${command.map.size}" }
        ecs {
            command.map.forEach { (hex, cellType) ->
                val entity = create {
                    add(hex)
                    when (cellType) {
                        CellType.WALL -> {
                            add(Tile("wall"))
                            add(MoveCost(Double.POSITIVE_INFINITY))
                            add(Transparency(false))
                        }

                        CellType.FLOOR -> {
                            add(Tile("floor"))
                            add(MoveCost(1.0))
                            add(Transparency(true))
                        }

                        CellType.DOOR -> {
                            add(Tile("door"))
                            add(MoveCost(2.0))
                            add(Transparency(true))
                        }

                        CellType.LIGHT_CRYSTAL -> {
                            add(Tile("light_crystal"))
                            add(MoveCost(Double.POSITIVE_INFINITY))
                            add(Transparency(false))
                        }

                        CellType.VOID -> {
                            add(Tile("void"))
                            add(MoveCost(Double.POSITIVE_INFINITY))
                            add(Transparency(false))
                        }
                    }
                }
                command.callback(entity)
            }
        }
        return Success
    }

    companion object {
        suspend operator fun invoke(injector: AsyncInjector): EntityHandler {
            return EntityHandler(injector.get())
        }
    }
}