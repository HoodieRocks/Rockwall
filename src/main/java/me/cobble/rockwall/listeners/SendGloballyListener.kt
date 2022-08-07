package me.cobble.rockwall.listeners

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.chat.ChatUtils
import me.cobble.rockwall.utils.chat.FormatType
import me.cobble.rockwall.utils.parties.PartyUtils
<<<<<<< HEAD
import net.md_5.bungee.api.chat.ComponentBuilder
=======
import net.kyori.adventure.text.Component
>>>>>>> parent of d7d1460 (added basic support for adventure api)
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

        if (PartyUtils.getPartyBySpeaking(player.uniqueId) == null) {

            val permission = ChatUtils.getFormatPermission(player)

            // config format components
            val prefix = ChatUtils.makeFormat(player, permission, FormatType.PREFIX)
            val prefixSeparator = ChatUtils.makeFormat(player, permission, FormatType.PREFIX_SEPARATOR)
            val name = ChatUtils.makeFormat(player, permission, FormatType.NAME)
            val nameSeparator = ChatUtils.makeFormat(player, permission, FormatType.NAME_SEPARATOR)

<<<<<<< HEAD
            Bukkit.spigot().broadcast(
                *ComponentBuilder()
                    .append(prefix)
                    .append(prefixSeparator)
                    .append(name)
                    .append(nameSeparator)
                    .appendLegacy(Utils.color(event.message, player))
                    .create()
            )
=======
            Bukkit.broadcast(Component.text()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .append(event.message())
                .asComponent())
>>>>>>> parent of d7d1460 (added basic support for adventure api)
        }
    }
}