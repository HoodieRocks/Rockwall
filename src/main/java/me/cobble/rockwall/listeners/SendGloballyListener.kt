package me.cobble.rockwall.listeners

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.chat.ChatUtils
import me.cobble.rockwall.utils.chat.FormatType
import me.cobble.rockwall.utils.parties.PartyUtils
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * Sends message to the regular Minecraft Chat
 */
class SendGloballyListener(plugin: Rockwall) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onGlobalMessageSent(event: AsyncPlayerChatEvent) {
        val player = event.player

        // player is muted
        if (event.isCancelled) {
            return
        }

        event.isCancelled = true

        if (PartyUtils.getCurrentSpeakingChat(player.uniqueId) == null) {

            val permission = ChatUtils.getFormatPermission(player)

            // config format components
            val prefix = ChatUtils.formatMaker(player, permission, FormatType.PREFIX)
            val prefixSeparator = ChatUtils.formatMaker(player, permission, FormatType.PREFIX_SEPARATOR)
            val name = ChatUtils.formatMaker(player, permission, FormatType.NAME)
            val nameSeparator = ChatUtils.formatMaker(player, permission, FormatType.NAME_SEPARATOR)

            Bukkit.spigot().broadcast(
                *ComponentBuilder()
                    .append(prefix)
                    .append(prefixSeparator)
                    .append(name)
                    .append(nameSeparator)
                    .appendLegacy(Utils.color(event.message, player))
                    .create()
            )
        }
    }
}