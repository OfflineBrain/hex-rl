package game.view.texture

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.BitmapSlice
import com.soywiz.korim.bitmap.sliceWithSize
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

class TileTextures private constructor(
    private val width: Int,
    private val height: Int
) {

    lateinit var tileSet: Bitmap
        private set

    private val internalTextures = mutableMapOf<String, BitmapSlice<Bitmap>>()
    val textures: Map<String, BitmapSlice<Bitmap>> get() = internalTextures

    suspend fun init() {
        tileSet = resourcesVfs["tiles/${width}x${height}tiles.png"].readBitmap()

        internalTextures["voidTexture"] = tileSet.sliceWithSize(0, 0, width, height)
        internalTextures["wallTexture"] = tileSet.sliceWithSize((width + 1) * 1, 0, width, height)
        internalTextures["floorTexture"] = tileSet.sliceWithSize((width + 1) * 2, 0, width, height)
        internalTextures["doorTexture"] = tileSet.sliceWithSize((width + 1) * 3, 0, width, height)
        internalTextures["shadowTexture"] = tileSet.sliceWithSize(0, (height + 1) * 1, width, height)
        internalTextures["lightCrystalTexture"] =
            tileSet.sliceWithSize((width + 1) * 1, (height + 1) * 1, width, height)
    }

    companion object {
        suspend operator fun invoke(width: Int, height: Int): TileTextures {
            return TileTextures(width, height).also { it.init() }
        }
    }
}

fun TileTextures.cellMappingFunction() = { cell: String ->
    when (cell) {
        "floor" -> textures["floorTexture"]!!
        "door" -> textures["doorTexture"]!!
        "wall" -> textures["wallTexture"]!!
        "void" -> textures["voidTexture"]!!
        "light_crystal" -> textures["lightCrystalTexture"]!!
        else -> {
            throw IllegalArgumentException("Unknown cell type: $cell")
        }
    }
}

class EntityTextures private constructor(
    private val width: Int,
    private val height: Int
) {

    lateinit var tileSet: Bitmap
        private set

    private val internalTextures = mutableMapOf<String, BitmapSlice<Bitmap>>()
    val textures: Map<String, BitmapSlice<Bitmap>> get() = internalTextures

    suspend fun init() {
        tileSet = resourcesVfs["tiles/${width}x${height}entities.png"].readBitmap()

        internalTextures["playerTexture"] = tileSet.sliceWithSize(0, 0, width, height)
        internalTextures["monsterTexture"] = tileSet.sliceWithSize((width + 1) * 1, 0, width, height)
        internalTextures["accessTexture"] = tileSet.sliceWithSize((width + 1) * 2, 0, width, height)
        internalTextures["pathTexture"] = tileSet.sliceWithSize((width + 1) * 3, 0, width, height)
    }

    companion object {
        suspend operator fun invoke(width: Int, height: Int): EntityTextures {
            return EntityTextures(width, height).also { it.init() }
        }
    }
}

fun EntityTextures.textureMappingFunction() = { type: String ->
    when (type) {
        "player" -> textures["playerTexture"]!!
        "monster" -> textures["monsterTexture"]!!
        "access" -> textures["accessTexture"]!!
        "path" -> textures["pathTexture"]!!
        else -> {
            throw IllegalArgumentException("Unknown entity type: $type")
        }
    }
}