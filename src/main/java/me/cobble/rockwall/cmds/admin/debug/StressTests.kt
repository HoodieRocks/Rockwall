package me.cobble.rockwall.cmds.admin.debug

import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.ColorUtils.sendDebug
import me.cobble.rockwall.utils.FormatUtils
import me.cobble.rockwall.utils.models.FormatTree
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.models.Party
import me.cobble.rockwall.utils.parties.models.PartyType
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
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
        val totalTime = measureTimeMillis {
            var elapsed: Long
            val tree = FormatTree("global-chat-formats")

            player.sendDebug("getting permissions")
            var permission = ""
            elapsed = measureTimeMillis {
                repeat(10_000) {
                    permission = ChatUtils.getFormatByPermission(player)
                }
            }
            player.sendMessage(ColorUtils.color("&eTook &d${elapsed}&7ms &eto get permissions 10k times"))

            // config format components
            var chatColor = ""
            elapsed = measureTimeMillis {
                repeat(10_000) {
                    chatColor = tree.getGroupChatColor(permission).orElse("&f")
                }
            }
            player.sendMessage(ColorUtils.color("&eTook &d${elapsed}&7ms &eto get chat color 10k times"))

            player.sendDebug("assembling message")
            var assembledMessage: ComponentBuilder
            var colorizedComponent = TextComponent()
            elapsed = measureTimeMillis {
                repeat(10_000) {
                    colorizedComponent = ColorUtils.colorSpigot(
                        chatColor + ChatUtils.processMessageFeatures(
                            FormatUtils.randomString(10),
                            player
                        ), player
                    )
                }
            }
            player.sendMessage(ColorUtils.color("&eTook &d${elapsed}&7ms &eto colorize 10k messages"))

            elapsed = measureTimeMillis {
                repeat(10_000) {
                    assembledMessage =
                        FormatUtils.assembleMessage(
                            colorizedComponent, tree, permission, player
                        )

                    assembledMessage.create()
                }
            }
            player.sendMessage(ColorUtils.color("&eTook &d${elapsed}&7ms &eto assemble 10k messages"))
        }
        player.sendMessage(ColorUtils.color("&eTook &d${totalTime}&7ms &eto create and complete test of 10k messages"))
    }


}