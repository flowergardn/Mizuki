package sh.astrid.mizuki.lib

import com.moandjiezana.toml.Toml
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerEvent
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import sh.astrid.mizuki.Discord
import sh.astrid.mizuki.Mizuki
import java.util.*

fun getWebhookURL(): String? {
    val info = getWebhookInfo();
    if(info.isNullOrEmpty()) return null
    return "https://discord.com/api/webhooks/${info["id"]}/${info["token"]}"
}

fun getWebhookInfo(): JSONObject? {
    val config = Mizuki.instance.config

    val webhookID = config.getString("webhookID")
    val token = config.getString("token")
    if(webhookID.isNullOrEmpty()) return null

    // doing this by hand, javacords implementation for webhooks is kinda shitty

    val client = OkHttpClient()

    val request: Request = Request.Builder()
        .url("https://discord.com/api/v10/webhooks/$webhookID")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bot $token")
        .build()

    val response = client.newCall(request).execute();

    val jsonResponse: String = Objects.requireNonNull(response.body)?.string() ?: ""
    val parser = JSONParser()
    val parsedResponse = parser.parse(jsonResponse) as JSONObject;

    return parsedResponse
}

fun sendWebhook(message: MessageBuilder) {
    val webhook = getWebhookURL()
    if(webhook.isNullOrEmpty()) return

    val config = Mizuki.instance.config

    // general checks to prevent null stuff
    var enableRoleMentions = config.getBoolean("enableRoleMentions");
    if(enableRoleMentions.toString().isEmpty()) enableRoleMentions = false

    var enableUserMentions = config.getBoolean("enableUserMentions");
    if(enableUserMentions.toString().isEmpty()) enableUserMentions = false

    val msg = message.setAllowedMentions(AllowedMentionsBuilder()
        .setMentionEveryoneAndHere(false)
        .setMentionRoles(enableRoleMentions)
        .setMentionUsers(enableUserMentions)
        .build())

    msg.sendWithWebhook(Discord.api, webhook)
}

fun buildMsg(event: PlayerEvent, key: String): MessageBuilder {
    val embedTable: Toml? = getMessage("$key.embed", isObject = true)

    val message = MessageBuilder()

    // need to do this to parse placeholders like %message%
    if(event.eventName == "AsyncPlayerChatEvent") event as AsyncPlayerChatEvent

    if(embedTable != null && !embedTable.isEmpty) {
        val embed = createEmbed(key, event)
        message.setEmbed(embed)
    }

    if(getMessage("$key.content").isNotEmpty()) {
        val notParsed = getMessage("$key.content")
        message.setContent(notParsed.parse(event))
    }

    return message
}