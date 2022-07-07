package me.cobble.rockwall

import me.cobble.rockwall.utils.Config
import org.bukkit.plugin.java.JavaPlugin

class Rockwall : JavaPlugin() {

    val registry = RockwallRegistry(this)

    override fun onEnable() {
        // Plugin startup logic

        // Config Logic
        initConfig(this)

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
}