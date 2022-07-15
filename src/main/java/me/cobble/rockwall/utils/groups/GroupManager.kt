package me.cobble.rockwall.utils.groups

import me.cobble.rockwall.rockwall.Messages
import me.cobble.rockwall.utils.groups.models.AdminGroup
import me.cobble.rockwall.utils.groups.models.Group
import me.cobble.rockwall.utils.groups.models.NormalGroup
import org.bukkit.Bukkit
import java.util.*

// Manages Rockwall groups
object GroupManager {
    private val groups = hashMapOf<UUID, Group>()

    private fun addGroup(owner: UUID, group: Group) {
        groups[owner] = group
    }

    /**
     * Deletes group
     * @param group group to delete
     */
    fun deleteGroup(group: Group) {

        val memberCopy = group.members
        for (member: UUID in group.members) {
            val player = Bukkit.getPlayer(member)
            if (player!!.isOnline) player.sendMessage(Messages.getGroupString("deletion", group))
        }

        group.members.removeAll(memberCopy.toSet())

        val inviteCopy = group.invites
        for (member: UUID in group.invites) {
            val player = Bukkit.getPlayer(member)
            if (player!!.isOnline) player.sendMessage(Messages.getGroupString("deletion", group))
        }
        group.invites.removeAll(inviteCopy.toSet())

        val speakerCopy = group.activeSpeakers
        group.activeSpeakers.removeAll(speakerCopy.toSet())

        groups.remove(group.owner)
    }

    /**
     * Creates a group
     *
     * @param owner uuid of owner player
     * @param name name of group
     * @param type type of group
     * @see GroupType
     */
    fun createGroup(owner: UUID, name: String, type: GroupType) {

        val tag = (0 until 10000).random()
        var alias = "$name#${String.format("%04d", tag)}"

        for (group: Group in groups.values) {
            if (group.alias == alias) {
                alias = "$name#${tag + 1}"
            }
        }

        val group = if (type == GroupType.NORMAL) NormalGroup(owner, alias) else AdminGroup(owner, alias)
        addGroup(owner, group)
    }

    /**
     * Gets group by UUID
     * @param uuid uuid of group owner
     * @return group
     */
    fun getGroup(uuid: UUID): Group? {
        if (!groupExists(uuid)) return null
        return groups[uuid]
    }

    /**
     * Gets group by group name
     * @param name name of group, including tag
     * @return group
     */
    fun getGroup(name: String): Group? {
        if (name.isBlank()) return null
        return groups.values.find {
            it.alias == name
        }
    }

    /**
     * Check if group exists by owner UUID
     * @param uuid owner uuid
     * @return true if exists
     */
    fun groupExists(uuid: UUID): Boolean {
        return groups.containsKey(uuid)
    }

    /**
     * Check if group exists by group name
     * @param name group name
     * @return true if exists
     */
    fun groupExists(name: String): Boolean {
        return getGroup(name) != null
    }

    /**
     * Gets all groups
     * @return all groups
     */
    fun getGroups(): HashMap<UUID, Group> {
        return groups
    }

    /**
     * ***For Internal Use Only***
     * Ticks all normal group's timeTillDeath variable
     * @see NormalGroup.timeTillDeath
     */
    fun tickTimers() {
        for (group: Group in groups.values) {
            if (group is NormalGroup) {
                if (group.timeTillDeath == 0) deleteGroup(group)
                group.decrementTimeTillDeath()
            }
        }
    }
}