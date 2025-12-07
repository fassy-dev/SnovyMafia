package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.utils.GradientUtil;
import dev.fassykite.snovymafia.utils.PlayerStatsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class WinRateCommand implements CommandExecutor {
    private final SnovyMafia plugin;
    private final PlayerStatsManager statsManager;

    public WinRateCommand(SnovyMafia plugin, PlayerStatsManager statsManager) {
        this.plugin = plugin;
        this.statsManager = statsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки могут использовать эту команду.");
            return true;
        }

        Map<String, Object> stats = statsManager.getPlayerStats(player.getUniqueId());
        int gamesPlayed = (int) stats.get("games_played");
        int wins = (int) stats.get("wins");

        if (gamesPlayed == 0) {
            player.sendMessage(GradientUtil.parse(GradientUtil.applyMafiaGradient("Ты ещё не играл.")));
            return true;
        }

        double winRate = (double) wins / gamesPlayed * 100;
        String msg = GradientUtil.applyMafiaGradient("Твой процент побед: §a" + String.format("%.2f", winRate) + "%");
        player.sendMessage(GradientUtil.parse(msg));
        return true;
    }
}