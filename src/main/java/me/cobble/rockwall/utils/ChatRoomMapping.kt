package me.cobble.rockwall.utils

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.Features
import me.cobble.rockwall.utils.models.Manager

object ChatRoomMapping : Manager<String, String>() {

    fun buildMapping() {
        if (Features.areChatroomsEnabled()) {
            Features.getChatRoomSection().keys.forEach {
                addOrUpdate(Config.getString("chat-rooms.$it.prefix").orElseThrow(), it as String)
            }
        }
    }

    fun getName(prefix: String): String {
        return get(prefix)!!
    }

    fun getPrefixes(): Set<String> {
        return getAll().keys
    }
}