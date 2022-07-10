package me.cobble.rockwall.rockwall

import me.cobble.rockwall.cmds.admin.RockwallCommand
import me.cobble.rockwall.cmds.global.ClearChatCommand
import me.cobble.rockwall.cmds.groups.GroupCommand
import me.cobble.rockwall.listeners.SendGloballyListener
import me.cobble.rockwall.listeners.SendToGroupListener
import me.cobble.rockwall.utils.global.GlobalChatUtils
import me.cobble.rockwall.utils.groups.GroupUtils
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
        if (GroupUtils.areGroupsEnabled()) {
            plugin.logger.info("Registering group commands")
            map!!.register(PREFIX, GroupCommand())
        }
        if (GlobalChatUtils.isGlobalChatEnabled()) {
            plugin.logger.info("Registering global chat commands")
            map!!.register(PREFIX, ClearChatCommand())
        }
    }

    fun registerCommandExecutors() {
        RockwallCommand(plugin)
    }

    fun registerListeners() {
        if (GlobalChatUtils.isGlobalChatEnabled()) {
            plugin.logger.info("Registering global chat listeners")
            SendGloballyListener(plugin)
        }

        if (GroupUtils.areGroupsEnabled()) {
            plugin.logger.info("Registering group listeners")
            SendToGroupListener(plugin)
        }
    }

    companion object {
        const val PREFIX = "rockwall"
    }
}