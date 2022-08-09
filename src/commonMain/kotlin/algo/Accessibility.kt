package algo

import game.entity.component.HasMoveCost
import game.entity.component.NONE
import game.entity.component.NOT_REACHABLE
import hex.HasHex
import hex.Hex
import hex.neighbors


fun <T> accessibility(
    map: Map<Hex, T>,
    start: Hex,
    maxCost: Double
): List<Hex> where T : HasMoveCost, T : HasHex {
    val startPoint = map[start] ?: return emptyList()

    val frontier = mutableListOf(
        startPoint to NONE
    )
    val costs = mutableMapOf(start to NONE)
    val path = mutableMapOf<Hex, Hex>()

    while (frontier.isNotEmpty()) {
        val current = frontier.last()
        frontier.remove(current)


        map.neighbors(current.first.hex).forEach { neighbor ->
            val previousCost = costs[neighbor.key]
            val moveCost = neighbor.value.costToMove(current.first)
            val newCost =
                if (moveCost == NOT_REACHABLE) NOT_REACHABLE
                else (costs[current.first.hex] ?: NONE) + moveCost
            if ((previousCost == null || newCost < previousCost) && newCost <= maxCost) {
                costs[neighbor.key] = newCost
                val priority =
                    if (newCost == NOT_REACHABLE) NOT_REACHABLE else newCost
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

    return path.keys.toList()
}
