package game.map.generation.rule

import game.map.generation.*
import kotlinx.serialization.*

@Serializable
data class GenerationConfig(
    val size: RoomSize,
    val generations: Int,
    val rules: List<Rule>
)

@Serializable
data class Rule(
    val states: Set<CellType>,
    val transformations: Set<TransformRule>
)
