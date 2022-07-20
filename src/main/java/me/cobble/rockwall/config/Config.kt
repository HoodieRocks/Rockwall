package me.cobble.rockwall.config

import me.cobble.rockwall.rockwall.Rockwall
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.nio.file.Files

object Config {
    private val log = Bukkit.getLogger()
    private var file: File? = null
    private var config: YamlConfiguration? = null
    fun setup() {
        file = File(
            Bukkit.getServer().pluginManager.getPlugin("Rockwall")!!.dataFolder,
            "config.yml"
        )
        if (!file!!.exists()) {
            log.info("No config found, Generating...")
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

    fun getBool(path: String): Boolean {
        return get()!!.getBoolean(path)
    }

    fun getInt(path: String): Int {
        return get()!!.getInt(path)
    }

    fun save() {
        try {
            config!!.save(file!!)
        } catch (e: IOException) {
            System.err.println("Couldn't save file")
        }
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file!!)
        log.info("Rockwall Main Config reloaded")
    }

    fun update(plugin: Rockwall) {
        val version = getInt("config-version")
        if (plugin.description.version[0].digitToInt() > version) {
            plugin.logger.info("Old config found, moving to ${plugin.dataFolder.toString() + "/old/config.yml"}")
            val oldFolder = File(plugin.dataFolder.toString() + "/old/")
            oldFolder.mkdir()

            val oldConfig = File(plugin.dataFolder.toString() + "/old/config.yml")

            Files.move(file!!.toPath(), oldConfig.toPath())
            plugin.initConfig()
        }
    }
}