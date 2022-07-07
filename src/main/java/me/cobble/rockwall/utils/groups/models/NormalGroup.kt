package me.cobble.rockwall.utils.groups.models

import java.util.*

class NormalGroup(override val owner: UUID, override val alias: String) : Group {
    override val members: ArrayList<UUID> = arrayListOf(owner)
    override val invites: ArrayList<UUID> = arrayListOf()
    override val activeSpeakers: ArrayList<UUID> = arrayListOf()
}