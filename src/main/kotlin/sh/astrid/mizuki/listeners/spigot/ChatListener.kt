package sh.astrid.mizuki.listeners.spigot

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import sh.astrid.mizuki.Mizuki
import sh.astrid.mizuki.lib.*

class ChatListener : Listener {
    init {
        Mizuki.instance.server.pluginManager.registerEvents(this, Mizuki.instance)
    }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        // embeds take priority over text content
        sendWebhook(buildMsg(event, "chat"))
    }
}