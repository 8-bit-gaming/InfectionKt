package cf.pixelinc.entities.pathfinders

import cf.pixelinc.util.isInfected
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal

import org.bukkit.entity.Player

class PathfinderGoalNearestAttackableUninfected<T : LivingEntity>(insentient : Mob, target : Class<T>, flag : Boolean) : NearestAttackableTargetGoal<T>(insentient, target, flag) {
    override fun canUse(): Boolean {
        val ret = super.canUse()

        if (ret && this.target is net.minecraft.world.entity.player.Player) {
            val human : Player = (this.target as net.minecraft.world.entity.player.Player).bukkitEntity as Player
            if (human.isInfected()) {
                this.target = null
                return false
            }

            return true
        }

        return ret
    }
}