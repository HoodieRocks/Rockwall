package me.cobble.rockwall.cmds.admin.debug

import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.TextUtils
import me.cobble.rockwall.utils.parties.PartyManager
import me.cobble.rockwall.utils.parties.parties.Party
import org.bukkit.entity.Player
import java.util.*

object StressTests {

    fun parties(player: Player): Boolean {
        player.sendMessage("Testing create() with 10K entries")

        val localCopy = linkedMapOf<UUID, Party>()

        var time = System.currentTimeMillis()
        for (i in 0 until 10000) {
            val uuid = UUID.randomUUID()
            localCopy[uuid] = PartyManager.createParty(uuid, TextUtils.randomString(10), PartyType.NORMAL)
        }
        player.sendMessage("Took ${System.currentTimeMillis() - time}ms to create 10K party objects")

        player.sendMessage("getting all 10K objects via UUID")
        time = System.currentTimeMillis()
        localCopy.forEach {
            PartyManager.getParty(it.key)
        }
        player.sendMessage("Took ${System.currentTimeMillis() - time}ms to get 10K objects via UUID")

        player.sendMessage("getting all 10K objects via name (expensive)")
        time = System.currentTimeMillis()
        localCopy.forEach {
            PartyManager.getParty(it.value.alias)
        }
        player.sendMessage("Took ${System.currentTimeMillis() - time}ms to get 10K objects via name")

        player.sendMessage("getting all 10K objects via name (expensive)")
        time = System.currentTimeMillis()
        localCopy.forEach {
            PartyManager.deleteParty(it.value)
        }
        player.sendMessage("Took ${System.currentTimeMillis() - time}ms to delete 10K objects")

        localCopy.clear()
        return true
    }

}