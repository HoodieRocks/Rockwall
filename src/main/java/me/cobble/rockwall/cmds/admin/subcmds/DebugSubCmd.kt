package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.cmds.admin.debug.StressTests
import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.models.RockwallBaseCommand
import org.bukkit.entity.Player

class DebugSubCmd : RockwallBaseCommand {
    override val name: String
        get() = "debug"

    override val descriptor: String
        get() = "Enables debug commands for this session only"

    override val syntax: String
        get() = "[label] debug"

    override fun run(p: Player, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/rockwall")}"))
            return false
        } else {
            if (args.size == 1) {
                return if (args[0] == "stressTestGlobal") {
                    StressTests.chat(p)
                    true
                } else if (args[0] == "stressTestParty") {
                    StressTests.parties(p)
                    true
                } else {
                    p.sendMessage(Messages.getGeneralError("unknown-argument"))
                    false
                }

            } else {
                p.sendMessage(Messages.getGeneralError("too-many-args"))
                return false
            }
        }
    }
}