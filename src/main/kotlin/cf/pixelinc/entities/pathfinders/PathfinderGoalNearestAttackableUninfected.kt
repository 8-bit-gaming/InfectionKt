package cf.pixelinc.entities.pathfinders

import cf.pixelinc.util.isInfected
import net.minecraft.world.entity.EntityInsentient
import net.minecraft.world.entity.EntityLiving
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget
import net.minecraft.world.entity.player.EntityHuman


import org.bukkit.entity.Player

class PathfinderGoalNearestAttackableUninfected<T : EntityLiving>(insentient : EntityInsentient, target : Class<T>, flag : Boolean) : PathfinderGoalNearestAttackableTarget<T>(insentient, target, flag) {
    override fun a(): Boolean {
        val ret = super.a()

        if (ret && this.c is EntityHuman) {
            val human : Player = (this.c as EntityHuman).bukkitEntity as Player
            if (human.isInfected()) {
                this.c = null
                return false
            }

            return true
        }

        return ret
    }
}