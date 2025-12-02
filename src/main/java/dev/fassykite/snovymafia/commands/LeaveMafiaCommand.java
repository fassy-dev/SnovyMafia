package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveMafiaCommand implements CommandExecutor {
    private final SnovyMafia plugin;

    public LeaveMafiaCommand(SnovyMafia plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки!");
            return true;
        }

        var game = plugin.getCurrentGame();
        if (game == null || game.getPhase() != dev.fassykite.snovymafia.game.MafiaGame.Phase.WAITING) {
            player.sendMessage("🎭 §cИгра ещё не началась или уже идёт!");
            return true;
        }

        if (game.removePlayerFromQueue(player)) {
            player.sendMessage("🎭 §aТы вышел из очереди.");
        } else {
            player.sendMessage("🎭 §cТы не записан в игру.");
        }
        return true;
    }
}