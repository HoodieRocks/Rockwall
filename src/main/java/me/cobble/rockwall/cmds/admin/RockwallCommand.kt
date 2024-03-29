package me.cobble.rockwall.cmds.admin

import me.cobble.rockwall.cmds.admin.subcmds.CheckUpdateSub
import me.cobble.rockwall.cmds.admin.subcmds.DebugSubCmd
import me.cobble.rockwall.cmds.admin.subcmds.ReloadConfigSub
import me.cobble.rockwall.cmds.admin.subcmds.RockwallInfoSub
import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.FormatUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class RockwallCommand(val plugin: Rockwall) : TabExecutor {

    init {
        plugin.getCommand("rockwall")!!.setExecutor(this)
        plugin.getCommand("rockwall")!!.tabCompleter = this
    }


    private val subCommands = arrayListOf(
        ReloadConfigSub(),
        CheckUpdateSub(plugin),
        RockwallInfoSub(plugin),
        DebugSubCmd()
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

            if (args.size == 2) {
                if (args[0].equals("update", true)) {
                    list.add("download")
                }
                if (args[0].equals("debug", true)) {
                    list.add("stressTestParty")
                    list.add("stressTestGlobal")
                    list.add("enableDebugMsgs")
                }
            }
        }

        return list
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(ColorUtils.color("&cThis command cannot be used by console"))
            return false
        }

        if (sender.hasPermission("rockwall.admin")) {
            if (args.isEmpty() || "help".equals(args[0], true)) {
                sender.sendMessage(ColorUtils.color("&e\n\n&lRockwall &cAdmin &7Commands"))
                val components: Array<BaseComponent> =
                    FormatUtils.formatAsFileStructure(subCommands.toList(), "/$label")

                sender.spigot().sendMessage(*components)
                sender.sendMessage("\n\n")
                return true
            }

            for (subCommand in subCommands) {
                if (subCommand.name == args[0]) {
                    subCommand.run(
                        sender,
                        args.drop(1).toTypedArray()
                    )
                    return true
                }
            }

        } else {
            sender.sendMessage(ColorUtils.color(Messages.getPermissionString("no-perm-general")))
            return false
        }

        return false
    }
}
