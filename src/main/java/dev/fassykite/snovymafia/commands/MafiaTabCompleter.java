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
            if (sender.hasPermission("snovymafia.admin") || sender.isOp()) {
                suggestions.add("goin");
                suggestions.add("leave");
                suggestions.add("leading");
                suggestions.add("stats");
                suggestions.add("top");
                suggestions.add("history");
                suggestions.add("lose");
                suggestions.add("winrate");
                suggestions.add("roles");
                suggestions.add("reload");
                suggestions.add("info");
            } else {
                suggestions.add("goin");
                suggestions.add("leave");
                suggestions.add("stats");
                suggestions.add("top");
                suggestions.add("history");
                suggestions.add("lose");
                suggestions.add("winrate");
                suggestions.add("roles");
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("leading")) {
            suggestions.add("add");
            suggestions.add("remove");
        }

        return suggestions;
    }
}