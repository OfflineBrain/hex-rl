package game.map.generation

import algo.getByProbability
import hex.Hex

class JoiningRoom(
    val room: Map<Hex, CellType>,
    private val borders: MutableMap<Int, List<List<Hex>>> = mutableMapOf()
) {

    init {
        if (borders.isEmpty() && room.isNotEmpty()) {
            borders[-1] =
                listOf(room.keys.minBy { it.q }.let { border -> room.keys.filter { hex -> hex.q == border.q } })
            borders[1] =
                listOf(room.keys.maxBy { it.q }.let { border -> room.keys.filter { hex -> hex.q == border.q } })
            borders[-2] =
                listOf(room.keys.minBy { it.r }.let { border -> room.keys.filter { hex -> hex.r == border.r } })
            borders[2] =
                listOf(room.keys.maxBy { it.r }.let { border -> room.keys.filter { hex -> hex.r == border.r } })
            borders[-3] =
                listOf(room.keys.minBy { it.s }.let { border -> room.keys.filter { hex -> hex.s == border.s } })
            borders[3] =
                listOf(room.keys.maxBy { it.s }.let { border -> room.keys.filter { hex -> hex.s == border.s } })
        }
    }

    fun join(
        other: JoiningRoom,
        joins: Map<CellType, Double> = mapOf(
            CellType.DOOR to 1.0,
            CellType.FLOOR to 0.5
        ),
        allowedToMerge: Map<CellType, Double> = mapOf(
            CellType.DOOR to 1.0,
            CellType.FLOOR to 1.0,
            CellType.WALL to 0.0001,
            CellType.LIGHT_CRYSTAL to 0.2,
        ),
        maxWidth: Int = Int.MAX_VALUE,
        maxHeight: Int = Int.MAX_VALUE,
    ): JoiningRoom {
        if (other.room.isEmpty()) {
            return this
        } else if (room.isEmpty()) {
            return other
        }

        for (borderIndex in borders.keys.shuffled()) {
            val otherBorderIndex = borderIndex * -1

            for (joinBorder in borders[borderIndex]!!.shuffled()) {
                val joinHex = joinBorder.random()
                val otherJoinBorder = other.borders[otherBorderIndex]!!.random()
                val otherJoinHex = otherJoinBorder.random()

                val diff = joinHex - otherJoinHex
                val shiftedOtherRoom = other.room.mapKeys { it.key + diff }

                val intersection = room.keys.intersect(shiftedOtherRoom.keys)
                if (intersection.any { it !in joinBorder }) {
                    continue
                }

                val merge = mergeIntersection(intersection, shiftedOtherRoom, allowedToMerge)

                val merged = mutableMapOf<Hex, CellType>().apply {
                    putAll(room)
                    putAll(shiftedOtherRoom)
                    putAll(merge)
                    put(joinHex, getByProbability(joins))
                }

                val origin = merged.toOrigin()
                val x = origin.keys.maxBy { it.x }.x.toInt() + 1
                val y = origin.keys.maxBy { it.y }.y.toInt()
                if (x > maxWidth || y > maxHeight) {
                    continue
                }

                val newBorders = combineBorders(borderIndex, otherBorderIndex, other, diff, joinHex)

                return JoiningRoom(merged, newBorders)
            }
        }
        return this
    }

    private fun combineBorders(
        borderIndex: Int,
        otherBorderIndex: Int,
        otherRoom: JoiningRoom,
        diff: Hex,
        joinHex: Hex
    ): MutableMap<Int, List<List<Hex>>> {
        val newBorders = mutableMapOf<Int, List<List<Hex>>>()
        borders.keys.filter { it != borderIndex && it != otherBorderIndex }.forEach {
            newBorders[it] =
                borders[it]!! + otherRoom.borders[it]!!.map { border -> border.map { hex -> hex + diff } }
        }
        newBorders[borderIndex] =
            otherRoom.borders[borderIndex]!!.map { border -> border.map { hex -> hex + diff } } +
                (borders[borderIndex]!!.filterNot { it.contains(joinHex) })
        newBorders[otherBorderIndex] = borders[otherBorderIndex]!! +
            (otherRoom.borders[otherBorderIndex]!!.map { border -> border.map { hex -> hex + diff } }
                .filterNot { it.contains(joinHex) })
        return newBorders
    }

    private fun mergeIntersection(
        intersection: Set<Hex>,
        shiftedOtherRoom: Map<Hex, CellType>,
        allowedToMerge: Map<CellType, Double>
    ): Map<Hex, CellType> {
        return intersection.associateWith {
            val roomType = room[it]!!
            val otherRoomType = shiftedOtherRoom[it]!!

            if (roomType == otherRoomType) {
                roomType
            } else {
                getByProbability(
                    mapOf(
                        roomType to (allowedToMerge[roomType] ?: 0.0),
                        otherRoomType to (allowedToMerge[otherRoomType] ?: 0.0)
                    )
                )
            }
        }
    }
}
