package dev.fassykite.snovymafia.commands;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.utils.GradientUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final SnovyMafia plugin;

    public ReloadCommand(SnovyMafia plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("snovymafia.admin") && !sender.isOp()) {
            sender.sendMessage("Нет прав.");
            return true;
        }

        plugin.reloadConfigData();
        sender.sendMessage(GradientUtil.parse(GradientUtil.applyMafiaGradient("Конфиг перезагружен!")));
        return true;
    }
}