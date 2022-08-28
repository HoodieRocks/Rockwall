package me.cobble.rockwall.utils.parties.parties

import me.cobble.rockwall.config.Config
import java.util.*
import java.util.concurrent.CompletableFuture

class NormalParty(override val owner: UUID, override val alias: String) : Party {
    override val members: ArrayList<UUID> = arrayListOf(owner)
    override val invites: ArrayList<UUID> = arrayListOf()
    override val activeSpeakers: ArrayList<UUID> = arrayListOf()
    var timeTillDeath = Config.getInt("parties.timeout")

    fun decrementTimeTillDeath() {
        CompletableFuture.runAsync {
            timeTillDeath--
        }
    }
}
