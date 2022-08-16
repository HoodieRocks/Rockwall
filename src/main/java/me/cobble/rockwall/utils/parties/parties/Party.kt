package me.cobble.rockwall.utils.parties.parties

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
        members.add(uuid)
    }

    fun removeMember(uuid: UUID) {
        members.remove(uuid)
    }

    fun addInvite(uuid: UUID) {
        invites.add(uuid)
    }

    fun removeInvite(uuid: UUID) {
        invites.remove(uuid)
    }

    fun addSpeaker(uuid: UUID) {
        activeSpeakers.add(uuid)
    }

    fun removeSpeaker(uuid: UUID) {
        activeSpeakers.remove(uuid)
    }
}
