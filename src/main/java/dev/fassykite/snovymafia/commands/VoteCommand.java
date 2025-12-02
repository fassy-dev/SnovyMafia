package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.MafiaGame;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {
    private final SnovyMafia plugin;

    public VoteCommand(SnovyMafia plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки!");
            return true;
        }

        MafiaGame game = plugin.getCurrentGame();
        if (game == null || game.getPhase() != MafiaGame.Phase.VOTING) {
            player.sendMessage("§cГолосование не идёт!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§eИспользуй: /vote <ник>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cИгрок не найден!");
            return true;
        }

        if (plugin.getLeaders().contains(target.getName())) {
            player.sendMessage("§cНельзя голосовать против ведущего!");
            return true;
        }

        if (game.getRoles().get(target.getUniqueId()) == null) {
            player.sendMessage("§cЭтот игрок не участвует в игре!");
            return true;
        }

        game.castVote(player, target);
        player.sendMessage("§aТы проголосовал за: §f" + target.getName());
        return true;
    }
}