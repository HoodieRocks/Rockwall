package me.cobble.rockwall.config

object Emojis {
    val config = Config.getSection("emojis")
    private var emojis = setOf<Any>()

    /**
     * Gets all emojis from the list
     */
    fun getAllEmojis(): Set<Any> {
        return emojis.ifEmpty {
            emojis = config!!.keys
            emojis
        }
    }

    /**
     * Gets a single emoji from the config
     */
    fun getEmoji(path: String): String {
        return config!!.getString(path)!!
    }
}
