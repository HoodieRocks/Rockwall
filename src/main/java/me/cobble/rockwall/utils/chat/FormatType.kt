package me.cobble.rockwall.utils.chat

enum class FormatType(private val type: String) {
    PREFIX("prefix"),
    NAME("name"),
    PREFIX_SEPARATOR("prefix-separator"),
    NAME_SEPARATOR("name-separator");

    fun getType() = (this.type)
}
