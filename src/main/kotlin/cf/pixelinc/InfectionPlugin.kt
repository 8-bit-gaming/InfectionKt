package cf.pixelinc

import cf.pixelinc.commands.InfectCmd
import cf.pixelinc.events.Events
import cf.pixelinc.events.PlayerData
import cf.pixelinc.infection.InfectionManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class InfectionPlugin: JavaPlugin() {
    val infectionManager: InfectionManager = InfectionManager()
    var infectionScheduler: BukkitTask? = null

    companion object {
        var instance: InfectionPlugin? = null
        private set
    }

    override fun onEnable() {
        getCommand("infected").executor = InfectCmd

        Bukkit.getPluginManager().registerEvents(Events, this)
        Bukkit.getPluginManager().registerEvents(PlayerData, this)

        instance = this
    }

}