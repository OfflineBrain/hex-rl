package game.map.generation.rule

import kotlinx.serialization.*

@Serializable
data class RoomSize(
    val minWidth: Int,
    val minHeight: Int,
    val maxWidth: Int,
    val maxHeight: Int,
) {
    init {
        require(minWidth > 0) { "minimum width must be positive" }
        require(minHeight > 0) { "minimum height must be positive" }
        require(maxWidth > 0 && maxWidth > minWidth) { "maximum height must be positive and higher than minimum" }
        require(maxHeight > 0 && maxHeight > minHeight) { "maximum height must be positive and higher than minimum" }
    }
}

