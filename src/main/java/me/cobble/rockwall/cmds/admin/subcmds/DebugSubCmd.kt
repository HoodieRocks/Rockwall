package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.cmds.admin.debug.StressTests
import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.ColorUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import org.bukkit.entity.Player

class DebugSubCmd : RockwallBaseCommand {
    override val name: String
        get() = "debug"

    override val descriptor: String
        get() = "Debugging commands"

    override val syntax: String
        get() = "[label] debug <value>"

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            p.sendMessage(ColorUtils.color("&c${syntax.replace("[label]", "/rockwall")}"))
            return false
        }

        if (args.size == 1) {
            return when (args[0]) {
                "stressTestGlobal" -> {
                    StressTests.chat(p)
                    true
                }

                "stressTestParty" -> {
                    StressTests.parties(p)
                    true
                }

                "enableDebugMsgs" -> {
                    Config.setDebug(!Config.isDebugEnabled())
                    if (Config.isDebugEnabled()) p.sendMessage(ColorUtils.color("&aEnabled debug messages"))
                    else p.sendMessage(ColorUtils.color("&cDisabled debug messages"))
                    true
                }

                else -> {
                    p.sendMessage(Messages.getGeneralError("unknown-argument"))
                    false
                }
            }

        } else {
            p.sendMessage(Messages.getGeneralError("too-many-args"))
            return false
        }
    }
}