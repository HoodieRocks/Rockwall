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
            map!!.register(PREFIX, GroupCommand())
        }
    }

    fun registerCommandExecutors() {
        RockwallCommand(plugin)
        if (GlobalChatUtils.isGlobalChatEnabled()) {
            ClearChatCommand(plugin)
        }
    }

    fun registerListeners() {
        SendGloballyListener(plugin)
        if (GroupUtils.areGroupsEnabled()) {
            SendToGroupListener(plugin)
        }
    }

    companion object {
        const val PREFIX = "rockwall"
    }
}