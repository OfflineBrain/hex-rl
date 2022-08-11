package game.view

import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.color.RGBAPremultiplied
import hex.Hex

class Minimap<T>(private val level: Map<Hex, T>, private val mapping: (T) -> RGBA) {
    val minimapBitmap: Bitmap32

    init {
        val x = level.keys.maxBy { it.x }.x.toInt() + 2
        val y = level.keys.maxBy { it.y }.y.toInt() + 1

        minimapBitmap = Bitmap32(x * 6, y * 5, RGBAPremultiplied(Colors.WHITE))
        write()
    }

    private fun write() {
        level.forEach { (hex, value) ->
            val color = mapping(value)
            minimapBitmap.fill(color, (hex.x * 6).toInt(), (hex.y * 5).toInt(), 6, 5)
            minimapBitmap.contentVersion++
        }
    }
}
