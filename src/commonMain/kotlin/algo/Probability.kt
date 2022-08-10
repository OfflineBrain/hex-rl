package algo

import kotlin.random.*

fun <V> getByProbability(map: Map<V, Double>): V {
    val totalProbability = map.values.sum()
    val random = Random.nextDouble(0.0, 1.0) * totalProbability
    var sum = 0.0
    for ((key, value) in map) {
        sum += value
        if (random <= sum) {
            return key
        }
    }
    throw IllegalStateException("This should never happen")
}
