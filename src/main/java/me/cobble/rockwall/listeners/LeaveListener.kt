package me.cobble.rockwall.listeners

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class LeaveListener(plugin: Rockwall) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        PartyManager.deleteParty(PartyManager.getParty(event.player.uniqueId))
    }
}