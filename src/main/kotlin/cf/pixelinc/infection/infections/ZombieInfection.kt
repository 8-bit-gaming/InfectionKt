package cf.pixelinc.infection.infections

import cf.pixelinc.infection.BaseInfection
import cf.pixelinc.infection.InfectionType
import cf.pixelinc.util.isDay
import cf.pixelinc.util.isOutside
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object ZombieInfection : BaseInfection("T-Virus", InfectionType.ZOMBIE, EntityType.ZOMBIE), Listener {
    override val shouldBurn = true

    override fun infect(e: Entity) {
        if (e is Player) {
            val player: Player = e

            player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 5 * 20, 3))
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 5))
            player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 5 * 20, 2))

            player.sendMessage("${ChatColor.RED} You all the sudden feel the need for.. brains?!")

            // Now lets make all the zombies aggro'd nearby aware that the player is now friendly.
            for(entity in player.getNearbyEntities(50.0, 50.0, 50.0))
                if (entity is Zombie)
                   if (entity.target is Player && entity.target.uniqueId == player.uniqueId)
                       entity.target = null
        }
    }

    override fun tickInfection(player: Player) {
        if (player.isOutside())
            if (player.world.isDay() && this.shouldBurn)
                player.fireTicks = 20 * 3
    }

}
