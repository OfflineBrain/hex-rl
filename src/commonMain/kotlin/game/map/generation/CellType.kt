package game.map.generation

import kotlinx.serialization.Serializable

@Serializable
enum class CellType {
    VOID,
    FLOOR,
    WALL,
    LIGHT_CRYSTAL
}
