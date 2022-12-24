package me.cobble.rockwall.cmds.admin.debug

import me.cobble.rockwall.config.FormatsConfig
import me.cobble.rockwall.config.models.ChatFormatType
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.FormatUtils
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.models.Party
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import java.util.*
import kotlin.system.measureTimeMillis

object StressTests {

    fun parties(player: Player) {
        player.sendMessage(
            ColorUtils.color(
                "&eThis test represents 10K parties at the same time, " +
                        "\nThis is more a performance test than an example of actual use"
            )
        )
        player.sendMessage(ColorUtils.color("&cTesting create() with 10K entries"))

        val localCopy = linkedMapOf<UUID, Party>()

        val totalTime = System.currentTimeMillis()

        var elapsed = measureTimeMillis {
            repeat(10_000) {
                val uuid = UUID.randomUUID()
                localCopy[uuid] = PartyManager.createParty(uuid, FormatUtils.randomString(10), PartyType.NORMAL)

            }
        }
        player.sendMessage(ColorUtils.color("&eTook &d${elapsed}ms &eto create 10K party objects"))

        player.sendMessage(ColorUtils.color("&7Getting all 10K objects via UUID"))

        elapsed = measureTimeMillis {
            localCopy.forEach {
                PartyManager.getParty(it.key)
            }

        }
        player.sendMessage(ColorUtils.color("&eTook &d${elapsed}ms &eto get 10K objects via UUID"))

        player.sendMessage(ColorUtils.color("&7Getting all 10K objects via name (expensive)"))
        elapsed = measureTimeMillis {
            localCopy.forEach {
                PartyManager.getParty(it.value.alias)
            }
        }
        player.sendMessage(ColorUtils.color("&eTook &d${elapsed}ms &eto get 10K objects via name"))

        player.sendMessage(ColorUtils.color("&7Deleting all 10k objects"))
        elapsed = measureTimeMillis {
            localCopy.forEach {
                PartyManager.deleteParty(it.value)
            }
        }
        player.sendMessage(ColorUtils.color("&eTook &d${elapsed}ms &eto delete 10K objects"))

        localCopy.clear()

        player.sendMessage(ColorUtils.color("&aTotal time of ${System.currentTimeMillis() - totalTime}ms"))
    }

    fun chat(player: Player) {
        val totalTime = System.currentTimeMillis()
        if (PartyUtils.getPartyBySpeaking(player.uniqueId) == null) {
            player.sendMessage(
                ColorUtils.color(
                    "&cThis test represents 10K chat messages at the same time" +
                            "\nThis is more a performance test than an example of actual use"
                )
            )
            player.sendMessage(ColorUtils.color("&7Generating 10K fake UUIDs"))

            val uuids = linkedSetOf<UUID>()

            repeat(10_000) {
                uuids.add(UUID.randomUUID())
            }

            player.sendMessage(ColorUtils.color("&7Generating format objects"))


            // config format components
            val prefix = arrayListOf<TextComponent?>()
            val prefixSeparator = arrayListOf<TextComponent?>()
            val name = arrayListOf<TextComponent?>()
            val nameSeparator = arrayListOf<TextComponent?>()
            val chatColor = arrayListOf<String>()


            val permission = ChatUtils.getFormatByPermission(player)

            var elapsed = measureTimeMillis {
                repeat(10_000) {
                    chatColor.add(getChatColor(permission))
                    prefix.add(makeFormat(permission, ChatFormatType.PREFIX))
                    prefixSeparator.add(makeFormat(permission, ChatFormatType.PREFIX_SEPARATOR))
                    name.add(makeFormat(permission, ChatFormatType.NAME))
                    nameSeparator.add(makeFormat(permission, ChatFormatType.NAME_SEPARATOR))
                }
            }

            player.sendMessage(ColorUtils.color("&eTook &d${elapsed}ms &eto make format objects"))

            player.sendMessage(ColorUtils.color("&7Combining format objects"))
            elapsed = measureTimeMillis {
                for (i in 0 until uuids.size) {
                    ComponentBuilder()
                        .append(prefix[i])
                        .append(prefixSeparator[i])
                        .append(name[i])
                        .append(nameSeparator[i])
                        .append(
                            chatColor[i] +
                                    ColorUtils.colorizeComponents(
                                        ChatUtils.processMessageFeatures(
                                            FormatUtils.randomString(25),
                                            player
                                        ), player
                                    )
                        )
                        .create()
                }
            }
            player.sendMessage(ColorUtils.color("&eTook &d${elapsed}ms &eto combine format objects"))

            uuids.clear()
        }
        player.sendMessage(ColorUtils.color("&aTotal time of ${System.currentTimeMillis() - totalTime}ms"))

    }

    private fun makeFormat(formatName: String, type: ChatFormatType): TextComponent? {
        if (formatName.isBlank()) return null
        val configSection = FormatsConfig.getSection("global-chat-formats.$formatName") ?: return null
        val section = configSection.getSection(type.getType())
        val format = ColorUtils.colorizeComponents(section.getString("display")!!)

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

    private fun getChatColor(formatName: String): String =
        FormatsConfig.getString("global-chat-formats.$formatName.chat-color").orElse("&f")

}