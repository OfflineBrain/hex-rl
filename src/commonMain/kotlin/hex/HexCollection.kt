package hex

fun <V> Map<Hex, V>.neighbors(hex: Hex): Map<Hex, V> {
    val neighbors = mutableMapOf<Hex, V>()
    for (neighbor in hex.neighbors.values) {
        this[neighbor]?.let { neighbors[neighbor] = it }
    }
    return neighbors
}

fun <V> buildRectHexGrid(width: Int, height: Int, build: (Int, Int) -> V): Map<Hex, V> {
    val hexes = mutableMapOf<Hex, V>()
    for (y in 0 until height) {
        val offset = y / 2
        for (x in -offset until width - offset) {
            val hex = Hex(x, y)
            hexes[hex] = build(x, y)
        }
    }
    return hexes
}

