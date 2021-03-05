package cf.pixelinc.events

import cf.pixelinc.InfectionPlugin
import cf.pixelinc.infection.InfectionType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object Events : Listener {
    private val random: Random = Random()

    private infix fun Location.equalsBlock(other: Location) =
            this.blockX == other.blockX && this.blockY == other.blockY && this.blockZ == other.blockZ

    private infix fun Entity.isNear(other: Entity) =
            other.location.distanceSquared(this.location) <= 10*10

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        // Start the infection scheduler if there's actually players.
        if (InfectionPlugin.instance.infectionScheduler == null) {
            print("A Player has joined, starting the scheduler.")
            InfectionPlugin.instance.infectionScheduler =
                    Bukkit.getScheduler().runTaskTimerAsynchronously(InfectionPlugin.instance, Runnable {
                        for (player in Bukkit.getOnlinePlayers()) {
                            val playerData: PlayerData = PlayerData[player]

                            if (playerData.isInfected()) {
                                // Most events require you to run them sync as of 1.14, we must enforce that.
                                Bukkit.getScheduler().runTask(InfectionPlugin.instance, Runnable {
                                    playerData.infection?.tickInfection(player)
                                })
                            }
                        }
                    }, 20L, 20L)
        }
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        if (Bukkit.getOnlinePlayers().size - 1 <= 0) {
            val scheduler = InfectionPlugin.instance.infectionScheduler
            scheduler?.cancel()

            InfectionPlugin.instance.infectionScheduler = null

            print("All the players have left the server, cancelling the infection scheduler")
        }
    }

    @EventHandler
    fun onInfect(e: EntityDamageByEntityEvent) {
        if (e.entity is Player) {
            val player: Player = (e.entity as Player)
            val playerData: PlayerData = PlayerData[player]
            val chance: Double = random.nextDouble()

            if (playerData.isInfected()) return

            // Try and grab an infection by type
            InfectionPlugin.instance.infectionManager.getInfection(e.damager.type)?.also { infection ->
                if (infection.type != InfectionType.NONE) { // Valid infection potential
                    if (chance <= infection.chance) {
                        infection.infect(player)

                        playerData.infection = infection
                        player.sendMessage("${ChatColor.RED} You have been infected with the ${infection.name}")
                    }
                }
            }
        }
    }
}
