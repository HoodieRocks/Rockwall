package me.cobble.rockwall.cmds.parties

import me.cobble.rockwall.cmds.parties.subcmds.*
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.parties.Parties
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.parties.Party
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
        if (!Parties.arePartiesEnabled()) return false

        if (sender is Player) {
            if (args.isEmpty() || "help".equals(args[0], ignoreCase = true)) {
                sender.sendMessage(Formats.color("&e\n\n&lRockwall &7Party Commands \n<param> = required [param] = optional\n\n"))
                val components: Array<BaseComponent> = Formats.formatAsFileStructure(subCommands, "/$commandLabel")

                sender.spigot().sendMessage(*components)
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

                if (args[0].equals("msg", ignoreCase = true)) {
                    Parties.getUserParties(sender.uniqueId).forEach { list.add(it.alias) }
                    list.add("global")
                }

                if (args[0].equals("leave", ignoreCase = true) || args[0].equals("members", ignoreCase = true)) {
                    Parties.getUserParties(sender.uniqueId).forEach { list.add(it.alias) }
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
