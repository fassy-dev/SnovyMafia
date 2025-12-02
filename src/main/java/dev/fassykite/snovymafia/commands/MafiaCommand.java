package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.MafiaGame;
import dev.fassykite.snovymafia.gui.MainGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MafiaCommand implements CommandExecutor {
    private final SnovyMafia plugin;

    public MafiaCommand(SnovyMafia plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("🎭 §fТолько игроки!");
            return true;
        }

        if (args.length == 0) {
            if (plugin.getLeaders().contains(player.getName())) {
                MainGui.open(player, plugin);
            } else {
                player.sendMessage("🎭 §4Ты не ведущий!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            var game = plugin.getCurrentGame();
            if (game == null || game.getPhase() != MafiaGame.Phase.WAITING) {
                player.sendMessage("🎭 §cИгра ещё не началась или уже идёт!");
                return true;
            }

            if (plugin.getLeaders().contains(player.getName())) {
                player.sendMessage("🎭 §cВедущие не могут участвовать!");
                return true;
            }

            if (game.addPlayerToQueue(player)) {
                player.sendMessage("🎭 §aТы успешно записался в игру!");
            } else {
                player.sendMessage("🎭 §cТы уже в очереди!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            var game = plugin.getCurrentGame();
            if (game == null || game.getPhase() != MafiaGame.Phase.WAITING) {
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

        if (args[0].equalsIgnoreCase("leading") && args.length >= 3) {
            if (!player.hasPermission("snovymafia.admin") && !player.isOp()) {
                player.sendMessage("🎭 §cНет прав");
                return true;
            }

            String target = args[2];
            if (args[1].equalsIgnoreCase("add")) {
                plugin.getLeaders().add(target);
                plugin.saveLeaders();
                player.sendMessage("🎭 §aДобавлен ведущий: " + target);
            } else if (args[1].equalsIgnoreCase("remove")) {
                plugin.getLeaders().remove(target);
                plugin.saveLeaders();
                player.sendMessage("🎭 §cУдалён ведущий: " + target);
            } else {
                player.sendMessage("🎭 §eИспользуй: /mafia leading (add/remove) <ник>");
            }
            return true;
        }

        player.sendMessage("🎭 §eИспользуй: /mafia join, /mafia leave, /mafia leading (add/remove) <ник>");
        return true;
    }
}