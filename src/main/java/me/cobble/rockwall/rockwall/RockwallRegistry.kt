package me.cobble.rockwall.rockwall

import me.cobble.rockwall.cmds.admin.RockwallCommand
import me.cobble.rockwall.cmds.chat.ClearChatCommand
import me.cobble.rockwall.cmds.parties.PartyCommand
import me.cobble.rockwall.listeners.JoinLeaveListeners
import me.cobble.rockwall.listeners.SendGloballyListener
import me.cobble.rockwall.listeners.SendToPartyListener
import me.cobble.rockwall.utils.chat.ChatUtils
import me.cobble.rockwall.utils.parties.Parties
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap

class RockwallRegistry(private val plugin: Rockwall) {
    private var map: CommandMap? = null

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
            map!!.register(PREFIX, PartyCommand())
        }
        if (ChatUtils.isGlobalChatEnabled()) {
            plugin.logger.info("Registering global chat commands")
            map!!.register(PREFIX, ClearChatCommand())
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

    companion object {
        const val PREFIX = "rockwall"
    }
}
