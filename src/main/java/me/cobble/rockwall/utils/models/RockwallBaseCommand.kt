package me.cobble.rockwall.utils.models

import org.bukkit.entity.Player

interface RockwallBaseCommand {

    /**
     * Get the name of the sub command.
     *
     * @return The name of the sub command.
     */
    val name: String

    /**
     * Get the description of the sub command.
     *
     * @return The description of the sub command.
     */
    val descriptor: String

    /**
     * Get the usage of the sub command.
     *
     * @return The usage of the sub command.
     */
    val syntax: String

    /**
     * Executes the sub command.
     *
     * @param p    - The player who executed the sub command.
     * @param args - The arguments of the sub command.
     */
    fun run(p: Player, args: Array<String>)
}
