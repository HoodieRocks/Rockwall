package me.cobble.rockwall.listeners

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinLeaveListeners(private val plugin: Rockwall) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        PartyManager.deleteParty(PartyManager.getParty(event.player.uniqueId))
        PartyManager.getAllAdminParties().thenAccept { parties ->
            parties.forEach {
                it.removeMember(event.player.uniqueId)
            }
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if ((player.hasPermission("rockwall.admin") || player.isOp) && plugin.getUpdateUtils().updateAvailable()) {
            plugin.getUpdateUtils().sendUpdateAvailableMsg(player)
        }

        if(player.hasPermission("rockwall.admin.join") || player.isOp) {
            PartyManager.getAllAdminParties().thenAccept { parties ->
                parties.forEach {
                    it.addMember(player.uniqueId)
                }
            }
        }
    }
}
