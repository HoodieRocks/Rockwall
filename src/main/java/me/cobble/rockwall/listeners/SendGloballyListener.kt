package me.cobble.rockwall.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.chat.ChatUtils
import me.cobble.rockwall.utils.chat.FormatType
import me.cobble.rockwall.utils.parties.PartyUtils
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

/**
 * Sends message to the regular Minecraft Chat
 */
class SendGloballyListener(plugin: Rockwall) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onGlobalMessageSent(event: AsyncChatEvent) {
        val player = event.player

        // player is muted
        if (event.isCancelled) {
            return
        }

        event.isCancelled = true

        if (PartyUtils.getPartyBySpeaking(player.uniqueId) == null) {

            val permission = ChatUtils.getFormatPermission(player)

            // config format components
            val prefix = ChatUtils.makeFormat(player, permission, FormatType.PREFIX)
            val prefixSeparator = ChatUtils.makeFormat(player, permission, FormatType.PREFIX_SEPARATOR)
            val name = ChatUtils.makeFormat(player, permission, FormatType.NAME)
            val nameSeparator = ChatUtils.makeFormat(player, permission, FormatType.NAME_SEPARATOR)

            Bukkit.broadcast(Component.text()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .append(event.message())
                .asComponent())
        }
    }
}