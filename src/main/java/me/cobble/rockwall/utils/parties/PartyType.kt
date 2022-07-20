package me.cobble.rockwall.utils.parties

/**
 * *Types of groups.*
 *
 * NORMAL - normal player groups.
 *
 * ADMIN - groups that only admins can create and join.
 * admins can join any admin group without invites
 */
enum class PartyType(private val type: String) {
    NORMAL("normal"),
    ADMIN("admin");

    fun getType() = (this.type)
}