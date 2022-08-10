package game.map.generation

import hex.*

fun Map<Hex, CellType>.biggestBlob(blobTypes: Set<CellType> = setOf(CellType.FLOOR)): Map<Hex, CellType> {
    fun fill(blobs: Map<Hex, Any>): Set<Hex> {
        val rewritableBlobs = blobs.toMutableMap()
        val results = mutableListOf<Set<Hex>>()
        while (rewritableBlobs.keys.isNotEmpty()) {
            val blob = mutableSetOf<Hex>()

            val hex = rewritableBlobs.keys.first()
            rewritableBlobs.remove(hex)
            blob.add(hex)

            var neighbors = rewritableBlobs.neighbors(hex).keys
            while (neighbors.isNotEmpty()) {
                val current = neighbors
                blob.addAll(current)
                current.forEach { rewritableBlobs.remove(it) }
                neighbors = neighbors.map { rewritableBlobs.neighbors(it).keys }.flatten().toSet()
            }

            results.add(blob)
        }

        return results.maxBy { it.size }
    }


    val blobs = this.filter { it.value in blobTypes }
    val blob = fill(blobs)

    return blob.map { blobs.neighbors(it).keys }.flatten().distinct()
        .associateWith { blobs[it]!! }
}

fun Map<Hex, CellType>.addBorder(borderType: CellType = CellType.WALL): Map<Hex, CellType> {
    val minWidth = keys.minBy { it.x.toInt() }.x.toInt() - 1
    val maxWidth = keys.maxBy { it.x.toInt() }.x.toInt() + 1
    val minHeight = keys.minBy { it.y }.y.toInt() - 1
    val maxHeight = keys.maxBy { it.y }.y.toInt() + 1

    val shift = Hex.ofXY(minWidth, minHeight)

    return buildRectHexGrid(maxWidth - minWidth + 1, maxHeight - minHeight + 1) { _, _ -> borderType }
        .mapKeys { (hex, _) -> hex + shift }.toMutableMap()
        .also { it.putAll(this) }
        .filter { (hex, _) -> neighbors(hex).count { (_, cell) -> cell != borderType } > 0 }
}

fun Map<Hex, CellType>.toOrigin(): Map<Hex, CellType> {
    val x = keys.minBy { it.x.toInt() }.x.toInt()
    val y = keys.minBy { it.y }.y.toInt()

    val shift = Hex.ofXY(x, y)

    return mapKeys { (hex, _) -> hex - shift }
}
