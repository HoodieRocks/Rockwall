package me.cobble.rockwall.rockwall

import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.groups.models.Group
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object Messages {
    private var config: FileConfiguration? = null
    private var plugin: Rockwall? = null
    private var file: File? = null

    fun init(plugin: Rockwall) {
        this.plugin = plugin
        file = File(Bukkit.getPluginManager().getPlugin("Rockwall")!!.dataFolder.toString() + "/messages.yml")
        plugin.saveResource("messages.yml", false)
        config = YamlConfiguration.loadConfiguration(file!!)
    }

    private fun getString(path: String): String {
        return Utils.color(config!!.getString(path)!!)
    }

    fun getPermissionString(path: String): String {
        return getString("permission-messages.$path")
    }

    fun getGroupString(path: String, group: Group): String {
        return getString("groups.$path").replace("%group_alias%", group.alias)
    }

    fun getGroupString(path: String): String {
        return getString("groups.$path")
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file!!)
        plugin!!.logger.info("Rockwall Message Config reloaded")
    }
}