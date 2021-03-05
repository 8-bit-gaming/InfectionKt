package cf.pixelinc.infection

import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import kotlin.random.Random

abstract class BaseInfection(val name: String, val type: InfectionType, val entity: EntityType?) {
    abstract fun infect(e: Entity)

    // Override this
    open fun tickInfection(player: Player) {

    }

    fun shouldInfect() : Boolean {
        return type != InfectionType.NONE && Random.nextDouble() <= type.chance
    }
}
