package game.map.generation

import algo.getByProbability
import game.map.generation.rule.GenerationConfig
import hex.Hex
import hex.buildRectHexGrid
import hex.neighbors

class RectRoomGenerator(private val config: GenerationConfig) : RoomGenerator {
    private val transformers = config.rules.map { rule ->
        rule.states.map { it to rule.transformations }
    }.flatten().toMap()

    override fun generate(): Map<Hex, CellType> {
        val width = (config.size.minWidth..config.size.maxWidth).random()
        val height = (config.size.minHeight..config.size.maxHeight).random()

        return generateRoomWithRetries(width, height)
            .biggestBlob()
            .addBorder()
            .toOrigin()
    }

    private fun generateRoomWithRetries(width: Int, height: Int): MutableMap<Hex, CellType> {
        for (trie in 0..10) {
            val room = generateRoom(width, height)
            if (config.goal(room.values.toList())) {
                return room
            }
        }
        return mutableMapOf()
    }

    private fun generateRoom(width: Int, height: Int): MutableMap<Hex, CellType> {
        val grid = buildRectHexGrid(width, height) { _, _ ->
            getByProbability(config.initialStates)
        }.toMutableMap()

        for (gen in 0..config.generations) {
            grid.keys.forEach { hex ->
                val state = grid[hex]!!
                val neighbors = grid.neighbors(hex).values.toList()
                val transformations = transformers[state] ?: emptyList()
                val resultStates =
                    transformations.mapNotNull { it(state, neighbors) }.toMap().ifEmpty { mapOf(state to 1.0) }
                val nextState = getByProbability(resultStates)
                grid[hex] = nextState
            }
        }

        return grid
    }
}
