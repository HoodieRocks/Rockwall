package me.cobble.rockwall.listeners

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.global.FormatType
import me.cobble.rockwall.utils.global.GlobalChatUtils
import me.cobble.rockwall.utils.groups.GroupUtils
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class SendGloballyListener(private val plugin: Rockwall) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler
    fun onGlobalMessageSent(event: AsyncPlayerChatEvent) {
        event.isCancelled = true

        if (GroupUtils.getCurrentSpeakingChat(event.player.uniqueId) == null) {

            val prefix = GlobalChatUtils.formatMaker(event.player, "default", FormatType.PREFIX)
            val prefixSeparator = GlobalChatUtils.formatMaker(event.player, "default", FormatType.PREFIX_SEPARATOR)
            val name = GlobalChatUtils.formatMaker(event.player, "default", FormatType.NAME)
            val nameSeparator = GlobalChatUtils.formatMaker(event.player, "default", FormatType.NAME_SEPARATOR)

            Bukkit.spigot().broadcast(
                *ComponentBuilder()
                    .append(prefix)
                    .appendLegacy(" ")
                    .append(prefixSeparator)
                    .appendLegacy(" ")
                    .append(name)
                    .appendLegacy(" ")
                    .append(nameSeparator)
                    .appendLegacy(" " + Utils.color(event.message, event.player))
                    .create()
            )
        }
    }
}