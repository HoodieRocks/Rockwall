package me.cobble.rockwall.utils.parties

/**
 * *Types of partys.*
 *
 * NORMAL - normal player partys.
 *
 * ADMIN - partys that only admins can create and join.
 * admins can join any admin party without invites
 */
enum class PartyType(private val type: String) {
    NORMAL("normal"),
    ADMIN("admin");

    fun getType() = (this.type)
}
