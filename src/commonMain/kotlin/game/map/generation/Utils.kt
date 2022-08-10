package game.map.generation

import hex.Hex
import hex.neighbors

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
