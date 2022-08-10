package game.map.generation

import kotlinx.serialization.*

@Serializable
enum class CellType {
    VOID,
    DOOR,
    FLOOR,
    WALL,
    LIGHT_CRYSTAL
}
