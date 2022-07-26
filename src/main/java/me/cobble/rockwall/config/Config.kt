package me.cobble.rockwall.config

import me.cobble.rockwall.rockwall.Rockwall
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.util.logging.Logger

object Config {
    private var plugin: Rockwall? = null
    private var log: Logger? = null
    private var file: File? = null
    private var config: YamlConfiguration? = null
    fun setup(plugin: Rockwall) {
        this.plugin = plugin
        this.log = plugin.logger
        file = File(plugin.dataFolder, "config.yml")

        if (!file!!.exists()) {
            log!!.info("No config found, Generating...")
            try {
                file!!.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        config = YamlConfiguration.loadConfiguration(file!!)
    }

    fun get(): FileConfiguration? {
        return config
    }

    fun getSection(path: String): ConfigurationSection? {
        return get()!!.getConfigurationSection(path)
    }

    fun getBool(path: String): Boolean {
        return get()!!.getBoolean(path)
    }

    fun getInt(path: String): Int {
        return get()!!.getInt(path)
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file!!)
        log!!.info("Rockwall Main Config reloaded")
    }

    fun moveAndUpdate() {
        val folder = File("${plugin!!.dataFolder}/old/")
        folder.mkdir()
        val oldFile = File("${plugin!!.dataFolder}/old/config-${LocalDate.now()}.yml.old")

        file!!.renameTo(oldFile)

        file = File(plugin!!.dataFolder, "config.yml")

        plugin!!.saveDefaultConfig()
        setup(plugin!!)
    }
}