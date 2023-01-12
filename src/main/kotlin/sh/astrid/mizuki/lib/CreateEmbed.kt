package sh.astrid.mizuki.lib

import org.bukkit.event.player.PlayerEvent
import org.javacord.api.entity.message.embed.EmbedBuilder
import java.awt.Color
import java.util.regex.Matcher
import java.util.regex.Pattern

// (kind of?) messy code. feel free to improve it!
fun createEmbed(key: String, event: PlayerEvent): EmbedBuilder {
    val embed = EmbedBuilder()
    val embedTitle = getMessage("$key.embed.title")
    val embedDesc = getMessage("$key.embed.description")
    val embedThumbnail = getMessage("$key.embed.thumbnail")
    val embedColour = getMessage("$key.embed.colour")
    val embedAuthor = getMessage("$key.embed.author", isObject = true)

    if(embedTitle.isNotEmpty())
        embed.setTitle(embedTitle.parse(event))

    if(embedDesc.isNotEmpty())
        embed.setDescription(embedDesc.parse(event))

    if(embedThumbnail.isNotEmpty())
        embed.setThumbnail(embedThumbnail.parse(event))

    if(embedColour.isNotEmpty()) {
        val hexPattern = Pattern.compile("#[a-fA-F\\d]{6}");
        val match: Matcher = hexPattern.matcher(embedColour)
        if(match.find()) embed.setColor(Color.decode(embedColour))
        else {
            println("[Mizuki - Embed Error] Colour $embedColour is not a valid hex colour.")
        }
    }

    if(embedAuthor != null && !embedAuthor.isEmpty) {
        val embedAuthorName = (embedAuthor.getString("name") ?: "").parse(event)
        val embedAuthorAvatar = (embedAuthor.getString("avatar") ?: "").parse(event)
        val embedAuthorLink = (embedAuthor.getString("link") ?: "").parse(event)

        embed.setAuthor(embedAuthorName, embedAuthorLink, embedAuthorAvatar)
    }

    return embed
}