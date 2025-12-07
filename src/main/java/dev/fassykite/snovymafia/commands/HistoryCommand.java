package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.utils.GradientUtil;
import dev.fassykite.snovymafia.utils.PlayerStatsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class HistoryCommand implements CommandExecutor {
    private final SnovyMafia plugin;
    private final PlayerStatsManager statsManager;

    public HistoryCommand(SnovyMafia plugin, PlayerStatsManager statsManager) {
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

        // Здесь можно хранить историю, но для простоты — покажем статистику
        int gamesPlayed = (int) stats.get("games_played");
        int wins = (int) stats.get("wins");
        int losses = (int) stats.get("losses");

        String header = GradientUtil.applyMafiaGradient("=== История игр ===");
        String gamesLine = "Всего игр: §a" + gamesPlayed;
        String winsLine = "Побед: §a" + wins;
        String lossesLine = "Поражений: §c" + losses;

        String fullMsg = header + "\n" + gamesLine + "\n" + winsLine + "\n" + lossesLine;

        player.sendMessage(GradientUtil.parse(fullMsg));
        return true;
    }
}