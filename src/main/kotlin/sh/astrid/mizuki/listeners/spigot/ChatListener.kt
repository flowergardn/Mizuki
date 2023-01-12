package sh.astrid.mizuki.listeners.spigot

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import sh.astrid.mizuki.Mizuki
import sh.astrid.mizuki.lib.*

class ChatListener : Listener {
    init {
        Mizuki.instance.server.pluginManager.registerEvents(this, Mizuki.instance)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onChat(event: AsyncPlayerChatEvent) {
        if(event.isCancelled) return
        sendWebhook(buildMsg(event, "chat"))
    }
}