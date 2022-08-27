package me.cobble.rockwall.rockwall

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.UpdateUtils
import me.cobble.rockwall.utils.parties.PartyManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Rockwall : JavaPlugin() {

    private val registry = RockwallRegistry(this)
    private val updateUtils = UpdateUtils(this)

    override fun onEnable() {
        // Plugin startup logic

        // Config Logic
        logger.info("Loading config...")
        Config.setup(this)
        logger.info("Config has been loaded!")

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
        TextUtils.placeholderAPIPresent = present
        if (!present) logger.warning("Placeholder support disabled due to missing PlaceholderAPI, please install PlaceholderAPI to use placeholders")
        else logger.info("PlaceholderAPI found!")
    }

    private fun registerComponents() {
        registry.registerBukkitCommands()
        registry.registerCommandExecutors()
        registry.registerListeners()
    }

    fun getUpdateUtils(): UpdateUtils {
        return updateUtils
    }
}
