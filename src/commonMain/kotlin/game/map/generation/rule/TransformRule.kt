package game.map.generation.rule

import game.map.generation.CellType
import game.map.generation.rule.MatchCondition.ALL
import game.map.generation.rule.MatchCondition.ANY
import game.map.generation.rule.MatchCondition.COUNT
import game.map.generation.rule.MatchCondition.NONE
import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    val states: Set<CellType>,
    val transformations: Set<TransformRule>,
    val priority: Int = 0,
)

@Serializable
data class GoalRule(
    private val match: MatchCondition,
    private val constraints: Set<Constraint>,
    private val min: Int = 1,
    private val max: Int = constraints.size,
) {
    init {
        require(min >= 0) { "min must be >= 0" }
        require(max >= 0) { "max must be >= 0" }
        require(min <= max) { "min must be <= max" }
    }

    operator fun invoke(cells: List<CellType>): Boolean {
        val matches = constraints.count { constraint ->
            val compliant = cells.count { it == constraint.cell }
            constraint.condition(compliant, constraint.value)
        }

        return when (match) {
            ALL -> matches == constraints.size
            ANY -> matches > 0
            NONE -> matches == 0
            COUNT -> matches in min..max
        }
    }
}

@Serializable
data class TransformRule(
    private val match: MatchCondition,
    private val constraints: Set<Constraint>,
    private val min: Int = 1,
    private val max: Int = constraints.size,
    private val result: CellType,
    private val subRules: Set<TransformRule> = setOf(),
    private val probability: Double = 0.5,
) {
    init {
        require(min >= 0) { "min must be >= 0" }
        require(max >= 0) { "max must be >= 0" }
        require(min <= max) { "min must be <= max" }
    }

    operator fun invoke(cell: CellType, neighbors: List<CellType>): Pair<CellType, Double>? {
        val matches = constraints.count { constraint ->
            val cells = neighbors.count { it == constraint.cell }
            constraint.condition(cells, constraint.value)
        } + subRules.count { it(cell, neighbors)?.first == result }

        val comply = when (match) {
            ALL -> matches == constraints.size + subRules.size
            ANY -> matches > 0
            NONE -> matches == 0
            COUNT -> matches in min..max
        }

        if (comply) {
            return result to probability
        }
        return null
    }
}

@Serializable
enum class MatchCondition {
    ALL,
    ANY,
    NONE,
    COUNT,
}

@Serializable
data class Constraint(
    val cell: CellType,
    val value: Int,
    val condition: IntOperation,
)
