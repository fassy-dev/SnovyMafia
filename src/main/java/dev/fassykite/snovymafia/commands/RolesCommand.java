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

public class RolesCommand implements CommandExecutor {
    private final SnovyMafia plugin;
    private final PlayerStatsManager statsManager;

    public RolesCommand(SnovyMafia plugin, PlayerStatsManager statsManager) {
        this.plugin = plugin;
        this.statsManager = statsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки могут использовать эту команду.");
            return true;
        }

        @SuppressWarnings("unchecked")
        Map<Role, Integer> rolesPlayed = (Map<Role, Integer>) statsManager.getPlayerStats(player.getUniqueId()).get("roles_played");

        StringBuilder sb = new StringBuilder();
        sb.append(GradientUtil.applyMafiaGradient("Твои роли:\n"));

        for (Map.Entry<Role, Integer> entry : rolesPlayed.entrySet()) {
            if (entry.getValue() > 0) {
                sb.append("  ").append(entry.getKey().getDisplayName()).append(": §e").append(entry.getValue()).append("\n");
            }
        }

        if (sb.length() <= 20) {
            sb.append("  §7Нет данных\n");
        }

        player.sendMessage(GradientUtil.parse(sb.toString()));
        return true;
    }
}