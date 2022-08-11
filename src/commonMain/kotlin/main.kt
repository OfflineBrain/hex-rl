import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.file.std.*
import game.map.generation.*
import game.map.generation.rule.*
import game.view.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.math.*

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo({ MyScene() })
}


class MyScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val generationConfigJson = resourcesVfs["generation/data/bigRoomsConfig.json"].readString()
        val generationConfig = Json.decodeFromString<GenerationConfig>(serializer(), generationConfigJson)

        val rectRoomGenerator = RectRoomGenerator(generationConfig)

        val postConfigJson = resourcesVfs["generation/data/levelPostProcessRules.json"].readString()
        val postConfig = Json.decodeFromString<PostProcessConfig>(serializer(), postConfigJson)

        val postProcessor = PostProcessor(postConfig)

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

        val room = postProcessor.process(level.room.addBorder().toOrigin())
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
