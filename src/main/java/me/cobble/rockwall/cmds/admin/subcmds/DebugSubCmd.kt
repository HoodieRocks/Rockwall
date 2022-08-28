package me.cobble.rockwall.cmds.admin.subcmds

import me.cobble.rockwall.cmds.admin.debug.StressTests
import me.cobble.rockwall.utils.RockwallBaseCommand
import me.cobble.rockwall.utils.TextUtils
import org.bukkit.entity.Player

class DebugSubCmd : RockwallBaseCommand {
    override val name: String
        get() = "debug"

    override val descriptor: String
        get() = "Enables debug commands for this session only"

    override val syntax: String
        get() = "[label] debug"

    override fun run(p: Player, args: Array<String>) {
        if (args.isEmpty()) {
            p.sendMessage(TextUtils.color("&c${syntax.replace("[label]", "/rockwall")}"))
        } else {
            if (args.size == 1) {
                if (args[0] == "stressTestParty") {
                    StressTests.parties(p)
                }

                if (args[0] == "stressTestGlobal") {
                    StressTests.chat(p)
                }
            }
        }
    }
}