package sh.astrid.mizuki

import org.bukkit.plugin.java.JavaPlugin
import sh.astrid.mizuki.commands.*
import sh.astrid.mizuki.listeners.spigot.*
import java.io.File
import java.util.logging.Level


class Mizuki : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()

        Discord.load()

        // Creates messages.toml
        val messages = File(dataFolder, "messages.toml")
        if (!messages.exists()) {
            messages.parentFile.mkdirs()
            saveResource("messages.toml", false)
        }

        // Register commands
        Reload()

        // Register Spigot events
        PlayerListener()
        ChatListener()

        val version = instance.description.version;
        instance.logger.log(Level.INFO, "Mizuki v$version has loaded successfully.")

        // todo: send messages with webhooks
        // add config option for "rich webhooks"
        //  - changes the webhook avatar to whatever they configure
        //  - changes the name to whatever they configure

    }

    override fun onDisable() {
        if(!Discord.isConnected) return
        Discord.api!!.disconnect().join()
    }

    companion object {
        @JvmStatic
        val instance: Mizuki
            get() = getPlugin(Mizuki::class.java)
    }
}