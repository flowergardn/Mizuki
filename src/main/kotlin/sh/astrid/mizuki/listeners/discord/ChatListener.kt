package sh.astrid.mizuki.listeners.discord

import org.bukkit.Bukkit
import org.javacord.api.event.message.MessageCreateEvent
import sh.astrid.mizuki.Discord
import sh.astrid.mizuki.Mizuki
import sh.astrid.mizuki.lib.Placeholder
import sh.astrid.mizuki.lib.coloured
import sh.astrid.mizuki.lib.getMessage
import sh.astrid.mizuki.lib.pp
import java.awt.Color

class ChatListener {
    init {
        Discord.api!!.addMessageCreateListener { event -> onChat(event) }
    }

    private fun onChat(event: MessageCreateEvent) {
        val channel = event.channel
        val config = Mizuki.instance.config

        val configuredChannel = config.getString("channelID")
        val characterLimit = config.getInt("discordMessageLength")
        val messageNewLines = config.getBoolean("discordNewLines")

        // filter out bots
        if(event.messageAuthor.isBotUser || !event.messageAuthor.isUser) return

        if(configuredChannel.isNullOrEmpty()) return
        if(channel.id.toString() != configuredChannel) return

        val contentMsg = getMessage("ingame.content")

        if(contentMsg.isEmpty()) return

        val userRoles = event
            .messageAuthor.asUser().get()
            .getRoles(event.server.get())
            .toMutableList()

        val highestRole = userRoles[userRoles.size - 1]

        /* TODO: Improve role colour grabbing
        *  The users highest role might not be the colour they have as their name
        * */

        var roleName = highestRole.name
        if(roleName == "@everyone") roleName = "Default"

        var color = Color.GRAY
        if(highestRole.color.isPresent)
            color = highestRole.color.get()

        // ty stackoverflow :)
        val rgba: Int = color.rgb shl 8 or color.alpha
        val hex = String.format("#%08X", rgba)
            .replace("FF", "")
        val rank = "&$hex$roleName&r"

        var msgContent = event.readableMessageContent

        // only substring if needed
        if(msgContent.length > characterLimit)
            msgContent.substring(0, characterLimit) + "..."

        // if new lines are disabled, remove them
        if(!messageNewLines)
            msgContent = msgContent.replace("\n", "")

        val placeholders: List<Placeholder> = listOf(
            Placeholder("%message%", msgContent),
            Placeholder("%display-name%", event.messageAuthor.displayName),
            Placeholder("%name%", event.messageAuthor.name),
            Placeholder("%tag%", event.messageAuthor.discriminatedName),
            Placeholder("%coloured-role%", rank),
            Placeholder("%role-colour%", "&$hex"),
            Placeholder("%role%", roleName)
        )

        val parsedMsg = contentMsg.pp(*placeholders.toTypedArray())

        Bukkit.broadcastMessage(parsedMsg.coloured())
     }
}