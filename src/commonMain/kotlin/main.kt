import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.image
import com.soywiz.korge.view.scale
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.file.std.resourcesVfs
import game.map.generation.CellType
import game.map.generation.JoiningRoom
import game.map.generation.RectRoomGenerator
import game.map.generation.addBorder
import game.map.generation.rule.GenerationConfig
import game.map.generation.toOrigin
import game.view.Minimap
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.math.max

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo({ MyScene() })
}


class MyScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val generationConfigJson = resourcesVfs["generation/data/bigBlobRoomGenRules.json"].readString()
        val generationConfig = Json.decodeFromString<GenerationConfig>(serializer(), generationConfigJson)

        val rectRoomGenerator = RectRoomGenerator(generationConfig)

        val colorMapping: (CellType) -> RGBA = {
            when (it) {
                CellType.FLOOR -> Colors["#7a7a7a"]
                CellType.WALL -> Colors["#494949"]
                CellType.DOOR -> Colors["#b85f02"]
                CellType.VOID -> Colors.BLACK
                CellType.LIGHT_CRYSTAL -> Colors.GOLD
            }
        }


        var level = JoiningRoom(rectRoomGenerator.generate())
        for (i in 0..500) {
            level = level.join(
                other = JoiningRoom(room = rectRoomGenerator.generate()),
                joins = mapOf(CellType.DOOR to 1.0),
                maxHeight = 1000,
                maxWidth = 1000
            )
        }

        val room = level.room.addBorder().toOrigin()
        val minimap = Minimap(room, colorMapping)

        val view = image(minimap.minimapBitmap)
        val sx = 512.0 / max(
            minimap.minimapBitmap.width,
            minimap.minimapBitmap.height
        )
        println(sx)
        view.scale(
            sx
        )
    }
}
