package me.cobble.rockwall.utils.groups

import me.cobble.rockwall.utils.Utils
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import java.util.*


// sends invites to player
object InviteSender {

    fun sendInvites(invites: ArrayList<UUID>, name: String) {
        for (id: UUID in invites) {
            val player = Bukkit.getPlayer(id)
            val inviteComponent =
                TextComponent(Utils.color("&eYou've been invited to join group &7$name. \n&eClick accept to join, or click deny to decline.\n"))
            val accept = TextComponent(Utils.color("&a&lAccept"))
            val separator = TextComponent(Utils.color(" &8| "))
            val deny = TextComponent(Utils.color("&c&lDeny"))

            accept.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Click to join $name"))
            deny.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Click to decline"))

            accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g accept $name")
            deny.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g deny $name")

            inviteComponent.addExtra(accept)
            inviteComponent.addExtra(separator)
            inviteComponent.addExtra(deny)

            player!!.spigot().sendMessage(inviteComponent)
        }
    }
}