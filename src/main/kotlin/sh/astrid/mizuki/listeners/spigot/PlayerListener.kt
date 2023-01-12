package sh.astrid.mizuki.listeners.spigot

import org.bukkit.event.EventHandler
import sh.astrid.mizuki.Mizuki
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import sh.astrid.mizuki.lib.*

class PlayerListener : Listener {
    init {
        Mizuki.instance.server.pluginManager.registerEvents(this, Mizuki.instance)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if(event.player.hasPlayedBefore()) sendWebhook(buildMsg(event, "join"))
        else sendWebhook(buildMsg(event, "firstjoin"))
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        sendWebhook(buildMsg(event, "leave"))
    }
}
