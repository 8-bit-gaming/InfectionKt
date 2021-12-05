package cf.pixelinc.infection

import cf.pixelinc.InfectionPlugin
import cf.pixelinc.entities.InfectedEntity
import cf.pixelinc.infection.infections.AirborneInfection
import cf.pixelinc.infection.infections.ZombieInfection
import net.minecraft.world.entity.animal.Animal
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.persistence.PersistentDataType

class InfectionManager {
    private val infections = ArrayList<BaseInfection>()

    fun getInfection(name: String): BaseInfection? {
        return infections.find { it.name.equals(name, ignoreCase = true) }
    }

    fun getInfection(type: EntityType): BaseInfection? {
        return infections.find { it.entity == type }
    }

    fun getInfection(type: InfectionType): BaseInfection? {
        return infections.find { it.type == type }
    }

    // Spawn an infected animal
    fun spawnInfectedAnimal(entity : Entity, animal : Animal, infectionType : InfectionType) {
        // ad() => getEntityType()
        val infectedEntity = InfectedEntity(entity.location, animal.type)
        val bukkitEntity: Entity = infectedEntity.bukkitEntity

        bukkitEntity.persistentDataContainer.set(
            NamespacedKey(InfectionPlugin.instance, "infected"),
            PersistentDataType.INTEGER,
            infectionType.value
        )

        entity.remove()
        (entity.world as CraftWorld).handle.addFreshEntity(infectedEntity, CreatureSpawnEvent.SpawnReason.CUSTOM)
    }

    init {
        infections.add(ZombieInfection)
        infections.add(AirborneInfection)
    }
}