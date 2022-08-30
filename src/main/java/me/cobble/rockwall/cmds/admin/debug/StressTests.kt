package me.cobble.rockwall.cmds.admin.debug

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.ChatFormatType
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.models.Party
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import java.util.*

object StressTests {

    fun parties(player: Player) {
        player.sendMessage("This test represents 10K parties at the same time, this is more a performance test than an example of actual use")
        player.sendMessage("Testing create() with 10K entries")

        val localCopy = linkedMapOf<UUID, Party>()

        val totalTime = System.currentTimeMillis()
        var time = System.currentTimeMillis()
        for (i in 0 until 10000) {
            val uuid = UUID.randomUUID()
            localCopy[uuid] = PartyManager.createParty(uuid, TextUtils.randomString(10), PartyType.NORMAL)
        }
        player.sendMessage("Took ${System.currentTimeMillis() - time}ms to create 10K party objects")

        player.sendMessage("getting all 10K objects via UUID")
        time = System.currentTimeMillis()
        localCopy.forEach {
            PartyManager.getParty(it.key)
        }
        player.sendMessage("Took ${System.currentTimeMillis() - time}ms to get 10K objects via UUID")

        player.sendMessage("getting all 10K objects via name (expensive)")
        time = System.currentTimeMillis()
        localCopy.forEach {
            PartyManager.getParty(it.value.alias)
        }
        player.sendMessage("Took ${System.currentTimeMillis() - time}ms to get 10K objects via name")

        player.sendMessage("deleting all 10k objects")
        time = System.currentTimeMillis()
        localCopy.forEach {
            PartyManager.deleteParty(it.value)
        }
        player.sendMessage("Took ${System.currentTimeMillis() - time}ms to delete 10K objects")

        localCopy.clear()

        player.sendMessage("Total time of ${System.currentTimeMillis() - totalTime}ms")
    }

    fun chat(player: Player) {
        val totalTime = System.currentTimeMillis()
        if (PartyUtils.getPartyBySpeaking(player.uniqueId) == null) {
            player.sendMessage("This test represents 10K chat messages at the same time, this is more a performance test than an example of actual use")
            player.sendMessage("Generating 10K fake UUIDs")

            val uuids = linkedSetOf<UUID>()

            for (i in 0 until 10000) {
                uuids.add(UUID.randomUUID())
            }

            player.sendMessage("generating format objects")
            var time = System.currentTimeMillis()

            val permission = ChatUtils.getFormatByPermission(player)

            // config format components
            var prefix: TextComponent? = null
            var prefixSeparator: TextComponent? = null
            var name: TextComponent? = null
            var nameSeparator: TextComponent? = null
            var chatColor: String? = null

            for (uuid in uuids) {
                chatColor = getChatColor(permission)
                prefix = makeFormat(permission, ChatFormatType.PREFIX)
                prefixSeparator = makeFormat(permission, ChatFormatType.PREFIX_SEPARATOR)
                name = makeFormat(permission, ChatFormatType.NAME)
                nameSeparator = makeFormat(permission, ChatFormatType.NAME_SEPARATOR)
            }
            player.sendMessage("Took ${System.currentTimeMillis() - time}ms to make format objects")

            player.sendMessage("combining format objects")
            time = System.currentTimeMillis()
            for (uuid in uuids) {
                ComponentBuilder()
                    .append(prefix)
                    .append(prefixSeparator)
                    .append(name)
                    .append(nameSeparator)
                    .append(
                        chatColor + TextUtils.colorToTextComponent(
                            ChatUtils.processMessageFeatures(
                                TextUtils.randomString(25),
                                player
                            ), player
                        )
                    )
                    .create()
            }
            player.sendMessage("Took ${System.currentTimeMillis() - time}ms to combine format objects")

            uuids.clear()
        }
        player.sendMessage("Total time of ${System.currentTimeMillis() - totalTime}ms")

    }

    private fun makeFormat(formatName: String, type: ChatFormatType): TextComponent? {
        if (formatName.isBlank()) return null
        val configSection = Config.getSection("global-chat.formats.$formatName") ?: return null
        val section = configSection.getSection(type.getType())
        val format = TextUtils.colorToTextComponent(section.getString("display")!!)

        format.font = section.getOptionalString("font").orElse("minecraft:default")
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            //TextUtils.formatStringList(section.getStringList("hover"), player).create()
            Text("This is just a test message you wont see this anyways")
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            // TextUtils.setPlaceholders(player, section.getString("on-click")!!)
            "/say hi"
        )

        return format
    }

    private fun getChatColor(formatName: String): String {
        return Config.getString("global-chat.formats.$formatName.chat-color").orElse("&f")
    }

}