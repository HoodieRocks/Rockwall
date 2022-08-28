package me.cobble.rockwall.cmds.admin.debug

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.ChatFormatType
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.parties.Party
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import java.util.*

object StressTests {

    fun parties(player: Player) {
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
        if (Parties.getPartyBySpeaking(player.uniqueId) == null) {
            player.sendMessage("Testing time to get format perm")
            var time = System.currentTimeMillis()
            val permission = ChatUtils.getFormatByPermission(player)
            player.sendMessage("Took ${System.currentTimeMillis() - time}ms to get format permission")

            player.sendMessage("Testing time to make format objects")
            time = System.currentTimeMillis()
            // config format components
            val prefix = makeFormat(player, permission, ChatFormatType.PREFIX)
            val prefixSeparator = makeFormat(player, permission, ChatFormatType.PREFIX_SEPARATOR)
            val name = makeFormat(player, permission, ChatFormatType.NAME)
            val nameSeparator = makeFormat(player, permission, ChatFormatType.NAME_SEPARATOR)
            player.sendMessage("Took ${System.currentTimeMillis() - time}ms to make format objects")

            player.sendMessage("Testing time to combine format objects")
            time = System.currentTimeMillis()
            ComponentBuilder()
                .append(prefix)
                .append(prefixSeparator)
                .append(name)
                .append(nameSeparator)
                .append(
                    TextUtils.colorToTextComponent(
                        ChatUtils.processMessageFeatures(
                            TextUtils.randomString(25),
                            player
                        ), player
                    )
                )
                .create()
            player.sendMessage("Took ${System.currentTimeMillis() - time}ms to combine format objects")


            // sending not included
        }
        player.sendMessage("Total time of ${System.currentTimeMillis() - totalTime}ms")

    }

    private fun makeFormat(player: Player, formatName: String, type: ChatFormatType): TextComponent? {
        if (formatName.isBlank()) return null
        val configSection = Config.getSection("global-chat.formats.$formatName") ?: return null
        val section = configSection.getSection(type.getType())
        val format = TextUtils.colorToTextComponent(TextUtils.setPlaceholders(player, section!!.getString("display")!!))

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

}