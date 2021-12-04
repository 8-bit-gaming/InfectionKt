package cf.pixelinc.events

import cf.pixelinc.InfectionPlugin
import cf.pixelinc.entities.InfectedEntity
import cf.pixelinc.infection.InfectionManager
import cf.pixelinc.infection.InfectionType
import cf.pixelinc.util.isInfected
import net.minecraft.server.v1_16_R3.EntityAnimal
import net.minecraft.server.v1_16_R3.EntityCreature
import net.minecraft.server.v1_16_R3.EntityTypes
import net.minecraft.server.v1_16_R3.LootTableInfo
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftAnimals
import org.bukkit.entity.Animals
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

object Events : Listener {
    private val random: Random = Random()
    private val infectionManager : InfectionManager = InfectionPlugin.instance.infectionManager

    private infix fun Location.equalsBlock(other: Location) =
            this.blockX == other.blockX && this.blockY == other.blockY && this.blockZ == other.blockZ

    private infix fun Entity.isNear(other: Entity) =
            other.location.distanceSquared(this.location) <= 10*10

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        // Start the infection scheduler if there's actually players.
        if (InfectionPlugin.instance.infectionScheduler == null) {
            println("A Player has joined, starting the scheduler.")
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

            println("All the players have left the server, cancelling the infection scheduler")
        }
    }

    private val namespacedKey = NamespacedKey(InfectionPlugin.instance, "infected")

    @EventHandler
    fun onInfect(e: EntityDamageByEntityEvent) {
        // If the attacker is infected, they have a chance to infect other (animal) entities by attacking them.
        if (e.entity is Animals) {
            val persistedInfection = e.damager.persistentDataContainer.getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0)

            if (persistedInfection != 0) {
                val infectionType = InfectionType.fromInt(persistedInfection)
                val animal = (e.entity as CraftAnimals).handle

                if (infectionType != null && random.nextDouble() <= infectionType.chance) {
                    infectionManager.spawnInfectedAnimal(e.entity, animal, infectionType)
                }
            }
        }

        // Players can get infected from various entities, namely zombies
        // TODO: allow infected passives to infect players
        if (e.entity is Player) {
            val player: Player = (e.entity as Player)
            val playerData: PlayerData = PlayerData[player]
            if (playerData.isInfected()) return

            // Try and grab an infection by entity type
            val infection = infectionManager.getInfection(e.damager.type)
            if (infection != null && infection.shouldInfect()) {
                infection.infect(player)

                playerData.infection = infection
                player.sendMessage("${ChatColor.RED} You have been infected with the ${infection.name}")
            }
        }
    }
}
