package cf.pixelinc.events

import cf.pixelinc.infection.BaseInfection
import cf.pixelinc.infection.InfectionType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.collections.HashMap

data class PlayerData(private val id: UUID, val name: String) {
    var infection: BaseInfection? = null

    companion object : Listener {
        private val data = HashMap<UUID, PlayerData>()

        // For PlayerData[id]
        operator fun get(id: UUID?) = data[id]
        operator fun get(p: Player) = PlayerData[p.uniqueId] ?: PlayerData(p.uniqueId, p.name)

        @EventHandler
        fun onJoin(e: PlayerJoinEvent) = PlayerData(e.player.uniqueId, e.player.name)

        @EventHandler
        fun onQuit(e: PlayerQuitEvent) = data.remove(e.player.uniqueId)
    }

    fun isInfected() = infection != null

    init {
        data[id] = this
    }
}