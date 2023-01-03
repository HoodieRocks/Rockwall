package me.cobble.rockwall.listeners

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.ColorUtils.sendDebug
import me.cobble.rockwall.utils.FormatUtils
import me.cobble.rockwall.utils.models.FormatTree
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.models.AdminParty
import me.cobble.rockwall.utils.parties.models.NormalParty
import me.cobble.rockwall.utils.parties.models.PartyType
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

// Sends messages to Rockwall's party system
class SendToPartyListener(plugin: Rockwall) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    // this is required to be the lowest priority, because muting
    @EventHandler(priority = EventPriority.LOWEST)
    fun onSpeakToParty(e: AsyncPlayerChatEvent) {
        val player = e.player

        // is the player muted by other plugins?
        if (e.isCancelled || PartyUtils.getPartyBySpeaking(player.uniqueId) == null) {
            return
        }

        e.isCancelled = true

        val tree = FormatTree("party-formats")

        player.sendDebug("getting party details")
        val party = PartyUtils.getPartyBySpeaking(player.uniqueId)!!
        val type = if (party is AdminParty) PartyType.ADMIN else PartyType.NORMAL

        val components =
            FormatUtils.assembleMessage(ColorUtils.colorSpigot(e.message, player), tree, type.getType(), player, party)
                .create()

        player.sendDebug("sending to you")
        player.spigot().sendMessage(*components)

        Bukkit.getConsoleSender().spigot().sendMessage(*components)

        player.sendDebug("sending to others")
        for (uuid: UUID in party.members) {
            val resolvedPlayer = Bukkit.getPlayer(uuid)!!
            if (resolvedPlayer.isOnline && uuid != e.player.uniqueId) {
                resolvedPlayer.spigot().sendMessage(*components)
            } else {
                party.removeMember(uuid)
            }
        }

        // reset time until auto-deletion
        player.sendDebug("resetting countdown if necessary")
        if (party is NormalParty) party.timeTillDeath = Config.getInt("parties.timeout")
    }
}
