package me.cobble.rockwall.utils.groups

/**
 * *Types of groups.*
 *
 * NORMAL - normal player groups.
 *
 * ADMIN - groups that only admins can create and join.
 * admins can join any admin group without invites
 */
enum class GroupType(private val type: String) {
    NORMAL("normal"),
    ADMIN("admin");

    fun getType() = (this.type)
}