package game.map.generation

import game.map.generation.rule.PostProcessConfig
import game.map.generation.rule.RuleRunner
import hex.Hex

class PostProcessor(private val config: PostProcessConfig) {
    private val transformers = config.rules.sortedByDescending { it.priority }.map { rule ->
        rule.states.map { it to rule.transformations }
    }.flatten().toMap()
    private val runner = RuleRunner(transformers)

    fun process(map: Map<Hex, CellType>): Map<Hex, CellType> {
        return runner.process(map, config.generations)
    }
}
