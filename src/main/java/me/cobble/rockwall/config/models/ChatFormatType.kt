package me.cobble.rockwall.config.models

enum class ChatFormatType(private val type: String) {
    PREFIX("prefix"),
    NAME("name"),
    PREFIX_SEPARATOR("prefix-separator"),
    NAME_SEPARATOR("name-separator"),
    SUFFIX("suffix"),
    SUFFIX_SEPARATOR("suffix-separator");

    fun getType() = (this.type)
}
