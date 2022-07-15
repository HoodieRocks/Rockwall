package me.cobble.rockwall.cmds.global

import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand

class MsgCommand : BukkitCommand("msg", "Message players", "", listOf("w","whisper","tell","t","message","dm","pm")) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        // TODO: implement
        return false
    }
}