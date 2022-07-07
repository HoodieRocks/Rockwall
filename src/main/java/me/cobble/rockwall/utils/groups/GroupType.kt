package me.cobble.rockwall.utils.groups

enum class GroupType(private val type: String) {
    NORMAL("normal"),
    ADMIN("admin");

    fun getType() = (this.type)
}