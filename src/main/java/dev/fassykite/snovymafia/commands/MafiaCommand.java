package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.MafiaGame;
import dev.fassykite.snovymafia.gui.MainGui;
import org.bukkit.Bukkit;
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
        if (args.length == 0) {
            if (sender instanceof Player player) {
                if (plugin.getLeaders().contains(player.getName())) {
                    MainGui.open(player, plugin);
                } else {
                    player.sendMessage("🎭 §cТы не ведущий!");
                }
            } else {
                sender.sendMessage("Только игроки могут открыть GUI.");
            }
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "join": // /mafia join
                if (sender instanceof Player player) {
                    MafiaGame game = plugin.getCurrentGame();
                    if (game == null) {
                        player.sendMessage("🎭 §cИгра ещё не началась или уже идёт!");
                        return true;
                    }

                    if (game.getPhase() != MafiaGame.Phase.WAITING) {
                        player.sendMessage("🎭 §cИгра уже идёт или не в фазе ожидания.");
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
                } else {
                    sender.sendMessage("Только игроки могут использовать эту команду.");
                }
                return true;

            case "leave": // /mafia leave
                if (sender instanceof Player player) {
                    MafiaGame game = plugin.getCurrentGame();
                    if (game == null) {
                        player.sendMessage("🎭 §cИгра ещё не началась или уже идёт!");
                        return true;
                    }

                    if (game.getPhase() != MafiaGame.Phase.WAITING) {
                        player.sendMessage("🎭 §cИгра уже идёт или не в фазе ожидания.");
                        return true;
                    }

                    if (game.removePlayerFromQueue(player)) {
                        player.sendMessage("🎭 §aТы вышел из очереди.");
                    } else {
                        player.sendMessage("🎭 §cТы не записан в игру.");
                    }
                } else {
                    sender.sendMessage("Только игроки могут использовать эту команду.");
                }
                return true;

            case "leading": // /mafia leading add/remove <ник>
                if (!sender.hasPermission("snovymafia.admin") && !sender.isOp()) {
                    sender.sendMessage("🎭 §cНет прав.");
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage("🎭 §eИспользуй: /mafia leading add/remove <ник>");
                    return true;
                }

                String action = args[1].toLowerCase();
                String target = args[2];

                if (action.equals("add")) {
                    plugin.getLeaders().add(target);
                    plugin.saveLeaders();
                    sender.sendMessage("🎭 §aДобавлен ведущий: §f" + target);
                } else if (action.equals("remove")) {
                    plugin.getLeaders().remove(target);
                    plugin.saveLeaders();
                    sender.sendMessage("🎭 §cУдалён ведущий: §f" + target);
                } else {
                    sender.sendMessage("🎭 §eИспользуй: /mafia leading add/remove <ник>");
                }
                return true;

            case "stats": // /mafia stats [ник]
                // Передаём в StatsCommand
                new StatsCommand(plugin, plugin.getStatsManager()).onCommand(sender, cmd, label, args.length > 1 ? new String[]{args[1]} : new String[]{});
                return true;

            case "top": // /mafia top
                new TopCommand(plugin, plugin.getStatsManager()).onCommand(sender, cmd, label, new String[]{});
                return true;

            case "history": // /mafia history
                new HistoryCommand(plugin, plugin.getStatsManager()).onCommand(sender, cmd, label, new String[]{});
                return true;

            case "lose": // /mafia lose
                new LoseCommand(plugin, plugin.getStatsManager()).onCommand(sender, cmd, label, new String[]{});
                return true;

            case "winrate": // /mafia winrate
                new WinRateCommand(plugin, plugin.getStatsManager()).onCommand(sender, cmd, label, new String[]{});
                return true;

            case "roles": // /mafia roles
                new RolesCommand(plugin, plugin.getStatsManager()).onCommand(sender, cmd, label, new String[]{});
                return true;

            case "reload": // /mafia reload
                if (!sender.hasPermission("snovymafia.admin") && !sender.isOp()) {
                    sender.sendMessage("🎭 §cНет прав.");
                    return true;
                }
                plugin.reloadConfigData();
                sender.sendMessage("🎭 §aКонфиг перезагружен.");
                return true;

            case "info": // /mafia info
                new InfoCommand(plugin).onCommand(sender, cmd, label, new String[]{});
                return true;

            default:
                sender.sendMessage("🎭 §eИспользуй: /mafia join, /mafia leave, /mafia stats, /mafia top, /mafia info, /mafia reload");
                return true;
        }
    }
}