package me.cobble.rockwall.rockwall

import me.cobble.rockwall.cmds.admin.RockwallCommand
import me.cobble.rockwall.cmds.global.ClearChatCommand
import me.cobble.rockwall.cmds.groups.PartyCommand
import me.cobble.rockwall.listeners.SendGloballyListener
import me.cobble.rockwall.listeners.SendToPartyListener
import me.cobble.rockwall.utils.chat.ChatUtils
import me.cobble.rockwall.utils.parties.PartyUtils
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
        if (PartyUtils.arePartiesEnabled()) {
            plugin.logger.info("Registering group commands")
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

        if (PartyUtils.arePartiesEnabled()) {
            plugin.logger.info("Registering group listeners")
            SendToPartyListener(plugin)
        }
    }

    companion object {
        const val PREFIX = "rockwall"
    }
}