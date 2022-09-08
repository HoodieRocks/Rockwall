package me.cobble.rockwall.listeners

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.ChatFormatType
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.models.AdminParty
import me.cobble.rockwall.utils.parties.models.NormalParty
import net.md_5.bungee.api.chat.ComponentBuilder
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
        if (e.isCancelled) {
            return
        }

        if (PartyUtils.getPartyBySpeaking(player.uniqueId) != null) {
            e.isCancelled = true

            TextUtils.sendDebug("getting party details", player)
            val party = PartyUtils.getPartyBySpeaking(player.uniqueId)
            val type = if (party is AdminParty) PartyType.ADMIN else PartyType.NORMAL

            // various components from config formats
            val prefix = PartyUtils.formatMaker(player, party, type, ChatFormatType.PREFIX)
            val prefixSeparator = PartyUtils.formatMaker(player, party, type, ChatFormatType.PREFIX_SEPARATOR)
            val name = PartyUtils.formatMaker(player, party, type, ChatFormatType.NAME)
            val nameSeparator = PartyUtils.formatMaker(player, party, type, ChatFormatType.NAME_SEPARATOR)

            TextUtils.sendDebug("assembling message", player)
            val components = ComponentBuilder()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .append(TextUtils.colorToTextComponent(e.message, player))
                .create()

            TextUtils.sendDebug("sending to you", player)
            player.spigot().sendMessage(*components)

            Bukkit.getConsoleSender().spigot().sendMessage(*components)

            TextUtils.sendDebug("sending to others", player)
            for (uuid: UUID in party!!.members) {
                val resolvedPlayer = Bukkit.getPlayer(uuid)!!
                if (resolvedPlayer.isOnline) {
                    // we already sent the message to the event player, don't do it again
                    if (uuid != e.player.uniqueId) {
                        resolvedPlayer.spigot().sendMessage(*components)
                    }
                } else {
                    party.removeMember(uuid)
                }
            }

            // reset time until auto-deletion
            TextUtils.sendDebug("resetting countdown if necessary", player)
            if (party is NormalParty) party.timeTillDeath = Config.getInt("parties.timeout")
        }
    }
}
