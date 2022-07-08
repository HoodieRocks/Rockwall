package me.cobble.rockwall.rockwall

import me.cobble.rockwall.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Rockwall : JavaPlugin() {

    private val registry = RockwallRegistry(this)

    override fun onEnable() {
        // Plugin startup logic

        // Config Logic
        initConfig(this)
        setPlaceholderAPIPresent()

        registry.registerBukkitCommands()
        registry.registerCommandExecutors()
        registry.registerListeners()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun initConfig(plugin: Rockwall) {
        plugin.saveDefaultConfig()
        Config.setup()
    }

    private fun setPlaceholderAPIPresent() {
        val present = (Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI") != null)
        Utils.placeholderAPIPresent = present
        if(!present) {
            logger.warning("Placeholder support disabled due to missing PlaceholderAPI, please install PlaceholderAPI to use placeholders")
        }
    }
}