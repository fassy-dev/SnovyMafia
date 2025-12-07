package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.utils.GradientUtil;
import dev.fassykite.snovymafia.utils.PlayerStatsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class LoseCommand implements CommandExecutor {
    private final SnovyMafia plugin;
    private final PlayerStatsManager statsManager;

    public LoseCommand(SnovyMafia plugin, PlayerStatsManager statsManager) {
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
        int losses = (int) stats.get("losses");

        String msg = GradientUtil.applyMafiaGradient("Ты проиграл §c" + losses + " §fраз(а).");
        player.sendMessage(GradientUtil.parse(msg));
        return true;
    }
}