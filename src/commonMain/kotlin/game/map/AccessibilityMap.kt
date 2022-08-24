package game.map

import cache.memoize
import com.offlinebrain.ecs.BaseQuery
import com.offlinebrain.ecs.ECSManager
import com.offlinebrain.ecs.Entity
import game.entity.component.HasMoveCost
import game.entity.component.HasTransparency
import game.entity.component.MoveCost
import game.entity.component.Transparency
import hex.HasHex
import hex.Hex

class AccessibilityMap<T>(val version: Int, val data: Set<T>) where T : HasHex, T : HasMoveCost {

    companion object {
        val accessibilityTileQuery = BaseQuery(include = setOf(Hex::class, MoveCost::class))


        private val cache = { _: Int, version: Int, ecs: ECSManager ->
            ecs {
                val map = accessibilityTileQuery.entities.asSequence().map {
                    AccessibilityTile(it, it.get()!!, it.get()!!, it.get()!!)
                }.toSet()
                AccessibilityMap(version, map)
            }
        }.memoize()

        fun get(level: Int, version: Int, ecs: ECSManager): AccessibilityMap<AccessibilityTile> {
            return cache(level, version, ecs)
        }
    }
}


data class AccessibilityTile(
    val entity: Entity,
    override val hex: Hex,
    override val moveCost: MoveCost,
    override val transparency: Transparency,
) : HasHex, HasMoveCost, HasTransparency