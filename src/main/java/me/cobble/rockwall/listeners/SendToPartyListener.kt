package me.cobble.rockwall.listeners

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.ColorUtils.sendDebug
import me.cobble.rockwall.utils.models.FormatTree
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

            val tree = FormatTree("party-formats")

            player.sendDebug("getting party details")
            val party = PartyUtils.getPartyBySpeaking(player.uniqueId)!!
            val type = if (party is AdminParty) PartyType.ADMIN else PartyType.NORMAL

            // various components from config formats
            val prefix = tree.asRockwallFormat(player, tree.getGroupPrefix(type.getType()), party)
            val prefixSeparator = tree.asRockwallFormat(player, tree.getGroupPrefixSeparator(type.getType()), party)
            val name = tree.asRockwallFormat(player, tree.getGroupPlayerName(type.getType()), party)
            val nameSeparator = tree.asRockwallFormat(player, tree.getGroupNameSeparator(type.getType()), party)
            val suffix = tree.asRockwallFormat(player, tree.getGroupSuffix(type.getType()), party)
            val suffixSeparator = tree.asRockwallFormat(player, tree.getGroupSuffixSeparator(type.getType()), party)

            player.sendDebug("assembling message")
            val components = ComponentBuilder()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .append(suffix)
                .append(suffixSeparator)
                .append(ColorUtils.colorizeComponents(e.message, player))
                .create()

            player.sendDebug("sending to you")
            player.spigot().sendMessage(*components)

            Bukkit.getConsoleSender().spigot().sendMessage(*components)

            player.sendDebug("sending to others")
            for (uuid: UUID in party.members) {
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
            player.sendDebug("resetting countdown if necessary")
            if (party is NormalParty) party.timeTillDeath = Config.getInt("parties.timeout")
        }
    }
}
