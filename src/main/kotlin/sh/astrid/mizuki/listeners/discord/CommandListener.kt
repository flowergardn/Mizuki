package sh.astrid.mizuki.listeners.discord

import org.javacord.api.entity.channel.ChannelType
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.webhook.Webhook
import org.javacord.api.entity.webhook.WebhookBuilder
import org.javacord.api.event.message.MessageCreateEvent
import sh.astrid.mizuki.Discord
import sh.astrid.mizuki.Discord.chatChannel
import sh.astrid.mizuki.Mizuki
import java.time.Duration


class CommandListener {
    init {
        Discord.api!!.addMessageCreateListener { event -> onCommand(event)}
    }

    private fun deleteMsgs(eventMsg: Message, sentMsg: Message, duration: Long = 5) {
        eventMsg.deleteAfter(Duration.ofSeconds(duration))
        sentMsg.deleteAfter(Duration.ofSeconds(duration))
    }

    private fun setup(event: MessageCreateEvent, args: MutableList<String>) {
        if(!event.messageAuthor.canManageServer()) {
            val msg: Message = event.channel.sendMessage("Hey <@${event.messageAuthor.id}>, you're not allowed to use this :c").join()
            deleteMsgs(event.message, msg)
        }

        if(event.channel.type != ChannelType.SERVER_TEXT_CHANNEL) {
            val msg: Message = event.channel.sendMessage("Mizuki only supports regular server text channels.").join()
            deleteMsgs(event.message, msg)
        }

        val config = Mizuki.instance.config

        // Save the Guild ID and Channel ID.

        config.set("guildID", event.server.get().id.toString())

        var channelId = event.channel.id.toString()

        // Allows for a channel to be passed in as an argument
        if(args.size == 1) {
            channelId = args[0].replace(".*<#|>.*".toRegex(), "")
        }

        config.set("channelID", channelId)

        chatChannel = Discord.api!!.getTextChannelById(channelId).get()

        // Creates the webhook
        if(config.getString("webhookID").isNullOrEmpty()) {
            val webhook: Webhook = WebhookBuilder(chatChannel as ServerTextChannel?)
                .setName(Discord.api!!.yourself.name)
                .setAvatar(Discord.api!!.yourself.avatar)
                .create().join()

            config.set("webhookID", webhook.id)
        }

        Mizuki.instance.saveConfig()

        event.channel.sendMessage("Successfully setup!")
    }

    private fun onCommand(event: MessageCreateEvent) {
        val content = event.messageContent

        val args = content.split(" ").toMutableList()
        // removes the command from the arguments
        args.removeFirst()

        if(content.startsWith("!setup")) {
            setup(event, args)
            return
        }
    }
}