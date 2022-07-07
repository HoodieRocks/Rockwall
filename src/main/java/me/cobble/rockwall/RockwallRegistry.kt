package me.cobble.rockwall

import me.cobble.rockwall.cmds.ClearChatCommand
import me.cobble.rockwall.cmds.GroupCommand
import me.cobble.rockwall.listeners.SendGloballyListener
import me.cobble.rockwall.listeners.SendToGroupListener
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
        map!!.register(PREFIX, GroupCommand())
    }

    fun registerCommandExecutors() {
        ClearChatCommand(plugin)
    }

    fun registerListeners() {
        SendGloballyListener(plugin)
        SendToGroupListener(plugin)
    }

    companion object {
        const val PREFIX = "rockwall"
    }
}