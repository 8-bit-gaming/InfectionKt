package cf.pixelinc.infection

import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

abstract class BaseInfection(val name: String, val type: InfectionType, val entity: EntityType?, val chance: Double = .4) {
    abstract fun infect(e: Entity)

    // Override this
    open fun tickInfection(player: Player) {

    }
}
