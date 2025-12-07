package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.utils.GradientUtil;
import dev.fassykite.snovymafia.utils.PlayerStatsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TopCommand implements CommandExecutor {
    private final SnovyMafia plugin;
    private final PlayerStatsManager statsManager;

    public TopCommand(SnovyMafia plugin, PlayerStatsManager statsManager) {
        this.plugin = plugin;
        this.statsManager = statsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Map<UUID, Integer> topWins = new HashMap<>();

        // Собираем всех игроков из player_data.yml
        for (String key : statsManager.getDataConfig().getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int wins = statsManager.getDataConfig().getInt(key + ".wins", 0);
            topWins.put(uuid, wins);
        }

        // Сортируем по победам
        List<Map.Entry<UUID, Integer>> sorted = topWins.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        if (sorted.isEmpty()) {
            sender.sendMessage(GradientUtil.parse(GradientUtil.applyMafiaGradient("❌ Нет данных для топа.")));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(GradientUtil.applyMafiaGradient("🏆 Топ игроков по победам:\n"));

        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : sorted) {
            String name = plugin.getServer().getOfflinePlayer(entry.getKey()).getName();
            if (name == null) name = "Unknown";
            sb.append("§e").append(rank).append(". §f").append(name).append(" §7(§a").append(entry.getValue()).append("§7)\n");
            rank++;
        }

        sender.sendMessage(GradientUtil.parse(sb.toString()));
        return true;
    }
}