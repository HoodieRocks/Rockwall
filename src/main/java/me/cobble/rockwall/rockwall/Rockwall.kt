package me.cobble.rockwall.rockwall

import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.groups.GroupManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Rockwall : JavaPlugin() {

    private val registry = RockwallRegistry(this)

    override fun onEnable() {
        // Plugin startup logic

        // Config Logic
        initConfig()

        // Is PlaceholderAPI present?
        setPlaceholderAPIPresent()

        // Registers components
        logger.info("Registering components...")
        registry.registerBukkitCommands()
        registry.registerCommandExecutors()
        registry.registerListeners()

        // Group timer registration
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            GroupManager.tickTimers()
        }, 20, 20)

        logger.info("Components registered!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun setPlaceholderAPIPresent() {
        logger.info("Checking if PlaceholderAPI is present...")
        val present = (Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI") != null)
        Utils.placeholderAPIPresent = present
        if (!present) logger.warning("Placeholder support disabled due to missing PlaceholderAPI, please install PlaceholderAPI to use placeholders")
        else logger.info("PlaceholderAPI found!")
    }

    private fun initConfig() {
        logger.info("Loading configs...")
        this.saveDefaultConfig()
        Config.setup()
        Messages.init(this)
        logger.info("Loaded configs...")
    }
}