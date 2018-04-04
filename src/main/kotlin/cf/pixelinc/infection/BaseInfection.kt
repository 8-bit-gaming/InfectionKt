package cf.pixelinc.infection

import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

abstract class BaseInfection(val name: String, val type: InfectionType, val entity: EntityType?, val chance: Double = .4) {
    open val shouldBurn: Boolean = false

    abstract fun infect(e: Entity)
}
