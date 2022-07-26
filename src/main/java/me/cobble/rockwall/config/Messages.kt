package me.cobble.rockwall.config

import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.parties.models.Party
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.time.LocalDate

object Messages {
    private var config: FileConfiguration? = null
    private var plugin: Rockwall? = null
    private var file: File? = null

    fun init(plugin: Rockwall) {
        Messages.plugin = plugin
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

    /**
     * Gets string from group config section
     */
    fun getPartyMsg(path: String, party: Party): String {
        return getString("parties.$path").replace("%party_alias%", party.alias)
    }

    fun getPartyMsg(path: String): String {
        return getString("parties.$path")
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file!!)
        plugin!!.logger.info("Rockwall Message Config reloaded")
    }

    fun moveAndUpdate() {
        val folder = File("${plugin!!.dataFolder}/old/")
        folder.mkdir()
        val oldFile = File("${plugin!!.dataFolder}/old/messages-${LocalDate.now()}.yml.old")

        file!!.renameTo(oldFile)

        plugin!!.saveDefaultConfig()
        init(plugin!!)
    }
}