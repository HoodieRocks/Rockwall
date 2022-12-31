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
        if (sender !is Player) {
            sender.sendMessage(ColorUtils.color("&cThis command cannot be used by console"))
            return false
        }

        if (!sender.hasPermission("rockwall.parties")) {
            sender.sendMessage(Messages.getPermissionString("no-perm-general"))
            return false
        }

        if (args.isEmpty() || "help".equals(args[0], true)) {
            sender.sendMessage(ColorUtils.color("&e\n\n&lRockwall &7Party Commands"))
            val components: Array<BaseComponent> = FormatUtils.formatAsFileStructure(subCommands, "/$commandLabel")

            sender.spigot().sendMessage(*components)
            sender.sendMessage("\n\n")
            return true
        }

        for (subCommand in subCommands) {
            if (subCommand.name == args[0]) return subCommand.run(sender, args.drop(1).toTypedArray())
        }

        return false
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()

        if (args.size == 1) subCommands.forEach { list.add(subCommands.indexOf(it), it.name) }

        if (args.size == 2 && sender is Player) {
            when (args[0].lowercase()) {
                "invite" -> Bukkit.getOnlinePlayers()
                    .forEach { list.add(Bukkit.getOnlinePlayers().indexOf(it), it.name) }

                "msg" -> {
                    if (sender.hasPermission("rockwall.admin.joinany")) {
                        PartyUtils.getUserParties(sender.uniqueId).forEach { list.add(it.alias) }
                        list.add("global")
                    } else {
                        PartyManager.getParties().values.forEach {
                            list.add(it.alias)
                        }
                    }
                }

                "create" -> list.clear()
                "leave", "members" -> PartyUtils.getUserParties(sender.uniqueId).forEach { list.add(it.alias) }
                "accept", "deny" -> PartyManager.getParties().values.forEach {
                    if (it.isInvited(sender.uniqueId)) list.add(
                        it.alias
                    )
                }
            }
        }

        if (args.size == 3 && args[0].equals("create", true)) {
            list.add("admin")
            list.add("normal")
        }
        return list
    }
}
