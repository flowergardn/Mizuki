package sh.astrid.mizuki.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import sh.astrid.mizuki.Mizuki

class Reload : CommandExecutor {
    init {
        Mizuki.instance.getCommand("reload")!!.setExecutor(this);
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val canExecute = (sender is ConsoleCommandSender || sender is Player && sender.isOp)
        if(!canExecute) return false

        Mizuki.instance.reloadConfig();

        sender.sendMessage("[Mizuki] Successfully reloaded config.")

        return true
    }
}