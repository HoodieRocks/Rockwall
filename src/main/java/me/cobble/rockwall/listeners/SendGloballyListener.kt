package me.cobble.rockwall.listeners

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ChatRoomMapping
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.ColorUtils.sendDebug
import me.cobble.rockwall.utils.FormatUtils
import me.cobble.rockwall.utils.models.FormatTree
import me.cobble.rockwall.utils.parties.PartyUtils
import org.bukkit.Bukkit
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
        val chatRoom = findChatRoomPrefixes(event.message)

        // player is muted
        if (event.isCancelled) {
            return
        }

        // player is not muted by other plugins, override message
        event.isCancelled = true

        if (chatRoom.first) {
            sendChatRoomMessage(player, event)
            return
        } else if (PartyUtils.getPartyBySpeaking(player.uniqueId) == null) {
            sendGlobalMessage(player, event)
        }
    }

    fun findChatRoomPrefixes(message: String): Pair<Boolean, String> {
        for (prefix in ChatRoomMapping.getPrefixes()) {
            if (message.startsWith(prefix)) return true to prefix
        }
        return false to ""
    }

    fun sendGlobalMessage(player: Player, event: AsyncPlayerChatEvent) {

        val tree = FormatTree("global-chat-formats")

        player.sendDebug("getting permissions")
        val permission = ChatUtils.getFormatByPermission(player)

        // config format components
        val chatColor = tree.getGroupChatColor(permission)

        player.sendDebug("assembling message")
        val completedMessage = FormatUtils.assembleMessage(
            ColorUtils.colorizeComponents(
                chatColor.orElse("&f") + ChatUtils.processMessageFeatures(
                    event.message,
                    player
                ), player
            ), tree, permission, player
        ).create()

        // send to everyone
        Bukkit.spigot().broadcast(*completedMessage)

        // Specific exception so console can see chat
        Bukkit.getConsoleSender().spigot().sendMessage(*completedMessage)
    }

    fun sendChatRoomMessage(player: Player, event: AsyncPlayerChatEvent) {
        val chatRoom = findChatRoomPrefixes(event.message)
        val tree = FormatTree("chat-room-formats")
        val room = ChatRoomMapping.getName(chatRoom.second)

        if (player.hasPermission("rockwall.chatroom.${chatRoom.second}.use")) {

            val chatColor = tree.getGroupChatColor(room)

            player.sendDebug("assembling message")
            val completedMessage = FormatUtils.assembleMessage(
                ColorUtils.colorizeComponents(
                    chatColor.orElse("&f") + ChatUtils.processMessageFeatures(
                        event.message,
                        player
                    ).drop(chatRoom.second.length), player
                ), tree, room, player
            ).create()

            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("rockwall.chatroom.${chatRoom.second}.use")) {
                    onlinePlayer.spigot().sendMessage(*completedMessage)
                }
            }

            // Specific exception so console can see chat
            Bukkit.getConsoleSender().spigot().sendMessage(*completedMessage)
        }
    }
}
