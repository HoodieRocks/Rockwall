package me.cobble.rockwall.cmds.admin

import me.cobble.rockwall.cmds.admin.subcmds.CheckUpdateSub
import me.cobble.rockwall.cmds.admin.subcmds.ConfigUpdateSub
import me.cobble.rockwall.cmds.admin.subcmds.ReloadConfigSub
import me.cobble.rockwall.cmds.admin.subcmds.RockwallInfoSub
import me.cobble.rockwall.config.Messages
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
        ReloadConfigSub(),
        CheckUpdateSub(plugin),
        RockwallInfoSub(plugin),
        ConfigUpdateSub()
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
            if (sender.hasPermission("rockwall.admin")) {
                if (args.isEmpty() || "help".equals(args[0], ignoreCase = true)) {
                    sender.sendMessage(Utils.color("&e\n\n&lRockwall &cAdmin &7Commands\n\n"))
<<<<<<< HEAD
                    val components: Array<BaseComponent> = Utils.formatAsFileStructure(subCommands.toList())
=======
                    val components: Array<BaseComponent> = Utils.formatAsFileStructure("/$label", subCommands.toList())
>>>>>>> parent of d7d1460 (added basic support for adventure api)

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
                sender.sendMessage(Utils.color(Messages.getPermissionString("no-perm-general")))
            }
        }
        return false
    }
}