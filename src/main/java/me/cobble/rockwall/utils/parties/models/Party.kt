package me.cobble.rockwall.utils.parties.models

import java.util.*

interface Party {
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

    fun addMember(uuid: UUID) {
        if (isMember(uuid)) return
        members.add(uuid)
    }

    fun removeMember(uuid: UUID) {
        members.remove(uuid)
        if (isSpeaking(uuid)) removeSpeaker(uuid)
    }

    fun addInvite(uuid: UUID) {
        if (isInvited(uuid)) return
        invites.add(uuid)
    }

    fun removeInvite(uuid: UUID) {
        invites.remove(uuid)
    }

    fun addSpeaker(uuid: UUID) {
        if (isSpeaking(uuid)) return
        activeSpeakers.add(uuid)
    }

    fun removeSpeaker(uuid: UUID) {
        activeSpeakers.remove(uuid)
    }
}
