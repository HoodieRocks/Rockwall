package me.cobble.rockwall.utils.groups.models

import me.cobble.rockwall.rockwall.Config
import java.util.*

class NormalGroup(override val owner: UUID, override val alias: String) : Group {
    override val members: ArrayList<UUID> = arrayListOf(owner)
    override val invites: ArrayList<UUID> = arrayListOf()
    override val activeSpeakers: ArrayList<UUID> = arrayListOf()
    var timeTillDeath = Config.getInt("groups.timeout")

    fun decrementTimeTillDeath() {
        timeTillDeath--
    }
}