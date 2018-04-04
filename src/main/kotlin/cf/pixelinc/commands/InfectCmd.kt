package cf.pixelinc.commands

import cf.pixelinc.events.PlayerData
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
                        player.sendMessage("${ChatColor.GRAY}You are currently${if (!playerData.isInfected()) " ${ChatColor.GREEN}not" else ChatColor.RED.toString()} infected")
                else -> {
                    player.sendMessage("${ChatColor.RED}Invalid sub-command, valid: status")
                }
            }
        } else
            player.sendMessage("${ChatColor.RED}Invalid command usage: /infected (status)")

        return true
    }
}