import com.soywiz.klogger.Logger
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
import game.map.generation.LevelGenerator
import game.map.generation.PostProcessor
import game.map.generation.RectRoomGenerator
import game.map.generation.addBorder
import game.map.generation.rule.GenerationConfig
import game.map.generation.rule.PostProcessConfig
import game.map.generation.toOrigin
import game.view.Minimap
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.math.max

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
    Logger.defaultLevel = Logger.Level.DEBUG
    Logger.defaultOutput = Logger.ConsoleLogOutput

    val sceneContainer = sceneContainer()
    sceneContainer.changeTo({ MyScene() })
}


class MyScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val generationConfigJson = resourcesVfs["generation/data/cavesConfig.json"].readString()
        val generationConfig = Json.decodeFromString<GenerationConfig>(serializer(), generationConfigJson)

        val caveRoomGenerator = RectRoomGenerator(generationConfig)

        val generationConfigJson2 = resourcesVfs["generation/data/bigRoomsConfig.json"].readString()
        val generationConfig2 = Json.decodeFromString<GenerationConfig>(serializer(), generationConfigJson2)

        val bigRoomGenerator = RectRoomGenerator(generationConfig2)

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
        val level = LevelGenerator(
            mapOf(
                caveRoomGenerator to 1.0,
                bigRoomGenerator to .3
            )
        ).generate(200, 200)


        val room = postProcessor.process(level.addBorder().toOrigin())
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
