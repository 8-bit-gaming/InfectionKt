package cf.pixelinc.infection.infections

import cf.pixelinc.infection.BaseInfection
import cf.pixelinc.infection.InfectionType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object AirborneInfection : BaseInfection("Radiation", InfectionType.AIR, null) {

    override fun infect(e: Entity) {
        if (e is Player) {
            val player: Player = e

            player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 8))
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 10 * 20, 8))

            player.sendMessage("${ChatColor.RED}It seems a bit.. harder to breathe out here than normal...")
        }
    }

    override fun tickInfection(player: Player) {
        player.damage(0.5)
    }

}