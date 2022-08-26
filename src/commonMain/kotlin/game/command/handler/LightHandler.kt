package game.command.handler

import algo.fov
import com.offlinebrain.command.CommandHandler
import com.offlinebrain.command.CommandResult
import com.offlinebrain.command.Failure
import com.offlinebrain.command.Success
import com.offlinebrain.ecs.ECSManager
import com.soywiz.korinject.AsyncInjector
import game.GameState
import game.command.CalculateLight
import game.entity.component.Light
import game.entity.component.LightSource
import game.entity.component.levels
import game.entity.component.lightQuery
import game.map.AccessibilityMap
import hex.Hex

class LightHandler(private val ecs: ECSManager) : CommandHandler() {

    init {
        on(::calculateLights)
    }

    private fun calculateLights(command: CalculateLight): CommandResult {
        val source = command.source
        val level = GameState.level
        val version = GameState.levels[level]?.max() ?: 0
        val map = AccessibilityMap.get(level, version, ecs).data.associateBy { it.hex }

        val lightSource = ecs { source.get<LightSource>() } ?: return Failure("Entity $source is not a Light Source")
        val position = ecs { source.get<Hex>() } ?: return Failure("Light Source $source does not have position")


        val oldLights = lightQuery[source]
        ecs.destroyAll(oldLights)


        val fov = fov(map, position, lightSource.radius)
        val lightLevels = lightSource.levels()

        fov.forEach { (hex, distance) ->
            ecs {
                lightLevels[distance]?.also {
                    map[hex]?.entity?.add(Light(it, source))
                }
            }
        }

        return Success
    }

    companion object {
        suspend operator fun invoke(injector: AsyncInjector): LightHandler {
            return LightHandler(
                injector.get()
            )
        }
    }
}