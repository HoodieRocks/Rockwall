package me.cobble.rockwall.config.models

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.parties.models.Party

/**
 * Utility class that gets messages from config
 */
object Messages {
    private val config = Config.getSection("messages")

    private fun getString(path: String): String {
        return TextUtils.color(config!!.getString(path)!!)
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
