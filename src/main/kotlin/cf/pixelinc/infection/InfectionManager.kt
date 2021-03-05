package cf.pixelinc.infection

import cf.pixelinc.infection.infections.AirborneInfection
import cf.pixelinc.infection.infections.ZombieInfection
import org.bukkit.entity.EntityType

class InfectionManager {
    private val infections = ArrayList<BaseInfection>()

    fun getInfection(name: String): BaseInfection? {
        return infections.find { it.name.equals(name, ignoreCase = true) }
    }

    fun getInfection(type: EntityType): BaseInfection? {
        return infections.find { it.entity == type }
    }

    init {
        infections.add(ZombieInfection)
        infections.add(AirborneInfection)
    }
}