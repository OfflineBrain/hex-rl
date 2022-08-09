package game.entity.component

import com.offlinebrain.ecs.Component
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface HasMoveCost {
    val moveCost: MoveCost
    fun costToMove(other: HasMoveCost): MoveCost {
        return if (other.moveCost == NOT_REACHABLE) NOT_REACHABLE else (this.moveCost + other.moveCost) / 2
    }
}

@JvmInline
@Serializable
@SerialName("MoveCost")
value class MoveCost(val value: Double) : Component, Comparable<MoveCost> {
    operator fun plus(other: MoveCost): MoveCost = MoveCost(value + other.value)
    operator fun plus(other: Int): MoveCost = MoveCost(value + other)
    operator fun minus(other: MoveCost): MoveCost = MoveCost(value - other.value)
    operator fun times(other: MoveCost): MoveCost = MoveCost(value * other.value)
    operator fun div(other: MoveCost): MoveCost = MoveCost(value / other.value)
    operator fun div(other: Int): MoveCost = MoveCost(value / other)
    override operator fun compareTo(other: MoveCost): Int = value.compareTo(other.value)
    operator fun compareTo(other: Double): Int = value.compareTo(other)
}


val NONE = MoveCost(0.0)
val NOT_REACHABLE = MoveCost(Double.POSITIVE_INFINITY)
