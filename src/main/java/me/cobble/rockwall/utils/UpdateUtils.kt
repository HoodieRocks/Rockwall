package me.cobble.rockwall.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import me.cobble.rockwall.rockwall.Rockwall
import me.cobble.rockwall.utils.ColorUtils.sendColoredMessage
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
class UpdateUtils(private val plugin: Rockwall) {
    private lateinit var updateVersion: String
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
            .uri(URI.create("https://api.spiget.org/v2/resources/$id/versions/latest"))
            .build()

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept {
            val obj = gson.fromJson(it.body(), JsonObject::class.java)
            val retrievedVersion = obj["name"].asString

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
    fun updateAvailable(): Boolean = updateVersion != plugin.description.version

    fun sendUpdateAvailableMsg(player: Player) {
        if (player.hasPermission("rockwall.admin")) {
            player.sendColoredMessage("&e&lUpdate available!")
            player.sendColoredMessage("&7There is an update available for Rockwall")
            player.sendColoredMessage("&c${plugin.description.version} &8→ &a$updateVersion")
            player.sendColoredMessage("&7Download at&6&n https://www.spigotmc.org/resources/rockwall.103709/")
        }
    }

    fun downloadUpdate(player: Player) {
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.spiget.org/v2/resources/$id/download"))
            .build()

        val path = Path.of(plugin.dataFolder.toString(), "../", "Rockwall-$updateVersion.jar")

        client.send(request, HttpResponse.BodyHandlers.ofFile(path))

        player.sendMessage(ColorUtils.color("&aUpdate downloaded, this will be applied on next restart"))
    }
}
