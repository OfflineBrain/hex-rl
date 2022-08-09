package hex

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import memoize.memoize
import kotlin.math.abs

@Serializable
class Hex private constructor(
    val q: Int,
    val r: Int,
    @Transient
    val s: Int = -q - r
) {
    init {
        assert(q + r + s == 0)
    }

    val x = q + (r / 2)
    val y = r

    val length = (abs(q) + abs(r) + abs(s)) / 2


    operator fun times(times: Int): Hex {
        return Hex(q * times, r * times)
    }

    operator fun plus(other: Hex): Hex {
        return Hex(q + other.q, r + other.r, s + other.s)
    }

    operator fun minus(other: Hex): Hex {
        return Hex(q - other.q, r - other.r, s - other.s)
    }

    fun distance(other: Hex): Int {
        return (this - other).length
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Hex

        if (q != other.q) return false
        if (r != other.r) return false

        return true
    }

    override fun hashCode(): Int {
        return q shl 16 or (r and 0xFFFF)
    }

    override fun toString(): String = "($q, $r)"

    companion object {
        val cache = { q: Int, r: Int, s: Int ->
            if (q + r + s == 0) {
                Hex(q, r, s)
            } else throw IllegalArgumentException("Invalid hex coordinates: $q, $r, $s")
        }.memoize()

        operator fun invoke(q: Int = 0, r: Int = 0, s: Int = -q - r): Hex {
            return cache(q, r, s)
        }

        fun ofXY(x: Int, y: Int): Hex {
            return invoke(x - (y / 2), y)
        }

        val ORIGIN = invoke(0, 0, 0)
        val UP_RIGHT = invoke(1, -1)
        val RIGHT = invoke(1, 0)
        val DOWN_RIGHT = invoke(0, 1)
        val DOWN_LEFT = invoke(-1, 1)
        val LEFT = invoke(-1, 0)
        val UP_LEFT = invoke(0, -1)

        val directions = listOf(UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN_LEFT, LEFT, UP_LEFT)
    }
}
