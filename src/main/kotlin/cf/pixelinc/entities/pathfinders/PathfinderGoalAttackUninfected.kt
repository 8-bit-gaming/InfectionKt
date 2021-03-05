package cf.pixelinc.entities.pathfinders

import cf.pixelinc.events.PlayerData
import net.minecraft.server.v1_16_R3.*
import org.bukkit.entity.Player

/*
    An extension of the attack pathfinder to only attack players which are not infected.
 */
class PathfinderGoalAttackUninfected(creature : EntityCreature, damage : Double, flag : Boolean) : PathfinderGoalMeleeAttack(creature, damage, flag) {

    override fun a(): Boolean {
        val entity : EntityLiving? = this.a.goalTarget
        if (entity is EntityHuman) {
            val player : Player = (entity.bukkitEntity as Player)
            val playerData: PlayerData = PlayerData[player]

            if (playerData.isInfected())
                return false
        }

        return super.a()
    }

}