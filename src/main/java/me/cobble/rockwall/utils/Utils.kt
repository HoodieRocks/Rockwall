package me.cobble.rockwall.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import me.clip.placeholderapi.PlaceholderAPI
import me.cobble.rockwall.config.Config
import me.cobble.rockwall.rockwall.Rockwall
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object Utils {
    private const val WITH_DELIMITER = "((?<=%1\$s)|(?=%1\$s))"
    var placeholderAPIPresent = false
    private var updateVersion: String? = null

    fun color(text: String): String {
        val texts: Array<String> =
            text.split(String.format(WITH_DELIMITER, "&").toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val finalText = StringBuilder()
        var i = 0
        while (i < texts.size) {
            if ("&".equals(texts[i], ignoreCase = true)) {
                //get the next string
                i++
                if (texts[i][0] == '#') {
                    finalText.append(ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7))
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]))
                }
            } else {
                finalText.append(texts[i])
            }
            i++
        }
        return finalText.toString()
    }

    /**
     * Same as Utils#color(text), but requires permission to use color
     * @see Utils.color
     */
    fun color(text: String, player: Player): String {
        if (player.hasPermission("rockwall.color")) {
            val texts: Array<String> =
                text.split(String.format(WITH_DELIMITER, "&").toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val finalText = StringBuilder()
            var i = 0
            while (i < texts.size) {
                if ("&".equals(texts[i], ignoreCase = true)) {
                    //get the next string
                    i++
                    if (texts[i][0] == '#') finalText.append(ChatColor.of(texts[i].substring(0, 7)))
                        .append(texts[i].substring(7))
                    else finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]))
                } else {
                    finalText.append(texts[i])
                }
                i++
            }
            return finalText.toString()
        }
        return text
    }

    /**
     * Formats sub commands as a file structure display
     */
    fun formatAsFileStructure(label: String, list: List<RockwallBaseCommand>): Component {
        val components = Component.text()
        val sortedList = list.sortedBy { it.name }

        for (i in sortedList.indices) {
            val sc: RockwallBaseCommand = sortedList[i]
            val format = "&e${sc.syntax.replace("[label]", label)} &7- ${sc.descriptor}"
            when (i) {
                0 -> components.add(addEvents(formatCmd("┌ ", format), sc.descriptor, sc.syntax))
                list.size - 1 -> components.add(addEvents(formatCmd("└ ", format), sc.descriptor, sc.syntax))
                else -> components.add(addEvents(formatCmd("├ ", format), sc.descriptor, sc.syntax))
            }
        }
        return components.toTypedArray()
    }

    /**
     * Adds click and hover events
     */
    private fun addEvents(text: String?, hoverText: String?, command: String?): BaseComponent {
        val component = TextComponent(text)
        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverText))
        component.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        return component
    }

    private fun formatCmd(prefix: String, cmd: String): String {
        return color("&e$prefix&7$cmd\n")
    }

    /**
     * Checks if string is non-alphanumeric
     * @return if string is alphanumeric
     */
    fun String.containsSpecialCharacters(): Boolean {
        return this.contains(Regex("[^A-Za-z0-9]"))
    }

    /**
     * Flattens a string list to a single string
     * @return string list as string
     */
    fun flattenList(list: List<String>): String {
        val builder: StringBuilder = StringBuilder()
        list.forEach {
            builder.append(it)
            if (list.indexOf(it) != list.size - 1) builder.append('\n')
            if (Config.getBool("settings.reset-color-on-new-line")) builder.append("&r&f")
        }

        return builder.toString()
    }

    /**
     * Uses PlaceholderAPI placeholders if present,
     * if not it returns the inputted string
     */
    fun setPlaceholders(player: Player, string: String): String {
        return if (placeholderAPIPresent) PlaceholderAPI.setPlaceholders(player, string)
        else string
    }

    // END OF UTILS FILE

    /**
     * Utils relating to update checking
     */
    class UpdateUtils(private val plugin: Rockwall) {

        /**
         * @return true if update available
         */
        fun retrieveUpdateData(): Boolean {
            var updateAvailable = false
            val gson = Gson()

            val client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build()

            val request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.spiget.org/v2/resources/103709/versions?size=20"))
                .build()

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept {
                val array = gson.fromJson(it.body(), JsonArray::class.java)
                val retrievedVersion = array[array.size() - 1].asJsonObject["name"].asString
                println(plugin.description.version)
                println(retrievedVersion)

                updateAvailable = retrievedVersion != plugin.description.version
                updateVersion = retrievedVersion
            }

            return updateAvailable
        }

        fun updateAvailable(): Boolean {
            return updateVersion != plugin.description.version
        }

        fun sendUpdateAvailableMsg(player: Player) {
            if (player.hasPermission("rockwall.admin")) {
                player.sendMessage(color("&e&lUpdate available!"))
                player.sendMessage(color("&7There is an update available for Rockwall"))
                player.sendMessage(color("&7Your version: &c${plugin.description.version} &8→ &7Newest version: &a${updateVersion}"))
                player.sendMessage(color("&7Download at &6&nhttps://www.spigotmc.org/resources/rockwall.103709/"))
            }
        }
    }
}