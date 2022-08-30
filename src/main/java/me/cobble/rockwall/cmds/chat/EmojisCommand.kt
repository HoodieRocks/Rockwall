package me.cobble.rockwall.cmds.chat

import me.cobble.rockwall.config.models.Features
import me.cobble.rockwall.utils.TextUtils
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand

class EmojisCommand : BukkitCommand("emojis", "Shows all the available emojis for this server", "/emojis", listOf()) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        sender.sendMessage(TextUtils.color("&e&lEmojis"))
        for (emoji in Features.getAllEmojis()) {
            sender.sendMessage(
                TextUtils.color(
                    "&7${(emoji as String).replaceFirstChar { it.uppercaseChar() }}: ${
                        Features.getEmoji(
                            emoji
                        )
                    }"
                )
            )
        }
        return true
    }
}