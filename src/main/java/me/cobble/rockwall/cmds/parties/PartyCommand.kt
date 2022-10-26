package me.cobble.rockwall.cmds.parties

import me.cobble.rockwall.cmds.parties.subcmds.*
import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.FormatUtils
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class PartyCommand : BukkitCommand("party", "Command for parties", "", listOf("par", "p", "part")) {

    private val subCommands = arrayListOf(
        CreatePartySub(),
        InviteToPartySub(),
        MessagePartySub(),
        AcceptInviteSub(),
        DenyInviteSub(),
        DeletePartySub(),
        LeavePartySub(),
        PartyMembersSub()
    )


    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (sender is Player) {

            if (!sender.hasPermission("rockwall.parties")) {
                sender.sendMessage(Messages.getPermissionString("no-perm-general"))
                return false
            }

            if (args.isEmpty() || "help".equals(args[0], true)) {
                sender.sendMessage(ColorUtils.color("&e\n\n&lRockwall &7Party Commands"))
                val components: Array<BaseComponent> = FormatUtils.formatAsFileStructure(subCommands, "/$commandLabel")

                sender.spigot().sendMessage(*components)
                sender.sendMessage("\n\n")
            } else {
                for (subCommand in subCommands) {
                    if (subCommand.name == args[0]) {
                        return subCommand.run(
                            sender,
                            args.drop(1).toTypedArray()
                        )
                    }
                }
            }
        }
        return false
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()

        if (sender is Player) {
            if (args.size == 1) {
                subCommands.forEach { list.add(subCommands.indexOf(it), it.name) }
            }
            if (args.size == 2) {
                if (args[0].equals("invite", true)) {
                    Bukkit.getOnlinePlayers().forEach { list.add(Bukkit.getOnlinePlayers().indexOf(it), it.name) }
                }

                if (args[0].equals("msg", true)) {
                    if (sender.hasPermission("rockwall.admin.joinany")) {
                        PartyUtils.getUserParties(sender.uniqueId).forEach { list.add(it.alias) }
                    } else {
                        PartyManager.getParties().values.forEach {
                            list.add(it.alias)
                        }
                    }
                    list.add("global")
                }

                if (args[0].equals("leave", true) || args[0].equals("members", true)) {
                    PartyUtils.getUserParties(sender.uniqueId).forEach { list.add(it.alias) }
                }

                if (args[0].equals("accept", true) || args[0].equals("deny", true)) {
                    PartyManager.getParties().values.forEach { if (it.isInvited(sender.uniqueId)) list.add(it.alias) }
                }
            }

            if (args.size == 3 && args[0].equals("create", true)) {
                list.add("admin")
                list.add("normal")
            }
        }

        return list
    }
}
