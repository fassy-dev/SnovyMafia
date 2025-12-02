package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.Role;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class MainGui implements Listener {
    private static final String TITLE = "🎭 SnovyMafia Управление";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, TITLE);

        inv.setItem(0, GuiUtil.createGuiItem(Material.EMERALD, "▶ Начать игру (60с)"));
        inv.setItem(1, GuiUtil.createGuiItem(Material.LIME_CONCRETE, "⚡ Начать игру (сразу)"));
        inv.setItem(2, GuiUtil.createGuiItem(Material.REDSTONE, "⏹ Остановить игру"));

        inv.setItem(7, GuiUtil.createGuiItem(Material.COMPARATOR, "🔧 Настройки ролей"));
        inv.setItem(8, GuiUtil.createGuiItem(Material.HOPPER, "⚙️ Настройки"));

        int enabledRoles = (int) Arrays.stream(Role.values())
                .filter(plugin::isRoleEnabled)
                .count();
        int queuedCount = (plugin.getCurrentGame() != null) ? plugin.getCurrentGame().getQueuedPlayerCount() : 0;

        inv.setItem(45, GuiUtil.createGuiItem(Material.OAK_SIGN, "Роли", "Включено: " + enabledRoles + "/20"));
        inv.setItem(46, GuiUtil.createGuiItem(Material.PLAYER_HEAD, "Игроки", "Записано: " + queuedCount));
        inv.setItem(53, GuiUtil.createGuiItem(Material.BARRIER, "❌ Закрыть"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onMainGuiClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        switch (e.getRawSlot()) {
            case 0:
                if (plugin.getCurrentGame() == null) {
                    new dev.fassykite.snovymafia.game.MafiaGame(plugin).startWithCountdown();
                    player.sendMessage("🎭 §aИгра начнётся через 60 секунд! Пишите §f/mafia join§a, чтобы записаться!");
                } else {
                    player.sendMessage("🎭 §cИгра уже идёт!");
                }
                break;

            case 1: // Начать игру (сразу)
                if (plugin.getCurrentGame() == null) {
                    new dev.fassykite.snovymafia.game.MafiaGame(plugin).startGameImmediately(true); // ← НОВЫЙ МЕТОД
                    player.sendMessage("🎭 §aМоментальный запуск игры!");
                } else {
                    player.sendMessage("🎭 §cИгра уже идёт!");
                }
                break;

            case 2: // Остановить игру
                if (plugin.getCurrentGame() != null) {
                    plugin.getCurrentGame().endGame();
                    player.sendMessage("🎭 §cИгра остановлена.");
                } else {
                    player.sendMessage("🎭 §7Игра не запущена.");
                }
                break;

            case 7:
                RoleSettingsGui.open(player, plugin);
                break;

            case 8:
                SettingsGui.open(player, plugin);
                break;

            case 53:
                player.closeInventory();
                break;
        }
    }
}