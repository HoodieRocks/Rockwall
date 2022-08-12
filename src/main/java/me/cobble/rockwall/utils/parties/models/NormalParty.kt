package me.cobble.rockwall.utils.parties.models

import me.cobble.rockwall.config.Config
import java.util.*

class NormalParty(override val owner: UUID, override val alias: String) : Party {
    override val members: ArrayList<UUID> = arrayListOf(owner)
    override val invites: ArrayList<UUID> = arrayListOf()
    override val activeSpeakers: ArrayList<UUID> = arrayListOf()
    var timeTillDeath = Config.getInt("parties.timeout")

    fun decrementTimeTillDeath() {
        timeTillDeath--
    }
}
