package me.cobble.rockwall.utils.groups.models

import java.util.*

interface Group {
    val members: ArrayList<UUID>
    val invites: ArrayList<UUID>
    val activeSpeakers: ArrayList<UUID>
    val alias: String
    val owner: UUID

    fun isMember(uuid: UUID): Boolean {
        return members.contains(uuid)
    }

    fun isInvited(uuid: UUID): Boolean {
        return invites.contains(uuid)
    }

    fun isSpeaking(uuid: UUID): Boolean {
        return activeSpeakers.contains(uuid)
    }
}
