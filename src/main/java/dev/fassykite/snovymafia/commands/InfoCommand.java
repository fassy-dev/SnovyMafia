package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.utils.GradientUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class InfoCommand implements CommandExecutor {
    private final SnovyMafia plugin;

    public InfoCommand(SnovyMafia plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String msg = GradientUtil.applyMafiaGradient("=== SnovyMafia ===\n" +
                "Версия: " + plugin.getDescription().getVersion() + "\n" +
                "Автор: Fassykite\n" +
                "Описание: Полноценная игра Мафия с градиентами и PlaceholderAPI\n" +
                "Команды: /mafia, /mafia stats, /mafia info");
        sender.sendMessage(GradientUtil.parse(msg));
        return true;
    }
}