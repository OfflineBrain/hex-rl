@file:OptIn(ExperimentalSerializationApi::class)

package game.map.generation.rule

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
@JsonClassDiscriminator("#operator")
sealed interface IntOperation {
    operator fun invoke(a: Int, b: Int): Boolean
}

@Serializable
@SerialName(">")
object IntGt : IntOperation {
    override operator fun invoke(a: Int, b: Int): Boolean = a > b
}

@Serializable
@SerialName(">=")
object IntGte : IntOperation {
    override operator fun invoke(a: Int, b: Int): Boolean = a >= b
}

@Serializable
@SerialName("<")
object IntLt : IntOperation {
    override operator fun invoke(a: Int, b: Int): Boolean = a < b
}

@Serializable
@SerialName("<=")
object IntLte : IntOperation {
    override operator fun invoke(a: Int, b: Int): Boolean = a <= b
}

@Serializable
@SerialName("==")
object IntEq : IntOperation {
    override operator fun invoke(a: Int, b: Int): Boolean = a == b
}

@Serializable
@SerialName("!=")
object IntNeq : IntOperation {
    override operator fun invoke(a: Int, b: Int): Boolean = a != b
}
