package me.cobble.rockwall.rockwall

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Rockwall : JavaPlugin() {

    private val registry = RockwallRegistry(this)
    private val updateUtils = Utils.UpdateUtils(this)

    override fun onEnable() {
        // Plugin startup logic

        // Config Logic
        initConfig()

        // Is PlaceholderAPI present?
        setPlaceholderAPIPresent()

        // Registers components
        logger.info("Registering components...")
        registerComponents()

        // party timer registration
        Bukkit.getScheduler().runTaskTimer(
            this,
            Runnable {
                PartyManager.tickTimers()
            },
            20,
            20
        )

        logger.info("Components registered!")

        updateUtils.retrieveUpdateData()
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
        Config.setup(this)
        Messages.init(this)
        logger.info("Loaded configs...")
    }

    private fun registerComponents() {
        registry.registerBukkitCommands()
        registry.registerCommandExecutors()
        registry.registerListeners()
    }

    fun getUpdateUtils(): Utils.UpdateUtils {
        return updateUtils
    }
}
