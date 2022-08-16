package me.cobble.rockwall.rockwall

import me.cobble.rockwall.cmds.admin.RockwallCommand
import me.cobble.rockwall.cmds.chat.ClearChatCommand
import me.cobble.rockwall.cmds.chat.EmojisCommand
import me.cobble.rockwall.cmds.parties.PartyCommand
import me.cobble.rockwall.listeners.JoinLeaveListeners
import me.cobble.rockwall.listeners.SendGloballyListener
import me.cobble.rockwall.listeners.SendToPartyListener
import me.cobble.rockwall.utils.ChatUtils
import me.cobble.rockwall.utils.parties.Parties
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap

class RockwallRegistry(private val plugin: Rockwall) {
    private var map: CommandMap? = null
    private val prefix = "rockwall"

    init {
        try {
            val bukkitCommandMap = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            bukkitCommandMap.isAccessible = true
            map = bukkitCommandMap[Bukkit.getServer()] as CommandMap
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    fun registerBukkitCommands() {
        if (Parties.arePartiesEnabled()) {
            plugin.logger.info("Registering party commands")
            map!!.register(prefix, PartyCommand())
        }
        if (ChatUtils.isGlobalChatEnabled()) {
            plugin.logger.info("Registering global chat commands")
            map!!.register(prefix, ClearChatCommand())
            map!!.register(prefix, EmojisCommand())
        }
    }

    fun registerCommandExecutors() {
        RockwallCommand(plugin)
    }

    fun registerListeners() {
        if (ChatUtils.isGlobalChatEnabled()) {
            plugin.logger.info("Registering global chat listeners")
            SendGloballyListener(plugin)
        }

        if (Parties.arePartiesEnabled()) {
            plugin.logger.info("Registering party listeners")
            SendToPartyListener(plugin)
            JoinLeaveListeners(plugin)
        }
    }
}
