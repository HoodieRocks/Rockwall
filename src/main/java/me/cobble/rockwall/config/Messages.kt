package me.cobble.rockwall.config

import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.parties.parties.Party

/**
 * Utility class that gets messages from config
 */
object Messages {
    private var config = Config.getSection("messages")

    private fun getString(path: String): String {
        return Formats.color(config!!.getString(path)!!)
    }

    /**
     * Gets errors for when a person does not have a permission
     */
    fun getPermissionString(path: String): String {
        return getString("permission-messages.$path")
    }

    /**
     * Gets string from party config section
     * @param party party to pull placeholder data from
     */
    fun getPartyMsg(path: String, party: Party): String {
        return getString("parties.$path").replace("%party_alias%", party.alias, true)
    }

    /**
     * Gets string from party config section
     */
    fun getPartyMsg(path: String): String {
        return getString("parties.$path")
    }
}
