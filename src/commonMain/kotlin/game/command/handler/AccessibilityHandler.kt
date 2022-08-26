package game.command.handler

import algo.accessibility
import algo.path
import com.offlinebrain.command.CommandHandler
import com.offlinebrain.command.CommandResult
import com.offlinebrain.command.Failure
import com.offlinebrain.command.Success
import com.offlinebrain.ecs.ECSManager
import com.offlinebrain.ecs.Entity
import com.soywiz.korinject.AsyncInjector
import game.GameState
import game.command.BuildAccessibility
import game.command.BuildPath
import game.entity.component.AccessibilityPoint
import game.entity.component.Displayable
import game.entity.component.MovePath
import game.entity.component.MovePathNode
import game.entity.component.MovementSpeed
import game.map.AccessibilityMap
import hex.Hex
import utils.logger

class AccessibilityHandler
    (
    private val ecs: ECSManager,
) : CommandHandler() {
    private val log by logger()

    init {
        on(::buildPath)
        on(::buildAccessibility)
    }

    private fun buildAccessibility(command: BuildAccessibility): CommandResult {
        val entity = command.entity
        val level = GameState.level
        val version = GameState.levels[level]?.max() ?: 0
        val accessibilityMap = AccessibilityMap.get(level, version, ecs)
        if (accessibilityMap.data.isEmpty()) {
            return Failure("No accessibility map found for level $level version $version")
        }

        val speed = ecs {
            entity.get<MovementSpeed>()
        }?.speed ?: return Failure("Entity $entity doesn't have movement speed and cannot move")
        val position = ecs {
            entity.get<Hex>()
        } ?: return Failure("Entity $entity doesn't have position")

        val accessibility = accessibility(accessibilityMap.data.associateBy { it.hex }, position, speed.toDouble())

        val accessibilityPoints = mutableSetOf<Entity>()

        accessibility.forEach { hex ->
            ecs {
                accessibilityPoints.add(create {
                    add(hex)
                    add(AccessibilityPoint(entity))
                    add(Displayable.access)
                })
            }
        }

        command.callback(accessibilityPoints)

        return Success
    }

    private fun buildPath(command: BuildPath): CommandResult {
        log.info { "Building path for entity ${command.entity}" }
        val level = GameState.level
        val version = GameState.levels[level]?.max() ?: 0
        val accessibilityMap = AccessibilityMap.get(level, version, ecs)
        if (accessibilityMap.data.isEmpty()) {
            return Failure("No accessibility map found for level $level version $version")
        }

        val entity = command.entity
        return ecs {
            val position = entity.get<Hex>() ?: return@ecs Failure("Entity has no position")
            val destination = command.destination

            val path = path(accessibilityMap.data.associateBy { it.hex }, position, destination)
            if (path.isEmpty()) {
                return@ecs Failure("No path found")
            }

            entity.add(MovePath(path))
            path.forEach {
                create {
                    add(MovePathNode(entity))
                    add(it)
                }
            }
            log.info { "Path built, length: ${path.size}" }

            return@ecs Success
        }
    }

    private fun destroyPath(): CommandResult {
        return Success
    }

    companion object {
        suspend operator fun invoke(injector: AsyncInjector): AccessibilityHandler {
            return AccessibilityHandler(
                injector.get(),
            )
        }
    }
}