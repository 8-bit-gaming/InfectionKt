package cf.pixelinc.util

import cf.pixelinc.events.PlayerData
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

// Extensions have to be outside of a class so they're not considered a "member".
fun Player.isOutside(): Boolean {
    val loc = this.location
    val world = loc.world

    return world?.getHighestBlockYAt(loc)!! < loc.blockY + 1
}

fun Player.isInfected(): Boolean {
    val playerData: PlayerData = PlayerData[this]

    return playerData.isInfected()
}

fun World.isDay(): Boolean {
    return (this.time < 12300 || this.time > 23850)
}

object GameUtil {

    fun circle(loc: Location, radius: Int, chance: Int): ArrayList<Location> {
        val inRange = ArrayList<Location>()
        val random = Random()

        val world = loc.world

        val cx = loc.blockX
        val cz = loc.blockZ
        val rs = radius * radius

        for (x in cx - radius..cx + radius) {
            for (z in cz - radius..cz + radius) {
                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rs) {
                    val rloc = Location(world,  (x +random.nextInt(chance)).toDouble(), loc.y, (z + random.nextInt(chance)).toDouble())
                    inRange.add(rloc)
                }
            }
        }

        print("[CIRCLE FUNC] Locations: ${inRange.size}")

        return inRange
    }

}