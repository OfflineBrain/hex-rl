package algo

import hex.*
import kotlin.math.*

fun line(start: Hex, end: Hex): List<Hex> {
    fun lerp(a: Double, b: Double, t: Double): Double {
        return a + (b - a) * t
    }

    fun hexLerp(a: Hex, b: Hex, t: Double): Hex {
        val x = lerp(a.q.toDouble(), b.q.toDouble(), t)
        val y = lerp(a.r.toDouble(), b.r.toDouble(), t)

        var q = round(x).toInt()
        var r = round(y).toInt()
        var s = round(-x - y).toInt()

        val qDiff = abs(q - x)
        val rDiff = abs(r - y)
        val sDiff = abs(s - (-x - y))
        if (qDiff > rDiff && qDiff > sDiff) {
            q = -r - s;
        } else if (qDiff > sDiff) {
            r = -q - s;
        } else {
            s = -q - r;
        }
        return Hex(q, r, s)
    }


    val distance = start.distance(end)
    val result = mutableListOf<Hex>()
    for (i in 0..distance) {
        val t = i.toDouble() / distance.toDouble()
        result.add(hexLerp(start, end, t))
    }
    return result
}
