package game.command.handler

import com.offlinebrain.command.CommandHandler
import com.offlinebrain.command.CommandResult
import com.offlinebrain.command.Success
import com.soywiz.korinject.AsyncInjector
import game.command.ShiftView
import game.view.TextureContainer

class ViewHandler(
    private val views: Map<Int, TextureContainer>
) : CommandHandler() {
    init {
        on(::shiftView)
    }

    private fun shiftView(command: ShiftView): CommandResult {
        views.forEach {
            it.value.shift(command.direction.x, command.direction.y)
        }
        return Success
    }

    companion object {
        suspend operator fun invoke(injector: AsyncInjector): ViewHandler {
            return ViewHandler(
                injector.get(),
            )
        }
    }
}