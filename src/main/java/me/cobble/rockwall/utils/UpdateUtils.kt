package me.cobble.rockwall.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import me.cobble.rockwall.rockwall.Rockwall
import org.bukkit.entity.Player
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration


/**
 * Really primitive update checker, will likely be improved in the future
 */
// TODO: Improve the update checker
class UpdateUtils(private val plugin: Rockwall) {
    private var updateVersion: String? = null
    private val id = 103709
    private val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    /**
     * @return true if update available
     */
    fun retrieveUpdateData(): Boolean {
        var updateAvailable = false
        val gson = Gson()

        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.spiget.org/v2/resources/$id/versions"))
            .build()

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept {
            val array = gson.fromJson(it.body(), JsonArray::class.java)
            val retrievedVersion = array[array.size() - 1].asJsonObject["name"].asString

            updateAvailable = retrievedVersion != plugin.description.version
            updateVersion = retrievedVersion
        }

        if (updateAvailable) {
            plugin.logger.info("Update available! Please update Rockwall to the latest version!")
        } else {
            plugin.logger.info("No update available!")
        }

        return updateAvailable
    }

    /**
     * Checks if there is an update available
     */
    fun updateAvailable(): Boolean {
        return updateVersion != plugin.description.version
    }

    fun sendUpdateAvailableMsg(player: Player) {
        if (player.hasPermission("rockwall.admin")) {
            player.sendMessage(Formats.color("&e&lUpdate available!"))
            player.sendMessage(Formats.color("&7There is an update available for Rockwall"))
            player.sendMessage(Formats.color("&7Your version: &c${plugin.description.version} &8→ &7Newest version: &a$updateVersion"))
            player.sendMessage(Formats.color("&7Download at&6&n https://www.spigotmc.org/resources/rockwall.103709/"))
        }
    }

    fun downloadUpdate(player: Player) {
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.spiget.org/v2/resources/$id/download"))
            .build()

        val path = Path.of(plugin.dataFolder.toString(), "../", "Rockwall.jar")

        client.send(request, HttpResponse.BodyHandlers.ofFile(path))

        player.sendMessage(Formats.color("&aUpdate downloaded, this will be applied on next restart"))
    }
}
