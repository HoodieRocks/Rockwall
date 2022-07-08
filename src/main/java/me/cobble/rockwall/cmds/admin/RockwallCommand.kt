package me.cobble.rockwall.cmds.admin

import me.cobble.rockwall.cmds.admin.subcmds.ReloadConfigSub
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class RockwallCommand(plugin: Rockwall) : TabExecutor {

    init {
        plugin.getCommand("rockwall")!!.setExecutor(this)
        plugin.getCommand("rockwall")!!.tabCompleter = this
    }

    private val subCommands = arrayListOf(
        ReloadConfigSub()
    )

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        val list = mutableListOf<String>()

        if (sender is Player) {
            if (args.size == 1) {
                for (element: RockwallBaseCommand in subCommands) {
                    list.add(subCommands.indexOf(element), element.name)
                }
            }
        }

        return list
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if(sender.hasPermission("rockwall.admin")) {
                if (args.isEmpty() || "help".equals(args[0], ignoreCase = true)) {
                    sender.sendMessage(Utils.color("&e\n\n&lRockwall &cAdmin &7Commands\n\n"))
                    val copy: MutableList<RockwallBaseCommand> = ArrayList(subCommands)
                    for (subCommand: RockwallBaseCommand in subCommands) {
                        if (!sender.hasPermission(subCommand.permission) || !sender.isOp()) {
                            copy.remove(subCommand)
                        }
                    }

                    val components: Array<BaseComponent> = Utils.formatAsFileStructure(copy)

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
            } else {
                sender.sendMessage(Utils.color("&aYou do not have permission to run this command"))
            }
        }
        return false
    }
}