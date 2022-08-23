package game

object GameState {
    val levels = mutableMapOf<Int, MutableSet<Int>>().withDefault { mutableSetOf() }
    var level = 0
}