package me.cobble.rockwall.utils.groups

import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.groups.models.AdminGroup
import me.cobble.rockwall.utils.groups.models.Group
import me.cobble.rockwall.utils.groups.models.NormalGroup
import org.bukkit.Bukkit
import java.util.*


object GroupManager {
    private val groups = hashMapOf<UUID, Group>()

    private fun addGroup(owner: UUID, group: Group) {
        groups[owner] = group
    }

    fun deleteGroup(group: Group) {

        val memberCopy = group.members
        for (member: UUID in group.members) {
            val player = Bukkit.getPlayer(member)
            if (player!!.isOnline) player.sendMessage(Utils.color("&cThe owner of this group has deleted the group"))
        }

        group.members.removeAll(memberCopy.toSet())

        val inviteCopy = group.invites
        for (member: UUID in group.invites) {
            val player = Bukkit.getPlayer(member)
            if (player!!.isOnline) player.sendMessage(Utils.color("&cThe owner of this group has deleted the group"))
        }
        group.invites.removeAll(inviteCopy.toSet())

        for (member: UUID in group.activeSpeakers) {
            group.activeSpeakers.remove(member)
        }

        groups.remove(group.owner)
    }

    fun createGroup(owner: UUID, conflictName: String, type: GroupType) {

        val tag = (0 until 10000).random()
        var name = "$conflictName#${String.format("%04d", tag)}"

        for (group: Group in groups.values) {
            if (group.alias == name) {
                name = "$conflictName#${tag + 1}"
            }
        }

        val group = if (type == GroupType.NORMAL) NormalGroup(owner, name) else AdminGroup(owner, name)
        addGroup(owner, group)
    }

    fun getGroup(uuid: UUID): Group? {
        if (!groupExists(uuid)) return null
        return groups[uuid]
    }

    fun getGroup(name: String): Group? {
        if (name.isBlank()) return null
        for (group: Group in groups.values) {
            if (group.alias == name) return group
        }
        return null
    }

    fun groupExists(uuid: UUID): Boolean {
        return groups.contains(uuid)
    }

    fun groupExists(name: String): Boolean {
        return getGroup(name) != null
    }

    fun getGroups(): HashMap<UUID, Group> {
        return groups
    }
}