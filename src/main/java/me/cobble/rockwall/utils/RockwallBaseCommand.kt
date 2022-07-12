package me.cobble.rockwall.utils

import org.bukkit.entity.Player

abstract class RockwallBaseCommand {

    /**
     * Get the name of the sub command.
     *
     * @return The name of the sub command.
     */
    abstract val name: String

    /**
     * Get the description of the sub command.
     *
     * @return The description of the sub command.
     */
    abstract val descriptor: String

    /**
     * Get the usage of the sub command.
     *
     * @return The usage of the sub command.
     */
    abstract val syntax: String

    /**
     * Executes the sub command.
     *
     * @param p    - The player who executed the sub command.
     * @param args - The arguments of the sub command.
     */
    abstract fun run(p: Player, args: Array<String>)
}
