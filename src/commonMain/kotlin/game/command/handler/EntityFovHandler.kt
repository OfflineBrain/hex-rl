package game.command.handler

import algo.fov
import com.offlinebrain.command.CommandHandler
import com.offlinebrain.command.CommandResult
import com.offlinebrain.command.Failure
import com.offlinebrain.command.Success
import com.offlinebrain.ecs.ECSManager
import com.soywiz.korinject.AsyncInjector
import game.GameState
import game.GameState.level
import game.command.CalculateFoV
import game.entity.component.FoV
import game.entity.component.Light
import game.entity.component.Vision
import game.map.AccessibilityMap
import hex.Hex

class EntityFovHandler(val ecs: ECSManager) : CommandHandler() {

    init {
        on(::calculateFov)
    }

    private fun calculateFov(command: CalculateFoV): CommandResult {
        val entity = command.entity
        val vision = ecs { entity.get<Vision>() }?.radius ?: return Failure("Entity $entity does not have vision")
        val position = ecs { entity.get<Hex>() } ?: return Failure("Entity $entity does not have position")

        val map = AccessibilityMap.get(level, GameState.levels[level]?.max() ?: 0, ecs).data.associateBy { it.hex }
        val fov = fov(map, position, vision)

        val visibleFov = fov.keys.filter { hex ->
            ecs { map[hex]!!.entity.get<Light>() } != null
        }.toSet()

        ecs {
            entity.add(FoV(visibleFov))
        }

        return Success
    }

    companion object {
        suspend operator fun invoke(injector: AsyncInjector): EntityFovHandler {
            return EntityFovHandler(
                injector.get()
            )
        }
    }
}