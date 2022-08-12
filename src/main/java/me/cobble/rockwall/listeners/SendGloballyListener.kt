package me.cobble.rockwall.listeners

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.Emojis
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.chat.ChatUtils
import me.cobble.rockwall.utils.chat.FormatType
import me.cobble.rockwall.utils.parties.Parties
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

        if (Parties.getPartyBySpeaking(player.uniqueId) == null) {
            val permission = ChatUtils.getFormatPermission(player)

            // config format components
            val prefix = ChatUtils.makeFormat(player, permission, FormatType.PREFIX)
            val prefixSeparator = ChatUtils.makeFormat(player, permission, FormatType.PREFIX_SEPARATOR)
            val name = ChatUtils.makeFormat(player, permission, FormatType.NAME)
            val nameSeparator = ChatUtils.makeFormat(player, permission, FormatType.NAME_SEPARATOR)

            val completedMessage = ComponentBuilder()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .appendLegacy(Formats.color(processMessageFeatures(event.message, player), player))
                .create()

            Bukkit.spigot().broadcast(*completedMessage)

            // Specific exception so console can see chat
            Bukkit.getConsoleSender().spigot().sendMessage(*completedMessage)
        }
    }

    private fun processMessageFeatures(msg: String, player: Player): String {
        val transformedString: String = processMentions(msg, player)
        return processEmojis(transformedString)
    }

    private fun processMentions(msg: String, player: Player): String {
        if (!Config.getBool("global-chat.features.mentions.enabled")) return msg
        val mentionExpression = Regex("@[A-z]+")
        val retrievedSound = Sound.valueOf(
            Config.getString("global-chat.features.mentions.sound")
                .orElse("block_note_block_pling")
                .uppercase()
        )
        if (mentionExpression.containsMatchIn(msg)) {
            mentionExpression.findAll(msg).forEach {
                if ((it.value == "@everyone" || it.value == "@here") && player.hasPermission("rockwall.massmention")) {
                    if (Config.getBool("global-chat.features.mentions.play-sound")) {
                        for (p in Bukkit.getServer().onlinePlayers) {
                            p.playSound(p.location, retrievedSound, 0.3f, 1f)
                        }
                    }
                } else {
                    val mentioned = Bukkit.getPlayer(it.value.drop(1))
                    if (Config.getBool("global-chat.features.mentions.play-sound")) {
                        mentioned?.playSound(mentioned.location, retrievedSound, 0.3f, 1f)
                    }
                }

                return msg.replace(
                    it.value,
                    Config.getString("global-chat.features.mentions.format")
                        .orElse("block_note_block_pling")
                        .replace("%format%", it.value, true),
                    true
                )
            }
        }
        return msg
    }

    private fun processEmojis(msg: String): String {
        if (!Config.getBool("global-chat.features.emojis.enabled")) return msg
        val emojiExpression = Regex(":[A-z]+:")
        val emojis = Emojis.getAllEmojis()
        if (emojiExpression.containsMatchIn(msg)) {
            emojiExpression.findAll(msg).forEach {
                for (emoji in emojis) {
                    if ((emoji as String).lowercase() == it.value.lowercase().replace(":", "")) {
                        return msg.replace(it.value, Emojis.getEmoji(emoji))
                    }
                }
            }
        }
        return msg
    }
}
