package me.cobble.rockwall.cmds.parties

import me.cobble.rockwall.cmds.parties.subcmds.*
import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.PartyUtils
import me.cobble.rockwall.utils.parties.models.Party
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
        LeavePartySub()
    )

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (!Config.getBool("groups.enabled")) return false

        if (sender is Player) {
            if (args.isEmpty() || "help".equals(args[0], ignoreCase = true)) {
                sender.sendMessage(Utils.colorAndComponent("\n\n&e&lRockwall &7Party Commands\n"))
                val components =
                    Utils.formatAsFileStructure("/$commandLabel", subCommands.toList())

                sender.sendMessage(components)
                sender.sendMessage("\n\n")
            } else {
                for (subCommand in subCommands) {
                    if (subCommand.name == args[0]) {
                        subCommand.run(
                            sender,
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
                    PartyUtils.getUserParties(sender.uniqueId).forEach { list.add(it.alias) }
                }

                if (args[0].equals("accept", ignoreCase = true) || args[0].equals("deny", ignoreCase = true)) {
                    for (party: Party in PartyManager.getParties().values) {
                        if (party.isInvited(sender.uniqueId)) list.add(party.alias)
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