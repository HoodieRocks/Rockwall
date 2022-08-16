package me.cobble.rockwall.config.models

/**
 * *Types of parties.*
 *
 * NORMAL - normal player parties.
 *
 * ADMIN - parties that only admins can create and join.
 * admins can join any admin party without invites
 */
enum class PartyType(private val type: String) {
    NORMAL("normal"),
    ADMIN("admin");

    fun getType() = (this.type)
}
