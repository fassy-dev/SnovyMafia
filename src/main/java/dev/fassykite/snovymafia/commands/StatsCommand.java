package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.Role;
import dev.fassykite.snovymafia.utils.GradientUtil;
import dev.fassykite.snovymafia.utils.PlayerStatsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class StatsCommand implements CommandExecutor {
    private final SnovyMafia plugin;
    private final PlayerStatsManager statsManager;

    public StatsCommand(SnovyMafia plugin, PlayerStatsManager statsManager) {
        this.plugin = plugin;
        this.statsManager = statsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player p) {
            player = p;
        }

        UUID targetUuid;
        String playerName;

        if (args.length == 0) {
            if (player == null) {
                sender.sendMessage("Только игроки могут смотреть свою статистику без указания никнейма.");
                return true;
            }
            targetUuid = player.getUniqueId();
            playerName = player.getName();
        } else {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(GradientUtil.parse(GradientUtil.applyMafiaGradient("❌ Игрок не найден: " + args[0])));
                return true;
            }
            targetUuid = target.getUniqueId();
            playerName = target.getName();
        }

        Map<String, Object> stats = statsManager.getPlayerStats(targetUuid);

        int gamesPlayed = (int) stats.get("games_played");
        int wins = (int) stats.get("wins");
        int losses = (int) stats.get("losses");
        int kills = (int) stats.get("kills");
        int heals = (int) stats.get("heals");
        @SuppressWarnings("unchecked")
        Map<Role, Integer> rolesPlayed = (Map<Role, Integer>) stats.get("roles_played");

        String header = GradientUtil.applyMafiaGradient("=== Статистика игрока " + playerName + " ===");
        String gamesLine = "Всего игр: §a" + gamesPlayed;
        String winsLine = "Побед: §a" + wins;
        String lossesLine = "Поражений: §c" + losses;
        String killsLine = "Убийств: §c" + kills;
        String healsLine = "Спасений: §a" + heals;

        StringBuilder rolesLine = new StringBuilder("Роли: ");
        for (Map.Entry<Role, Integer> entry : rolesPlayed.entrySet()) {
            if (entry.getValue() > 0) {
                rolesLine.append(entry.getKey().getDisplayName()).append(": §e").append(entry.getValue()).append("§7, ");
            }
        }
        if (rolesLine.length() > 6) {
            rolesLine.setLength(rolesLine.length() - 2); // убираем последнюю запятую
        } else {
            rolesLine.append("Нет данных");
        }

        String fullMsg = header + "\n" +
                gamesLine + "\n" +
                winsLine + "\n" +
                lossesLine + "\n" +
                killsLine + "\n" +
                healsLine + "\n" +
                rolesLine.toString();

        sender.sendMessage(GradientUtil.parse(fullMsg));
        return true;
    }
}