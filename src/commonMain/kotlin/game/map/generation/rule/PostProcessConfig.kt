package game.map.generation.rule

import kotlinx.serialization.*

@Serializable
data class PostProcessConfig(
    val generations: Int,
    val rules: List<Rule>,
    ) {
}
