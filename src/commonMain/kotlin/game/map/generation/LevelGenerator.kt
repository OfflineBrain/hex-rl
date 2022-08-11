package game.map.generation

import algo.getByProbability
import com.soywiz.korio.async.async
import hex.Hex
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import utils.AsyncLogger
import utils.measureTimeMillis

class LevelGenerator(private val generators: Map<RoomGenerator, Double>) {
    private val logger = AsyncLogger<LevelGenerator>()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun generate(width: Int, height: Int): Map<Hex, CellType> {
        lateinit var root: JoiningRoom
        val generationTime = measureTimeMillis {
            root = JoiningRoom(getByProbability(generators).generate())
            while (true) {
                var room: Pair<JoiningRoom, JoiningRoom>? = null
                val expandTime = measureTimeMillis {
                    room = flow {
                        for (i in 0 until 10) {
                            emit(JoiningRoom(getByProbability(generators).generate()))
                        }
                    }.buffer(10).transform {
                        emit(
                            GlobalScope.async {
                                root to root.join(
                                    other = it,
                                    joins = mapOf(CellType.DOOR to 1.0),
                                    maxWidth = width,
                                    maxHeight = height
                                )
                            }
                        )
                    }.buffer(10).transform {
                        emit(it.await())
                    }.filter {
                        it.first.room.size != it.second.room.size
                    }.firstOrNull()
                }

                if (room != null) {
                    root = room!!.second
                    logger.debug { "Joined ${root.room.size}" }
                } else if (root.room.isNotEmpty()) {
                    break
                }

                if (expandTime > 150) {
                    logger.debug { "Expand time exceeded: $expandTime" }
                    root = JoiningRoom(root.room)
                }
            }
        }

        logger.info { "Level generation time: ${generationTime}ms" }

        return root.room
    }
}
