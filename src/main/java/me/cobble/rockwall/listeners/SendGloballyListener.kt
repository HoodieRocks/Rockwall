package me.cobble.rockwall.listeners

import me.cobble.rockwall.config.models.ChatFormatType
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.parties.Parties
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
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

        // player is not muted by other plugins, override message
        event.isCancelled = true

        if (Parties.getPartyBySpeaking(player.uniqueId) == null) {
            val permission = ChatUtils.getFormatByPermission(player)

            // config format components
            val prefix = ChatUtils.makeFormat(player, permission, ChatFormatType.PREFIX)
            val prefixSeparator = ChatUtils.makeFormat(player, permission, ChatFormatType.PREFIX_SEPARATOR)
            val name = ChatUtils.makeFormat(player, permission, ChatFormatType.NAME)
            val nameSeparator = ChatUtils.makeFormat(player, permission, ChatFormatType.NAME_SEPARATOR)

            // hacky way to fix format codes being non-overridable
            val msg = TextComponent(Formats.color(ChatUtils.processMessageFeatures(event.message, player), player))
            msg.retain(ComponentBuilder.FormatRetention.NONE)

            val completedMessage = ComponentBuilder()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .append(msg)
                .create()

            // send to everyone
            Bukkit.spigot().broadcast(*completedMessage)

            // Specific exception so console can see chat
            Bukkit.getConsoleSender().spigot().sendMessage(*completedMessage)
        }
    }
}
