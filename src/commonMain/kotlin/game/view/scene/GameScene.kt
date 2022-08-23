package game.view.scene

import com.offlinebrain.command.AsyncCommandBus
import com.offlinebrain.command.Failure
import com.offlinebrain.ecs.ECSManager
import com.offlinebrain.ecs.Entity
import com.soywiz.korev.Key
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.addUpdater
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.PointInt
import game.command.ConvertMapToEntities
import game.command.SetTileTexture
import game.command.ShiftView
import game.map.generation.CellType
import game.map.generation.LevelGenerator
import game.map.generation.PostProcessor
import game.map.generation.RectRoomGenerator
import game.map.generation.addBorder
import game.map.generation.rule.GenerationConfig
import game.map.generation.rule.PostProcessConfig
import game.map.generation.toOrigin
import game.view.TextureContainer
import game.view.texture.TileTextures
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import utils.logger

class GameScene : Scene() {
    val log by logger()
    private lateinit var bus: AsyncCommandBus
    private lateinit var ecs: ECSManager
    private lateinit var textures: Map<Int, TextureContainer>
    private lateinit var tileTexture: TileTextures
    override suspend fun SContainer.sceneMain() {
        bus = injector.get()
        ecs = injector.get()
        textures = injector.get()
        tileTexture = injector.get()

        log.info { "Loading resources" }
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

        log.info { "Generating level" }
        val level = LevelGenerator(
            mapOf(
                caveRoomGenerator to 1.0,
                bigRoomGenerator to .3
            )
        ).generate(50, 100)


        val room = postProcessor.process(level.addBorder().toOrigin())

        log.info { "Converting level to entities" }
        val entities = mutableSetOf<Entity>()
        bus.send(ConvertMapToEntities(room) {
            entities.add(it)
        })
        entities
            .onEach {
                ecs {
                    log.trace { "ID $it, ${it.components()}" }
                }
            }
            .map { SetTileTexture(it) }
            .also { bus.sendManyAsync(it).await().also { if (it is Failure) log.error { it.message } } }

        val textureContainer = textures[TextureContainer.TILE_CONTAINER]!!
        log.info { "Rendering level, ${textureContainer.size} sprites" }
        val view = textureContainer.createView(tileTexture.tileSet)
        addChild(view)

        addUpdater {
            runBlocking { shiftView() }
        }
    }

    private suspend fun shiftView() {

        if (!views.input.keys[Key.LEFT_CONTROL] && !views.input.keys[Key.RIGHT_CONTROL]) {
            if (views.input.keys.justPressed(Key.UP)) {
                bus.send(ShiftView(PointInt(0, 10)))
            }
            if (views.input.keys.justPressed(Key.RIGHT)) {
                bus.send(ShiftView(PointInt(-10, 0)))
            }
            if (views.input.keys.justPressed(Key.DOWN)) {
                bus.send(ShiftView(PointInt(0, -10)))
            }
            if (views.input.keys.justPressed(Key.LEFT)) {
                bus.send(ShiftView(PointInt(10, 0)))
            }
        }
    }
}