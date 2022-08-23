package game.view

import com.soywiz.korge.view.fast.FSprite
import com.soywiz.korge.view.fast.FSprites
import com.soywiz.korge.view.fast.fastForEach
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointInt
import hex.Hex
import kotlin.math.floor

class TextureContainer private constructor(
    maxSize: Int, val id: Int,
    private val boardSize: PointInt,
    private val tileSize: PointInt,
) : FSprites(maxSize) {
    private val rowShift = tileSize.y * (3.0 / 4.0).toFloat()

    var shift: PointInt = PointInt(0, 0)
        private set

    operator fun <R> invoke(id: Int, fn: FSprite.() -> R): R {
        return FSprite(id).fn()
    }

    fun alloc(fn: FSprite.() -> Unit): Int {
        return alloc().also(fn).id
    }

    fun FSprite.setPosition(x: Float, y: Float) {
        setPos(
            x = (x + shift.x) * tileSize.x,
            y = (y + shift.y) * rowShift
        )
    }

    fun shift(x: Int, y: Int) {
        var shiftX = x
        var shiftY = y
        val new = PointInt(x + shift.x, y + shift.y)
        if (new.x > 5) {
            shiftX = 5 - shift.x
        } else if (new.x < -boardSize.x + 1) {
            shiftX = -boardSize.x + 1 - shift.x
        }

        if (new.y > 10) {
            shiftY = 10 - shift.y
        } else if (new.y < -boardSize.y + 1) {
            shiftY = -boardSize.y + 1 - shift.y
        }

        shift = PointInt(shiftX + shift.x, shiftY + shift.y)

        fastForEach {
            it.x += (tileSize.x * shiftX).toFloat()
            it.y += (rowShift * shiftY).toFloat()
        }
    }

    fun getHex(xy: Point): Hex {
        val y = xy.y
        val x = ((xy.x + ((shift.x % 2) * (tileSize.x / 2)) - tileSize.x / 2) / tileSize.x)
        val t1: Double = y / (tileSize.y / 2)
        val t2: Double = floor(x + t1)
        val r: Double = floor((floor(t1 - x) + t2) / 3)
        val q: Double = floor((floor(2 * x + 1) + t2) / 3) - r
        return Hex(q.toInt() - shift.x + (shift.y / 2), r.toInt() - shift.y)
    }

    companion object {
        private val textureContainer = mutableMapOf<Int, TextureContainer>()

        operator fun invoke(maxSize: Int, id: Int, boardSize: PointInt, tileSize: PointInt): TextureContainer {
            return textureContainer.getOrPut(id) { TextureContainer(maxSize, id, boardSize, tileSize) }
        }

        operator fun get(id: Int): TextureContainer? = textureContainer[id]

        const val TILE_CONTAINER = 1
        const val ENTITY_CONTAINER = 2
    }
}
