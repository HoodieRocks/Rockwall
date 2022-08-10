package me.cobble.rockwall.utils.parties.models

import org.bukkit.Bukkit
import java.util.*

class AdminParty(override val owner: UUID, override val alias: String) : Party {
    override val members: ArrayList<UUID> = arrayListOf(owner)
    override val invites: ArrayList<UUID> = arrayListOf()
    override val activeSpeakers: ArrayList<UUID> = arrayListOf()

    override fun addMember(uuid: UUID) {
        val player = Bukkit.getPlayer(uuid)
        if (player != null && player.hasPermission("rockwall.admin.join")) members.add(uuid)
    }

    override fun addInvite(uuid: UUID) {
        val player = Bukkit.getPlayer(uuid)
        if (player != null && player.hasPermission("rockwall.admin.join")) invites.add(uuid)
    }
}
