package me.cobble.rockwall.config

object Emojis {
    val config = Config.document

    fun getAllEmojis(): Set<Any> {
        return config!!.keys
    }

    fun getEmoji(path: String): String {
        return config!!.getString("emojis.$path")!!
    }
}
