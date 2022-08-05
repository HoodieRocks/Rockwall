package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.Utils
import org.bukkit.entity.Player

class ConfigUpdateSub : RockwallBaseCommand() {
    override val name: String
        get() = "updateconfig"
    override val descriptor: String
        get() = "Update your config to a newer version (this will reset your previous settings)"
    override val syntax: String
        get() = "[label] updateconfig [confirm]"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(Utils.color("&cAre you sure you want to update the config? This action resets your existing settings!"))
            p.sendMessage(Utils.color("&cYour previous config will be saved, but you'll need to configure the updated config"))
            p.sendMessage(Utils.color("&cTo confirm, please run /rockwall updateconfig confirm"))
        } else {
            Config.moveAndUpdate()
            Messages.moveAndUpdate()
            p.sendMessage(Utils.color("&aConfigs have been successfully updated, you should probably configure the updated config!"))
        }
    }
}