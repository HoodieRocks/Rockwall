package me.cobble.rockwall.listeners

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.chat.ChatUtils
import me.cobble.rockwall.utils.chat.FormatType
import me.cobble.rockwall.utils.parties.PartyUtils
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
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

            Bukkit.spigot().broadcast(
                *ComponentBuilder()
                    .append(prefix)
                    .append(prefixSeparator)
                    .append(name)
                    .append(nameSeparator)
                    .appendLegacy(Formats.color(processMessageFeatures(event.message, player), player))
                    .create()
            )
        }
    }

    private fun processMessageFeatures(msg: String, player: Player): String {
        return processMentions(msg, player)
    }

    private fun processMentions(msg: String, player: Player): String {
        if (!Config.getBool("global-chat.features.mentions.enabled")) return msg
        val mentionExpression = Regex("@[A-z]+")
        if (mentionExpression.containsMatchIn(msg)) {
            mentionExpression.findAll(msg).forEach {
                if ((it.value == "@everyone" || it.value == "@here") && player.hasPermission("rockwall.massmention")) {
                    for (p in Bukkit.getServer().onlinePlayers) {
                        p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.3f, 1f)
                    }
                } else {
                    val mentioned = Bukkit.getPlayer(it.value.drop(1))
                    mentioned?.playSound(mentioned.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.3f, 1f)
                }

                return msg.replace(
                    it.value,
                    Config.getString("global-chat.features.mentions.format").replace("%format%", it.value)
                )
            }
        }
        return msg
    }
}
