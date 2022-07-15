package me.cobble.rockwall.listeners

import me.cobble.rockwall.rockwall.Config
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.global.FormatType
import me.cobble.rockwall.utils.groups.GroupType
import me.cobble.rockwall.utils.groups.GroupUtils
import me.cobble.rockwall.utils.groups.models.AdminGroup
import me.cobble.rockwall.utils.groups.models.NormalGroup
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

// Sends messages to Rockwall's group system
class SendToGroupListener(plugin: Rockwall) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    // for some reason, this is required to be the lowest priority
    @EventHandler(priority = EventPriority.LOWEST)
    fun onSpeakToGroup(event: AsyncPlayerChatEvent) {

        if(event.isCancelled) {
            return
        }

        event.isCancelled = true

        if (GroupUtils.getCurrentSpeakingChat(event.player.uniqueId) != null) {

            val group = GroupUtils.getCurrentSpeakingChat(event.player.uniqueId)
            val type = if(group is AdminGroup) GroupType.ADMIN else GroupType.NORMAL


            // various components from config formats
            val prefix = GroupUtils.formatMaker(event.player, group, type, FormatType.PREFIX)
            val prefixSeparator = GroupUtils.formatMaker(event.player, group, type, FormatType.PREFIX_SEPARATOR)
            val name = GroupUtils.formatMaker(event.player, group, type, FormatType.NAME)
            val nameSeparator = GroupUtils.formatMaker(event.player, group, type, FormatType.NAME_SEPARATOR)

            val components = ComponentBuilder()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .appendLegacy(Utils.color(event.message, event.player))

            event.player.spigot().sendMessage(*components.create())

            for (uuid: UUID in group!!.members) {
                val player = Bukkit.getPlayer(uuid)!!
                if (player.isOnline){

                    // we already sent the message to the event player, don't do it again
                    if(uuid != event.player.uniqueId) {
                        player.spigot().sendMessage(*components.create())
                    }
                } else {
                    group.removeMember(uuid)
                }
            }

            // reset time until auto-deletion
            if (group is NormalGroup) group.timeTillDeath = Config.getInt("groups.timeout")
        }
    }
}