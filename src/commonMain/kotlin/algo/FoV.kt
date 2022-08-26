package algo

import cache.memoize
import game.entity.component.HasTransparency
import game.entity.component.Transparency
import hex.Hex

fun <V> fov(
    map: Map<Hex, V>,
    center: Hex,
    radius: Int
): Map<Hex, Int> where V : HasTransparency {
    val fovLines = fovLinesCache(radius + 1)
    val result = mutableMapOf<Hex, Int>()
    fovLines.values.forEach {
        it.forEach { line ->
            var previous = center
            line.map { hex -> hex + center }.takeWhile { hex ->
                val canSee = map[previous]?.transparency != Transparency(false)
                previous = hex
                canSee
            }.associateWith { hex ->
                center.distance(hex)
            }.also { visibleLine ->
                result.putAll(visibleLine)
            }
        }
    }

    return result
}

private val fovLinesCache: (Int) -> Map<Int, Set<List<Hex>>> = { radius: Int ->
    fovLines(radius)
}.memoize()

private fun fovLines(radius: Int): MutableMap<Int, MutableSet<List<Hex>>> {
    val lines = mutableMapOf<Int, MutableSet<List<Hex>>>()
    val visited = mutableSetOf<Hex>()
    for (i in radius downTo 1) {

        val ring = ring(Hex.ORIGIN, i)
        ring.forEach {
            val pointDirection = pointDirection(Hex.ORIGIN, it)
            val line = line(Hex.ORIGIN, it).takeIf { !visited.containsAll(it) } ?: emptyList()
            visited.addAll(line)
            pointDirection to line

            val notRepeat =
                lines.getOrElse(pointDirection) { mutableSetOf() }
                    .filterNot { previous -> line.containsAll(previous) }
                    .toMutableList()
            notRepeat.add(line)
            lines[pointDirection] = notRepeat.toMutableSet()
        }
    }
    return lines
}

private fun pointDirection(center: Hex, point: Hex): Int {
    val x = point.q
    val y = point.r
    val q = x < center.q
    val r = y < center.r
    val s = -x - y < center.s
    return if (q)
        if (r) 0
        else if (s) 1
        else 2
    else
        if (r) 3
        else if (s) 4
        else 5
}
