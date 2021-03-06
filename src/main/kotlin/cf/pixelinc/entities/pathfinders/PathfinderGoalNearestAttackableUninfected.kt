package cf.pixelinc.entities.pathfinders

import cf.pixelinc.util.isInfected
import net.minecraft.server.v1_16_R3.EntityHuman
import net.minecraft.server.v1_16_R3.EntityInsentient
import net.minecraft.server.v1_16_R3.EntityLiving
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget
import org.bukkit.entity.Player

class PathfinderGoalNearestAttackableUninfected<T : EntityLiving>(insentient : EntityInsentient, target : Class<T>, flag : Boolean) : PathfinderGoalNearestAttackableTarget<T>(insentient, target, flag) {
    override fun a(): Boolean {
        val ret = super.a()

        if (ret && this.c is EntityHuman) {
            val human : Player = this.c.bukkitEntity as Player
            if (human.isInfected()) {
                this.c = null
                return false
            }

            return true
        }

        return ret
    }
}