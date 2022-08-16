package me.cobble.rockwall.utils.parties

import me.cobble.rockwall.config.Messages
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.Manager
import me.cobble.rockwall.utils.parties.parties.AdminParty
import me.cobble.rockwall.utils.parties.parties.NormalParty
import me.cobble.rockwall.utils.parties.parties.Party
import org.bukkit.Bukkit
import java.util.*

// Manages Rockwall parties
object PartyManager : Manager<UUID, Party>() {

    private fun addParty(owner: UUID, party: Party) {
        addOrUpdate(owner, party)
    }

    /**
     * Deletes party
     * @param party party to delete
     */
    fun deleteParty(party: Party?) {
        if (party == null) return

        val memberCopy = party.members
        for (member: UUID in party.members) {
            val player = Bukkit.getPlayer(member)
            if (player!!.isOnline) player.sendMessage(Messages.getPartyMsg("deletion", party))
        }

        party.members.removeAll(memberCopy.toSet())

        val inviteCopy = party.invites
        for (member: UUID in party.invites) {
            val player = Bukkit.getPlayer(member)
            if (player!!.isOnline) player.sendMessage(Messages.getPartyMsg("deletion", party))
        }
        party.invites.removeAll(inviteCopy.toSet())

        val speakerCopy = party.activeSpeakers
        party.activeSpeakers.removeAll(speakerCopy.toSet())

        remove(party.owner)
    }

    /**
     * Creates a party
     *
     * @param owner uuid of owner player
     * @param name name of party
     * @param type type of party
     * @see PartyType
     */
    fun createParty(owner: UUID, name: String, type: PartyType) {
        val tag = (0 until 10000).random()
        var alias = "$name#${String.format("%04d", tag)}"

        while (getParty(alias) != null) {
            alias = "$name#${tag + 1}"
        }

        val party = if (type == PartyType.NORMAL) NormalParty(owner, alias) else AdminParty(owner, alias)
        addParty(owner, party)
    }

    /**
     * Gets party by UUID
     * @param uuid uuid of party owner
     * @return party
     */
    fun getParty(uuid: UUID): Party? {
        return get(uuid)
    }

    /**
     * Gets party by party name
     * @param name name of party, including tag
     * @return party
     */
    fun getParty(name: String): Party? {
        if (name.isBlank()) return null
        return getAll().values.find {
            it.alias == name
        }
    }

    /**
     * Check if party exists by owner UUID
     * @param uuid owner uuid
     * @return true if exists
     */
    fun partyExists(uuid: UUID): Boolean {
        return containsKey(uuid)
    }

    /**
     * Check if party exists by party name
     * @param name party name
     * @return true if exists
     */
    fun partyExists(name: String): Boolean {
        return getParty(name) != null
    }

    /**
     * Gets all parties
     * @return all parties
     */
    fun getParties(): HashMap<UUID, Party> {
        return getAll()
    }

    fun tickTimers() {
        for (party: Party in getAll().values) {
            if (party is NormalParty) {
                if (party.timeTillDeath == 0) deleteParty(party)
                party.decrementTimeTillDeath()
            }
        }
    }
}
