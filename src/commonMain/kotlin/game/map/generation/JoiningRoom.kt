package game.map.generation

import algo.*
import hex.*

class JoiningRoom(
    val room: Map<Hex, CellType>,
    private val borders: MutableMap<Int, List<Hex>> = mutableMapOf()
) {

    init {
        if (borders.isEmpty()) {
            borders[-1] = room.keys.minBy { it.q }.let { border -> room.keys.filter { hex -> hex.q == border.q } }
            borders[1] = room.keys.maxBy { it.q }.let { border -> room.keys.filter { hex -> hex.q == border.q } }
            borders[-2] = room.keys.minBy { it.r }.let { border -> room.keys.filter { hex -> hex.r == border.r } }
            borders[2] = room.keys.maxBy { it.r }.let { border -> room.keys.filter { hex -> hex.r == border.r } }
            borders[-3] = room.keys.minBy { it.s }.let { border -> room.keys.filter { hex -> hex.s == border.s } }
            borders[3] = room.keys.maxBy { it.s }.let { border -> room.keys.filter { hex -> hex.s == border.s } }
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
        )
    ): JoiningRoom {
        val borderIndex = borders.keys.random()
        val otherBorderIndex = borderIndex * -1

        val joinHex = borders[borderIndex]!!.random()
        val otherJoinHex = other.borders[otherBorderIndex]!!.random()

        val diff = joinHex - otherJoinHex
        val shiftedOtherRoom = other.room.mapKeys { it.key + diff }

        val intersection = room.keys.intersect(shiftedOtherRoom.keys)
        val merge = intersection.associateWith {
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

        val merged = mutableMapOf<Hex, CellType>().apply {
            putAll(room)
            putAll(shiftedOtherRoom)
            putAll(merge)
            put(joinHex, getByProbability(joins))
        }

        val newBorders = mutableMapOf<Int, List<Hex>>()
        borders.keys.filter { it != borderIndex && it != otherBorderIndex }.forEach {
            newBorders[it] = borders[it]!! + other.borders[it]!!.map { it + diff }
        }
        newBorders[borderIndex] = other.borders[borderIndex]!!.map { it + diff }
        newBorders[otherBorderIndex] = borders[otherBorderIndex]!!

        return JoiningRoom(merged, newBorders)
    }
}
