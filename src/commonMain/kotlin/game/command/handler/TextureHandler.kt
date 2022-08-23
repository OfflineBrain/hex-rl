package game.command.handler

import com.offlinebrain.command.CommandHandler
import com.offlinebrain.command.CommandResult
import com.offlinebrain.command.Failure
import com.offlinebrain.command.Success
import com.offlinebrain.ecs.ECSManager
import com.soywiz.korinject.AsyncInjector
import game.command.SetTileTexture
import game.entity.component.Texture
import game.entity.component.Tile
import game.view.TextureContainer
import game.view.texture.TileTextures
import game.view.texture.cellMappingFunction
import hex.Hex
import utils.logger

class TextureHandler(
    private val ecs: ECSManager,
    private val textureContainer: TextureContainer,
    private val tileTexture: TileTextures
) : CommandHandler() {
    private val log by logger()
    val textureMappingFunction = tileTexture.cellMappingFunction()

    init {
        on(::setTileTexture)
    }

    private suspend fun setTileTexture(command: SetTileTexture): CommandResult {
        log.info { "Setting tile texture, ${command.entity}" }
        val entity = command.entity
        ecs {
            val hex = entity.get<Hex>() ?: return@ecs Failure("No hex found for entity $entity").also { log.error { it.message } }
            val tile = entity.get<Tile>() ?: return@ecs Failure("No cell found for entity $entity").also { log.error { it.message } }
            val sprite = textureContainer.run {
                alloc {
                    setPosition(hex.x.toFloat(), hex.y.toFloat())
                    setTex(textureMappingFunction(tile.type))
                }
            }
            entity.add(Texture(sprite, textureContainer.id))
            log.info { "Setting tile texture, sprite id: ${sprite}" }
        }
        return Success
    }


    companion object {
        suspend operator fun invoke(injector: AsyncInjector): TextureHandler {
            return TextureHandler(
                injector.get(),
                injector.get<Map<Int, TextureContainer>>()[1]
                    ?: throw IllegalStateException("No texture container found for id 1"),
                injector.get(),
            )
        }
    }
}