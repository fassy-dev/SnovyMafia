package dev.fassykite.snovymafia.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MafiaTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // Подсказываем первые аргументы
            if (sender.hasPermission("snovymafia.admin") || sender.isOp()) {
                suggestions.add("join");
                suggestions.add("leave");
                suggestions.add("leading");
            } else {
                suggestions.add("join");
                suggestions.add("leave");
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("leading")) {
            suggestions.add("add");
            suggestions.add("remove");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("leading")) {
            suggestions.add("<ник>");
        }

        return suggestions;
    }
}