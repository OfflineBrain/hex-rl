package game.entity.component

interface HasTransparency {
    val transparency: Transparency
}

data class Transparency(val transparent: Boolean) : SComponent()