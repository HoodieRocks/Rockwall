package me.cobble.rockwall.listeners

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.ChatFormatType
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.parties.AdminParty
import me.cobble.rockwall.utils.parties.parties.NormalParty
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

    // for some reason, this is required to be the lowest priority
    @EventHandler(priority = EventPriority.LOWEST)
    fun onSpeakToParty(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (event.isCancelled) {
            return
        }

        if (Parties.getPartyBySpeaking(player.uniqueId) != null) {
            event.isCancelled = true

            val party = Parties.getPartyBySpeaking(player.uniqueId)
            val type = if (party is AdminParty) PartyType.ADMIN else PartyType.NORMAL

            // various components from config formats
            val prefix = Parties.formatMaker(player, party, type, ChatFormatType.PREFIX)
            val prefixSeparator = Parties.formatMaker(player, party, type, ChatFormatType.PREFIX_SEPARATOR)
            val name = Parties.formatMaker(player, party, type, ChatFormatType.NAME)
            val nameSeparator = Parties.formatMaker(player, party, type, ChatFormatType.NAME_SEPARATOR)

            val components = ComponentBuilder()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .appendLegacy(Formats.color(event.message, player))

            player.spigot().sendMessage(*components.create())

            for (uuid: UUID in party!!.members) {
                val resolvedPlayer = Bukkit.getPlayer(uuid)!!
                if (resolvedPlayer.isOnline) {
                    // we already sent the message to the event player, don't do it again
                    if (uuid != event.player.uniqueId) {
                        resolvedPlayer.spigot().sendMessage(*components.create())
                    }
                } else {
                    party.removeMember(uuid)
                }
            }

            // reset time until auto-deletion
            if (party is NormalParty) party.timeTillDeath = Config.getInt("parties.timeout")
        }
    }
}
