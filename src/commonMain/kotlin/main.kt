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

        val minimap = Minimap(rectRoomGenerator.generate(), colorMapping)

        addChild(image(minimap.minimapBitmap))
    }
}
