package game.command

import com.offlinebrain.command.Command
import com.soywiz.korma.geom.PointInt

data class ShiftView(val direction: PointInt) : Command
