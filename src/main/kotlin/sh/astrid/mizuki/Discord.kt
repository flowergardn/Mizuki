package sh.astrid.mizuki

import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.intent.Intent
import sh.astrid.mizuki.listeners.discord.ChatListener
import sh.astrid.mizuki.listeners.discord.CommandListener

object Discord {
    var api: DiscordApi? = null
    var chatChannel: TextChannel? = null
    var isConnected: Boolean = false

    fun load() {
        val token = Mizuki.instance.config.getString("token")
        val channel = Mizuki.instance.config.getString("channelID")

        if (token != null && token.isEmpty()) {
            throw Exception("No token specified! Please enter a token in config.yml.")
        }

        api = DiscordApiBuilder()
            .setToken(token)
            .setIntents(Intent.GUILDS, Intent.GUILD_MESSAGES, Intent.MESSAGE_CONTENT)
            .login()
            .join()

        if(api == null) {
            println("API was null, cancelling load.")
            return
        }

        isConnected = true

        if(channel.isNullOrEmpty()) {
            println("[Mizuki] Mizuki is not currently setup! Read https://mizuki.astrid.sh/setup for more information.")
        } else {
            chatChannel = api!!.getTextChannelById(channel).get()
        }

        // Register listeners
        CommandListener()
        ChatListener()
    }
}