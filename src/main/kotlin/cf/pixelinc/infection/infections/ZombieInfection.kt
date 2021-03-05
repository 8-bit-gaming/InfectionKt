package cf.pixelinc.infection.infections

import cf.pixelinc.events.PlayerData
import cf.pixelinc.infection.BaseInfection
import cf.pixelinc.infection.InfectionType
import cf.pixelinc.util.isDay
import cf.pixelinc.util.isOutside
import org.bukkit.ChatColor
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object ZombieInfection : BaseInfection("T-Virus", InfectionType.ZOMBIE, EntityType.ZOMBIE), Listener {

    /*
        The classic zombie infection.
        Most widely known as The T-Virus (from resident evil).

        This infection causes the player to be friends with any hostile mob (they wont attack them).
        This may seem like a big plus, but... there are some downsides.

        The player now burns in the day-light, oh no!
        The player must eat brains (kill passive mobs, or players) or they will rot away and die.

        If a passive mob is infected:
          - They will attack players and other passive mobs nearby.
     */

    override fun infect(e: Entity) {
        if (e is Player) {
            val player: Player = e

            player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 5 * 20, 3))
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 5))
            player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 5 * 20, 2))

            player.sendMessage("${ChatColor.RED} You all the sudden feel the need for.. brains?!")

            // Now lets make all the hostile mobs aggro'd nearby aware that the player is now friendly.
            for(entity in player.getNearbyEntities(50.0, 50.0, 50.0))
                if (entity is Creature)
                   if (entity.target is Player && (entity.target as Player).uniqueId == player.uniqueId)
                       entity.target = null
        }
    }

    override fun tickInfection(player: Player) {
        if (player.isOutside())
            if (player.world.isDay())
                player.fireTicks = 20 * 3
    }



}
