package dev.fassykite.snovymafia.listeners;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.MafiaGame;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class NightListener implements Listener {
    private final SnovyMafia plugin;

    public NightListener(SnovyMafia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        // 🔧 ИСПРАВЛЕНО: проверка на двери и люки с безопасностью
        if (isDoor(block.getType()) || isTrapdoor(block.getType())) {
            MafiaGame game = plugin.getCurrentGame();
            if (game != null && game.getPhase() == MafiaGame.Phase.NIGHT) {
                if (game.getRoles().containsKey(p.getUniqueId()) && game.getRoles().get(p.getUniqueId()).hasNightAction()) {
                    e.setCancelled(true); // отменяем стандартное открытие
                    toggleDoor(block); // вручную переключаем
                }
            }
        }
    }

    private boolean isDoor(Material type) {
        String name = type.name();
        return name.endsWith("_DOOR") || name.equals("IRON_DOOR");
    }

    private boolean isTrapdoor(Material type) {
        String name = type.name();
        return name.endsWith("_TRAPDOOR") || name.equals("TRAPDOOR") || name.equals("IRON_TRAPDOOR");
    }

    private void toggleDoor(Block door) {
        org.bukkit.block.data.Openable openable = (org.bukkit.block.data.Openable) door.getBlockData();
        openable.setOpen(!openable.isOpen());
        door.setBlockData(openable);
    }
}