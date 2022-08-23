package game.command.handler

import com.offlinebrain.command.CommandHandler
import com.offlinebrain.command.CommandResult
import com.offlinebrain.command.Failure
import com.offlinebrain.command.Success
import com.offlinebrain.ecs.BaseMapQuery
import com.offlinebrain.ecs.ECSManager
import com.soywiz.korinject.AsyncInjector
import game.GameState
import game.command.CreatePlayer
import game.command.SpawnPlayer
import game.entity.component.Displayable
import game.entity.component.NOT_REACHABLE
import game.entity.component.Player
import game.entity.component.Tile
import game.map.AccessibilityMap
import hex.Hex

class PlayerHandler(private val ecs: ECSManager) : CommandHandler() {
    private val entitiesQuery = BaseMapQuery(
        key = Hex::class,
        include = setOf(Hex::class, Displayable::class),
        exclude = setOf(Tile::class)
    )

    init {
        ecs {
            register(entitiesQuery)
        }
        on(::create)
        on(::spawn)
    }

    private fun create(command: CreatePlayer): CommandResult {

        val player = ecs {
            create {
                add(Displayable("player"))
                add(Player)
            }
        }
        command.callback(player)

        return Success
    }

    private fun spawn(command: SpawnPlayer): CommandResult {
        val player = command.entity
        val level = GameState.level
        val version = GameState.levels[level]?.max() ?: 0
        val accessibilityMap = AccessibilityMap.get(level, version, ecs)

        val spawnPosition = accessibilityMap.data.asSequence()
            .filter { it.hex !in entitiesQuery.map.keys }
            .filter { it.moveCost != NOT_REACHABLE }
            .map { it.hex }
            .toSet().randomOrNull() ?: return Failure("No free spots to spawn entity: $player")

        ecs {
            player.add(spawnPosition)
        }

        command.callback(player)

        return Success
    }

    companion object {
        suspend operator fun invoke(injector: AsyncInjector): PlayerHandler {
            return PlayerHandler(
                injector.get(),
            )
        }
    }
}