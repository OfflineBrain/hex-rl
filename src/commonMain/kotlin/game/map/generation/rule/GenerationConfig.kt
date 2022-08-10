package game.map.generation.rule

import game.map.generation.CellType
import kotlinx.serialization.Serializable

@Serializable
data class GenerationConfig(
    val size: RoomSize,
    val generations: Int,
    val initialStates: Map<CellType, Double>,
    val rules: List<Rule>
)
