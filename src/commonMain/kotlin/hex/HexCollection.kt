package hex

fun <V> Map<Hex, V>.neighbors(hex: Hex): Map<Hex, V> {
    val neighbors = mutableMapOf<Hex, V>()
    for (neighbor in hex.neighbors.values) {
        this[neighbor]?.let { neighbors[neighbor] = it }
    }
    return neighbors
}

