package me.cobble.rockwall.rockwall

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

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

    fun getString(path: String): String {
        return get()!!.getString(path)!!
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
}