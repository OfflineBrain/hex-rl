import com.offlinebrain.command.AsyncCommandBus
import com.offlinebrain.ecs.ECSManager
import com.soywiz.klogger.Logger
import com.soywiz.korge.scene.Module
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.PointInt
import game.command.handler.EntityHandler
import game.command.handler.TextureHandler
import game.command.handler.ViewHandler
import game.entity.component.SComponent
import game.map.AccessibilityMap
import game.view.TextureContainer
import game.view.scene.GameScene
import game.view.texture.EntityTextures
import game.view.texture.TileTextures
import hex.Hex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object GameModule : Module() {
    override val mainScene = GameScene::class

    override suspend fun AsyncInjector.configure() {
        Logger.defaultLevel = Logger.Level.DEBUG
        Logger.defaultOutput = Logger.ConsoleLogOutput

        mapSingleton { AsyncCommandBus(CoroutineScope(Dispatchers.Default)) }
        mapSingleton {
            ECSManager().apply {
                SComponent::class.sealedSubclasses.forEach {
                    register(it)
                }
                register<Hex>()
            }
        }


        mapSingleton {
            mapOf(
                TextureContainer.TILE_CONTAINER to TextureContainer(
                    1_000_000, TextureContainer.TILE_CONTAINER, PointInt(100, 100), PointInt(42, 50)
                ),
                TextureContainer.ENTITY_CONTAINER to TextureContainer(
                    1_000_000, TextureContainer.ENTITY_CONTAINER, PointInt(100, 100), PointInt(42, 50)
                )
            )
        }
        mapSingleton { TileTextures(42, 50) }
        mapSingleton { EntityTextures(42, 50) }

        EntityHandler(this).also { get<AsyncCommandBus>().register(it) }
        TextureHandler(this).also { get<AsyncCommandBus>().register(it) }
        ViewHandler(this).also { get<AsyncCommandBus>().register(it) }

        get<ECSManager>().register(AccessibilityMap.accessibilityTileQuery)

        mapPrototype { GameScene() }
    }
}