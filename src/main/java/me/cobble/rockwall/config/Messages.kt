package me.cobble.rockwall.config

import dev.dejvokep.boostedyaml.YamlDocument
import me.cobble.rockwall.utils.Formats
import me.cobble.rockwall.utils.parties.models.Party

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
