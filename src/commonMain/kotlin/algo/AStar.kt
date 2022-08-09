package algo

import game.entity.component.HasMoveCost
import game.entity.component.NONE
import game.entity.component.NOT_REACHABLE
import hex.HasHex
import hex.Hex
import hex.neighbors

fun <T> path(
    map: Map<Hex, T>,
    start: Hex,
    end: Hex
): List<Hex> where T : HasMoveCost, T : HasHex {
    val startPoint = map[start]
    val endPoint = map[end]
    if (startPoint == null || endPoint == null) {
        return emptyList()
    }

    if (endPoint.moveCost == NOT_REACHABLE) {
        return emptyList()
    }

    val frontier = mutableListOf(
        startPoint to NONE
    )
    val costs = mutableMapOf(start to NONE)
    val path = mutableMapOf<Hex, Hex>()

    while (frontier.isNotEmpty()) {
        val current = frontier.last()
        frontier.remove(current)
        if (current.first.hex == end) {
            break
        }

        map.neighbors(current.first.hex).forEach { neighbor ->
            val previousCost = costs[neighbor.key]
            val moveCost = neighbor.value.costToMove(current.first)
            val newCost =
                if (moveCost == NOT_REACHABLE) NOT_REACHABLE
                else (costs[current.first.hex] ?: NONE) + moveCost
            if (previousCost == null || newCost < previousCost) {
                costs[neighbor.key] = newCost
                val priority = if (newCost == NOT_REACHABLE) NOT_REACHABLE else newCost + end.distance(neighbor.key)
                frontier.apply {
                    if (priority != NOT_REACHABLE) {
                        add(neighbor.value to priority)
                        sortByDescending { it.second }
                    }
                }
                path[neighbor.key] = current.first.hex
            }
        }
    }

    var backward = path[end]
    val backwardPath = mutableListOf<Hex>()
    if (backward != null) {
        backwardPath.add(end)
    }
    while (backward != null) {
        backwardPath.add(backward)
        backward = path[backward]
    }

    return backwardPath.reversed()
}
