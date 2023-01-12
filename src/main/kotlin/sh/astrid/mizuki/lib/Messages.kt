package sh.astrid.mizuki.lib

import com.moandjiezana.toml.Toml
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerEvent
import sh.astrid.mizuki.Mizuki
import java.io.File

private fun playerPlaceholders(event: PlayerEvent): List<Placeholder> {
    return listOf(
        // %name% - the players name
        Placeholder("%name%", event.player.name),
        // %uuid% - the players uuid
        Placeholder("%uuid%", event.player.uniqueId.toString())
    )
}

// can't have multiple return types, and checking that would be a nightmare
// thus there's 2 methods

fun getMessage(key: String): String {
    val path = File(Mizuki.instance.dataFolder, "messages.toml")
    val toml: Toml = Toml().read(path)
    return toml.getString(key) ?: ""
}

fun getMessage(key: String, isObject: Boolean = true): Toml? {
    val path = File(Mizuki.instance.dataFolder, "messages.toml")
    val toml: Toml = Toml().read(path)
    return toml.getTable(key)
}

fun String.parse(event: PlayerEvent): String {
    var placeholders = playerPlaceholders(event)

    if(event.eventName == "AsyncPlayerChatEvent") {
        event as AsyncPlayerChatEvent
        placeholders = placeholders.plus(Placeholder("%message%", event.message))
    }

    return this.pp(*placeholders.toTypedArray())
}