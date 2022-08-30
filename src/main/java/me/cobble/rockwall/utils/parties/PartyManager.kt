package me.cobble.rockwall.utils.parties

import me.cobble.rockwall.config.Config
import me.cobble.rockwall.config.models.Messages
import me.cobble.rockwall.config.models.PartyType
import me.cobble.rockwall.utils.models.Manager
import me.cobble.rockwall.utils.parties.models.AdminParty
import me.cobble.rockwall.utils.parties.models.NormalParty
import me.cobble.rockwall.utils.parties.models.Party
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList

// Manages Rockwall parties
object PartyManager : Manager<UUID, Party>() {

    private fun addParty(owner: UUID, party: Party) {
        addOrUpdate(owner, party)
    }

    fun deleteParty(party: Party?) {
        if (party == null) return

        val memberCopy = party.members
        for (member: UUID in party.members) {
            val player = Bukkit.getPlayer(member)
            if (player != null && player.isOnline) player.sendMessage(Messages.getPartyMsg("deletion", party))
        }

        party.members.removeAll(memberCopy.toSet())

        val inviteCopy = party.invites
        for (member: UUID in party.invites) {
            val player = Bukkit.getPlayer(member)
            if (player != null && player.isOnline) player.sendMessage(Messages.getPartyMsg("deletion", party))
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
     *
     * @see PartyType
     */
    fun createParty(owner: UUID, name: String, type: PartyType): Party {
        var tag = (0 until 10000).random()
        var alias = "$name#${String.format("%04d", tag)}"

        getParty(alias).thenAccept {
            for (i in 0 until Config.getInt("settings.party-discriminator-tries")) {
                if (it != null) {
                    tag += 1
                    if (tag >= 10000) tag = 0
                    alias = "$name#${tag}"
                } else {
                    break
                }
            }
        }

        val party = if (type == PartyType.NORMAL) NormalParty(owner, alias) else AdminParty(owner, alias)
        addParty(owner, party)
        return party
    }

    fun getParty(uuid: UUID): Party? {
        return get(uuid)
    }

    fun getParty(name: String): CompletableFuture<Party?> {
        return CompletableFuture.supplyAsync {
            getAll().values.find {
                it.alias == name
            }
        }
    }

    fun doesPartyExists(uuid: UUID): Boolean {
        return containsKey(uuid)
    }

    fun doesPartyExists(name: String): Boolean {
        return getParty(name).get() != null
    }

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

    fun getAllAdminParties(): CompletableFuture<ArrayList<AdminParty>> {
        return CompletableFuture.supplyAsync {
            val adminParties = arrayListOf<AdminParty>()
            getAll().values.forEach {
                if(it is AdminParty) adminParties.add(it)
            }
            return@supplyAsync adminParties
        }
    }
}
