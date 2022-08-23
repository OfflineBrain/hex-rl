package game.entity.component

import com.offlinebrain.ecs.*

interface HasTransparency {
    val transparency: Transparency
}

sealed class Transparency : SComponent()

object Opaque : Transparency()

object Transparent : Transparency()
