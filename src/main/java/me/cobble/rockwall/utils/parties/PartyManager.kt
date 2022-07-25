package me.cobble.rockwall.utils.parties

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.utils.parties.models.AdminParty
import me.cobble.rockwall.utils.parties.models.NormalParty
import me.cobble.rockwall.utils.parties.models.Party
import org.bukkit.Bukkit
import java.util.*

// Manages Rockwall groups
object PartyManager {
    private val groups = hashMapOf<UUID, Party>()

    private fun addGroup(owner: UUID, party: Party) {
        groups[owner] = party
    }

    /**
     * Deletes group
     * @param party group to delete
     */
    fun deleteGroup(party: Party) {

        val memberCopy = party.members
        for (member: UUID in party.members) {
            val player = Bukkit.getPlayer(member)
            if (player!!.isOnline) player.sendMessage(Messages.getGroupString("deletion", party))
        }

        party.members.removeAll(memberCopy.toSet())

        val inviteCopy = party.invites
        for (member: UUID in party.invites) {
            val player = Bukkit.getPlayer(member)
            if (player!!.isOnline) player.sendMessage(Messages.getGroupString("deletion", party))
        }
        party.invites.removeAll(inviteCopy.toSet())

        val speakerCopy = party.activeSpeakers
        party.activeSpeakers.removeAll(speakerCopy.toSet())

        groups.remove(party.owner)
    }

    /**
     * Creates a group
     *
     * @param owner uuid of owner player
     * @param name name of group
     * @param type type of group
     * @see PartyType
     */
    fun createGroup(owner: UUID, name: String, type: PartyType) {

        val tag = (0 until 10000).random()
        var alias = "$name#${String.format("%04d", tag)}"

        while (getGroup(alias) != null) {
            alias = "$name#${tag + 1}"
        }

        val group = if (type == PartyType.NORMAL) NormalParty(owner, alias) else AdminParty(owner, alias)
        addGroup(owner, group)
    }

    /**
     * Gets group by UUID
     * @param uuid uuid of group owner
     * @return group
     */
    fun getGroup(uuid: UUID): Party? {
        if (!groupExists(uuid)) return null
        return groups[uuid]
    }

    /**
     * Gets group by group name
     * @param name name of group, including tag
     * @return group
     */
    fun getGroup(name: String): Party? {
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
    fun getGroups(): HashMap<UUID, Party> {
        return groups
    }
    fun tickTimers() {
        for (party: Party in groups.values) {
            if (party is NormalParty) {
                if (party.timeTillDeath == 0) deleteGroup(party)
                party.decrementTimeTillDeath()
            }
        }
    }
}