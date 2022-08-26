package game.command.handler

import com.offlinebrain.command.CommandHandler
import com.offlinebrain.command.CommandResult
import com.offlinebrain.command.Failure
import com.offlinebrain.command.Success
import com.offlinebrain.ecs.ECSManager
import com.soywiz.korge.view.fast.FSprite
import com.soywiz.korim.color.RGBA
import com.soywiz.korinject.AsyncInjector
import game.command.ApplyLight
import game.command.SetEntityTexture
import game.command.SetTileTexture
import game.entity.component.Displayable
import game.entity.component.Light
import game.entity.component.Texture
import game.entity.component.Tile
import game.entity.component.displayableQuery
import game.entity.component.tileQuery
import game.view.TextureContainer
import game.view.texture.EntityTextures
import game.view.texture.TileTextures
import game.view.texture.cellMappingFunction
import game.view.texture.textureMappingFunction
import hex.Hex
import utils.logger
import kotlin.math.max

class TextureHandler(
    private val ecs: ECSManager,
    private val tileTextureContainer: TextureContainer,
    private val entityTextureContainer: TextureContainer,
    private val tileTexture: TileTextures,
    private val entityTextures: EntityTextures,
) : CommandHandler() {
    private val log by logger()
    val tileTextureMappingFunction = tileTexture.cellMappingFunction()
    val entityTextureMappingFunction = entityTextures.textureMappingFunction()

    init {
        on(::setTileTexture)
        on(::setEntityTexture)
        on(::light)
    }

    private fun setTileTexture(command: SetTileTexture): CommandResult {
        log.trace { "Setting tile texture, ${command.entity}" }
        val entity = command.entity
        return ecs {
            val hex = entity.get<Hex>()
                ?: return@ecs Failure("No hex found for entity $entity").also { log.error { it.message } }
            val tile = entity.get<Tile>()
                ?: return@ecs Failure("No cell found for entity $entity").also { log.error { it.message } }
            val sprite = tileTextureContainer.run {
                alloc {
                    setPosition(hex.x.toFloat(), hex.y.toFloat())
                    setTex(tileTextureMappingFunction(tile.type))
                }
            }
            entity.add(Texture(sprite, tileTextureContainer.id))
            log.trace { "Setting tile texture, sprite id: ${sprite}" }
            Success
        }
    }

    private fun setEntityTexture(command: SetEntityTexture): CommandResult {
        val entity = command.entity
        return ecs {
            val hex = entity.get<Hex>()
                ?: return@ecs Failure("No hex found for entity $entity").also { log.error { it.message } }
            val displayable = entity.get<Displayable>()
                ?: return@ecs Failure("No display option found for entity $entity").also { log.error { it.message } }

            val sprite = entityTextureContainer.run {
                alloc {
                    setPosition(hex.x.toFloat(), hex.y.toFloat())
                    setTex(entityTextureMappingFunction(displayable.type))
                }
            }
            entity.add(Texture(sprite, entityTextureContainer.id))
            Success
        }
    }

    private fun light(command: ApplyLight): CommandResult {
        val defaultMul = 55u

        val hexToEntityTexture = ecs {
            displayableQuery.map.mapValues { (_, entity) ->
                entity.get<Texture>()
            }
        }

        ecs {
            tileQuery.map.mapValues { (hex, tile) ->
                val texture = tile.get<Texture>()
                val light = tile.get<Light>()

                val mul = max(light?.level?.toUInt() ?: 0u, defaultMul).run { RGBA(toInt(), toInt(), toInt()) }

                if (texture != null) {
                    tileTextureContainer.run {
                        FSprite(texture.id).colorMul = mul
                    }
                }

                hexToEntityTexture[hex]?.let { entityTexture ->
                    entityTextureContainer.run {
                        FSprite(entityTexture.id).colorMul = mul
                    }
                }
            }
        }


        return Success
    }

    companion object {
        suspend operator fun invoke(injector: AsyncInjector): TextureHandler {
            return TextureHandler(
                injector.get(),
                injector.get<Map<Int, TextureContainer>>()[TextureContainer.TILE_CONTAINER]
                    ?: throw IllegalStateException("No texture container found for id ${TextureContainer.TILE_CONTAINER}"),
                injector.get<Map<Int, TextureContainer>>()[TextureContainer.ENTITY_CONTAINER]
                    ?: throw IllegalStateException("No texture container found for id ${TextureContainer.ENTITY_CONTAINER}"),
                injector.get(),
                injector.get(),
            )
        }
    }
}