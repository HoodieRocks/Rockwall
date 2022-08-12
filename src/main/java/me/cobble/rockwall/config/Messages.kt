package me.cobble.rockwall.config

import dev.dejvokep.boostedyaml.YamlDocument
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.parties.models.Party
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.time.LocalDate

object Messages {
    private var config: YamlDocument? = Config.document

    private fun getString(path: String): String {
        return Formats.color(config!!.getString(path)!!)
    }

    fun getPermissionString(path: String): String {
        return getString("messages.permission-messages.$path")
    }

    /**
     * Gets string from party config section
     */
    fun getPartyMsg(path: String, party: Party): String {
        return getString("messages.parties.$path").replace("%party_alias%", party.alias)
    }

    fun getPartyMsg(path: String): String {
        return getString("messages.parties.$path")
    }
}
