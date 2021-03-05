package cf.pixelinc.entities.pathfinders

import cf.pixelinc.InfectionPlugin
import cf.pixelinc.infection.InfectionType
import cf.pixelinc.util.isInfected
import net.minecraft.server.v1_16_R3.*
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class PathfinderGoalNearestAttackableUninfected<T : EntityLiving>(insentient : EntityInsentient, target : Class<T>, flag : Boolean) : PathfinderGoalNearestAttackableTarget<T>(insentient, target, flag) {

    private val namespacedKey = NamespacedKey(InfectionPlugin.instance, "infected")

    override fun a(): Boolean {
        val entity : EntityLiving? = this.e.goalTarget
        if (entity is EntityHuman) {
            val player : Player = (entity.bukkitEntity as Player)

            if (player.isInfected()) {
                this.e.goalTarget = null
                return false
            }
        } else if (entity is EntityLiving) {
            // if it's an infected zombie mob, don't attack
            val bukkitEntity = entity.bukkitEntity
            val persistedInfection = bukkitEntity.persistentDataContainer.getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0)
            if (persistedInfection == InfectionType.ZOMBIE.value) {
                this.e.goalTarget = null
                return false
            }
        }
        return super.a()
    }
}