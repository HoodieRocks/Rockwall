package me.cobble.rockwall.listeners

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.global.FormatType
import me.cobble.rockwall.utils.global.GlobalChatUtils
import me.cobble.rockwall.utils.groups.GroupUtils
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class SendGloballyListener(plugin: Rockwall) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onGlobalMessageSent(event: AsyncPlayerChatEvent) {
        if (event.isCancelled) {
            return
        }

        event.isCancelled = true

        if (GroupUtils.getCurrentSpeakingChat(event.player.uniqueId) == null) {

            val permission = GlobalChatUtils.getFormatPermission(event.player)

            val prefix = GlobalChatUtils.formatMaker(event.player, permission, FormatType.PREFIX)
            val prefixSeparator = GlobalChatUtils.formatMaker(event.player, permission, FormatType.PREFIX_SEPARATOR)
            val name = GlobalChatUtils.formatMaker(event.player, permission, FormatType.NAME)
            val nameSeparator = GlobalChatUtils.formatMaker(event.player, permission, FormatType.NAME_SEPARATOR)

            Bukkit.spigot().broadcast(
                *ComponentBuilder()
                    .append(prefix)
                    .append(prefixSeparator)
                    .append(name)
                    .append(nameSeparator)
                    .appendLegacy(Utils.color(event.message, event.player))
                    .create()
            )
        }
    }
}