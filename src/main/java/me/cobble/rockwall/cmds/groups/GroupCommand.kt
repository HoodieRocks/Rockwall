package me.cobble.rockwall.cmds.groups

import me.cobble.rockwall.cmds.groups.subcmds.*
import me.cobble.rockwall.rockwall.Config
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.groups.GroupManager
import me.cobble.rockwall.utils.groups.GroupUtils
import me.cobble.rockwall.utils.groups.models.Group
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class GroupCommand : BukkitCommand("group", "Command for groups", "", listOf("g")) {

    private val subCommands = arrayListOf(
        CreateGroupSub(),
        InviteToGroupSub(),
        MessageGroupSub(),
        AcceptInviteSub(),
        DenyInviteSub(),
        DeleteGroupSub(),
        LeaveGroupSub()
    )

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (!Config.getBool("groups.enabled")) return false

        if (sender is Player) {
            val p = sender
            if (args.isEmpty() || "help".equals(args[0], ignoreCase = true)) {
                p.sendMessage(Utils.color("&e\n\n&lRockwall &7Group Commands\n\n"))
                val copy: MutableList<RockwallBaseCommand> = ArrayList(subCommands)
                for (subCommand: RockwallBaseCommand in subCommands) {
                    if (!p.hasPermission(subCommand.permission) || !p.isOp()) {
                        copy.remove(subCommand)
                    }
                }

                val components: Array<BaseComponent> = Utils.formatAsFileStructure(copy)

                p.spigot().sendMessage(*components)
                p.sendMessage("\n\n")
            } else {
                for (subCommand in subCommands) {
                    if (subCommand.name == args[0]) {
                        subCommand.run(
                            p,
                            args.drop(1).toTypedArray()
                        )
                        return true
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
                for (element: RockwallBaseCommand in subCommands) {
                    list.add(subCommands.indexOf(element), element.name)
                }
            }
            if (args.size == 2) {
                if (args[0].equals("invite", ignoreCase = true)) {
                    for (player: Player in Bukkit.getOnlinePlayers()) {
                        list.add(Bukkit.getOnlinePlayers().indexOf(player), player.name)
                    }
                }

                if (args[0].equals("msg", ignoreCase = true) || args[0].equals("leave", ignoreCase = true)) {
                    GroupUtils.getUsersGroups(sender.uniqueId).forEach { list.add(it.alias) }
                }

                if (args[0].equals("accept", ignoreCase = true) || args[0].equals("deny", ignoreCase = true)) {
                    for (group: Group in GroupManager.getGroups().values) {
                        if (group.isInvited(sender.uniqueId)) list.add(group.alias)
                    }
                }
            }

            if (args.size == 3) {
                if (args[0].equals("create", ignoreCase = true)) {
                    list.add("admin")
                    list.add("normal")
                }
            }
        }

        return list
    }
}