package dev.fassykite.snovymafia.expansion;

import dev.fassykite.snovymafia.SnovyMafia;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class SnovyMafiaExpansion extends PlaceholderExpansion {
    private final SnovyMafia plugin;

    public SnovyMafiaExpansion(SnovyMafia plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "snovymafia";
    }

    @Override
    public String getAuthor() {
        return "Fassykite";
    }

    @Override
    public String getVersion() {
        return "2.12.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equals("enabled_roles")) {
            long count = java.util.Arrays.stream(dev.fassykite.snovymafia.game.Role.values())
                    .filter(plugin::isRoleEnabled)
                    .count();
            return String.valueOf(count);
        }

        if (identifier.equals("queued_players")) {
            var game = plugin.getCurrentGame();
            if (game != null) {
                return String.valueOf(game.getQueuedPlayerCount());
            }
            return "0";
        }

        return null;
    }

    public boolean register() {
        return true;
    }
}