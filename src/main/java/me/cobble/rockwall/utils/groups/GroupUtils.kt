package me.cobble.rockwall.utils.groups

import me.clip.placeholderapi.PlaceholderAPI
import me.cobble.rockwall.utils.Config
import me.cobble.rockwall.utils.Utils
import me.cobble.rockwall.utils.global.FormatType
import me.cobble.rockwall.utils.global.GlobalChatUtils
import me.cobble.rockwall.utils.groups.models.Group
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import java.util.*

object GroupUtils {

    fun validateGroupName(string: String): Boolean {
        return Regex("[A-z]+#[0-9]+").matches(string)
    }

    fun inviteToMember(uuid: UUID, group: Group?) {
        if (group == null) return
        group.members.add(uuid)
        group.invites.remove(uuid)
    }

    fun changeChatSpeaker(player: UUID, group: Group?) {
        GroupManager.getGroups().values.forEach {
            if (group == null || it != group) {
                it.activeSpeakers.remove(player)
            }
        }
    }

    fun getCurrentSpeakingChat(player: UUID): Group? {
        for (group: Group in GroupManager.getGroups().values) {
            if (group.isSpeaking(player)) {
                return group
            }
        }
        return null
    }

    fun formatMaker(player: Player, group: Group?, groupType: GroupType, formatType: FormatType): TextComponent? {
        val configSection =
            Config.get()!!.getConfigurationSection("groups.formats.${groupType.getType()}") ?: return null
        val section = configSection.getConfigurationSection(formatType.getType())
        val format = TextComponent(
            *TextComponent.fromLegacyText(
                Utils.color(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        section!!.getString("display")!!.replace("%chat_alias%", group!!.alias)
                    )
                )
            )
        )
        format.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT, Text(
                Utils.color(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        GlobalChatUtils.flattenStringList(section.getStringList("hover"))
                    )
                )
            )
        )
        format.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            PlaceholderAPI.setPlaceholders(player, section.getString("on-click")!!)
        )

        return format
    }
}