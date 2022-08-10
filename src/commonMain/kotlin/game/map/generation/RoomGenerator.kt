package game.map.generation

import hex.*

interface RoomGenerator {
    fun generate(): Map<Hex, CellType>
}
