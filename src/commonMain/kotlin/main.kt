import com.soywiz.korge.Korge
import com.soywiz.korgw.GameWindow
import com.soywiz.korma.geom.ISizeInt

suspend fun main() = Korge(
    config = Korge.Config(
        module = GameModule,
        quality = GameWindow.Quality.QUALITY,
        windowSize = ISizeInt(1280, 720),
        virtualSize = ISizeInt(1280, 720),
    )
)
