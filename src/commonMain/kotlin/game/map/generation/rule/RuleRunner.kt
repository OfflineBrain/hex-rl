package game.map.generation.rule

import algo.getByProbability
import game.map.generation.CellType
import hex.Hex
import hex.neighbors

class RuleRunner(private val transforms: Map<CellType, Collection<TransformRule>>) {

    fun process(map: Map<Hex, CellType>, generations: Int): Map<Hex, CellType> {
        val grid = map.toMutableMap()
        for (gen in 0..generations) {
            grid.keys.forEach { hex ->
                val state = grid[hex]!!
                val neighbors = grid.neighbors(hex).values.toList()
                val transformations = transforms[state] ?: emptyList()
                val resultStates =
                    transformations.mapNotNull { it(state, neighbors) }.toMap().ifEmpty { mapOf(state to 1.0) }
                val nextState = getByProbability(resultStates)
                grid[hex] = nextState
            }
        }

        return grid
    }
}
