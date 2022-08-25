package game.entity.component

import cache.memoize
import com.offlinebrain.ecs.BaseQuery
import com.offlinebrain.ecs.ECSManager
import com.offlinebrain.ecs.Entity
import com.soywiz.korim.color.RGB

data class LightSource(val level: UByte, val radius: Int) : SComponent()

private val levels = { source: LightSource ->
    val step = source.level / source.radius.toUInt()
    val result = mutableMapOf(0 to source.level)
    if (source.radius > 0) {
        for (i in 1..source.radius) {
            result[i] = (source.level - (step * (i - 1).toUByte())).toUByte()
        }
    }
    result
}.memoize()

fun LightSource.levels() = levels(this)

data class Light(val level: UByte, val source: Entity) : SComponent()

fun Light.toMux() = RGB(level.toInt(), level.toInt(), level.toInt())

class LightQuery private constructor() : BaseQuery(include = setOf(Light::class)) {
    private val sourceToLight = mutableMapOf<Entity, MutableSet<Entity>>()
    private lateinit var sourceFn: (Entity) -> Entity

    override fun offer(entity: Entity, removeIfApplicable: Boolean): Boolean {
        if (requiredMatch(entity)) {
            _entities.add(entity)
            val source = sourceFn(entity)

            return sourceToLight.getOrPut(source) { mutableSetOf() }.add(entity)
        } else if (removeIfApplicable) {
            _entities.remove(entity)
            val source = sourceFn(entity)

            val lights = sourceToLight[source]
            val result = lights?.remove(entity) ?: false
            if (lights != null && lights.isEmpty()) {
                sourceToLight.remove(source)
            }
            return result
        }
        return false
    }

    operator fun get(source: Entity): Set<Entity> = sourceToLight[source] ?: emptySet()

    override fun ECSManager.init() {
        includeMappers.addAll(include.mapNotNull { this.mapper(it) })
        excludeMappers.addAll(exclude.mapNotNull { this.mapper(it) })
        sourceFn = { entity: Entity ->
            invoke {
                entity.get<Light>()!!.source
            }
        }
    }

    companion object {
        private val query by lazy { LightQuery() }
        operator fun invoke() = query
    }
}