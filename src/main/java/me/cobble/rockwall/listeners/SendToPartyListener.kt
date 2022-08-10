package me.cobble.rockwall.listeners

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.chat.FormatType
import me.cobble.rockwall.utils.parties.PartyType
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

    // for some reason, this is required to be the lowest priority
    @EventHandler(priority = EventPriority.LOWEST)
    fun onSpeakToparty(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (event.isCancelled) {
            return
        }

        if (PartyUtils.getPartyBySpeaking(player.uniqueId) != null) {
            event.isCancelled = true

            val party = PartyUtils.getPartyBySpeaking(player.uniqueId)
            val type = if (party is AdminParty) PartyType.ADMIN else PartyType.NORMAL

            // various components from config formats
            val prefix = PartyUtils.formatMaker(player, party, type, FormatType.PREFIX)
            val prefixSeparator = PartyUtils.formatMaker(player, party, type, FormatType.PREFIX_SEPARATOR)
            val name = PartyUtils.formatMaker(player, party, type, FormatType.NAME)
            val nameSeparator = PartyUtils.formatMaker(player, party, type, FormatType.NAME_SEPARATOR)

            val components = ComponentBuilder()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .appendLegacy(Utils.color(event.message, player))

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
            if (party is NormalParty) party.timeTillDeath = Config.getInt("partys.timeout")
        }
    }
}
