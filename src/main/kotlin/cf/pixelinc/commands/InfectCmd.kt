package cf.pixelinc.commands

import cf.pixelinc.InfectionPlugin
import cf.pixelinc.entities.InfectedEntity
import cf.pixelinc.events.PlayerData
import cf.pixelinc.infection.BaseInfection
import cf.pixelinc.infection.InfectionType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.persistence.PersistentDataType

object InfectCmd : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can run this as of now.")
            return false
        }

        val player: Player = sender
        val playerData: PlayerData = PlayerData[player]

        if (args.isNotEmpty()) {
            when(args[0]) {
                "status" ->
                    player.sendMessage("${ChatColor.GRAY}You are currently${if (!playerData.isInfected()) " ${ChatColor.GREEN}not infected" else " ${ChatColor.RED}infected with the ${playerData.infection?.name}"}")
                "infect" -> {
                    if (args.size >= 3) {
                        val target: Player? = Bukkit.getPlayer(args[1])
                        if (target == null) {
                            player.sendMessage("${ChatColor.RED}The player, '${args[1]}', could not be found.")
                            return false
                        }

                        val infection: BaseInfection? = InfectionPlugin.instance.infectionManager.getInfection(args[2])

                        if (infection == null) {
                            // TODO: Make it auto get the infections cuz I shouldn't need to update it every time.
                            player.sendMessage("${ChatColor.RED}The infection, '${args[2]}', does not exist, try: T-Virus/Radiation")
                            return false
                        }

                        // Grab their data
                        val targetData: PlayerData = PlayerData[target]
                        targetData.infection = infection
                        infection.infect(target)

                        player.sendMessage("${ChatColor.GREEN}Successfully infected '${target.name}' with the ${infection.name}")
                        target.sendMessage("${ChatColor.RED}You have been infected with the ${infection.name}")
                    } else
                        player.sendMessage("${ChatColor.RED}Invalid usage: infect <player> <infection>")
                }
                "uninfect" -> {
                    if (args.size >= 2) {
                        val target: Player? = Bukkit.getPlayer(args[1])
                        if (target == null) {
                            player.sendMessage("${ChatColor.RED}The player, '${args[1]}', could not be found.")
                            return false
                        }

                        val targetData: PlayerData = PlayerData[target]
                        targetData.infection = null

                        player.sendMessage("${ChatColor.GREEN}${target.name} has been cured of all diseases.")
                        target.sendMessage("${ChatColor.GREEN}You have been magically cured of all diseases.")
                    } else
                        player.sendMessage("${ChatColor.RED}Invalid usage: uninfect <player>")
                }
                "spawn" -> {
                    val zombie = InfectedEntity(player.location, net.minecraft.world.entity.EntityType.CAT)
                    val entity : CraftEntity = zombie.bukkitEntity
                    entity.persistentDataContainer.set(NamespacedKey(InfectionPlugin.instance, "infected"), PersistentDataType.INTEGER, InfectionType.ZOMBIE.value)

                    (player.world as CraftWorld).handle.addFreshEntity(entity.handle, CreatureSpawnEvent.SpawnReason.CUSTOM)
                    player.sendMessage("Kat spawned")
                }
                else -> {
                    player.sendMessage("${ChatColor.RED}Invalid sub-command, valid: status")
                }
            }
        } else
            player.sendMessage("${ChatColor.RED}Invalid command usage: /infected (status)")

        return true
    }
}