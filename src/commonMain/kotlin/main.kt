import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
	val sceneContainer = sceneContainer()
	sceneContainer.changeTo({ MyScene() })
}


class MyScene : Scene() {
	override suspend fun SContainer.sceneMain() {

	}
}
