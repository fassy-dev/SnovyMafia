package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class SettingsGui implements Listener {
    private static final String TITLE = "⚙️ Настройки";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, TITLE);

        // Кнопки настроек
        inv.setItem(11, GuiUtil.createGuiItem(Material.CLOCK, "⏱ Время игры",
                "Текущее время ночи: §c" + plugin.getNightDurationSeconds() + "с",
                "Текущее время голосования: §c" + plugin.getDayVoteDuration() + "с"));

        inv.setItem(13, GuiUtil.createGuiItem(Material.COMPARATOR, "🔧 Настроить роли",
                "Изменить включённые роли"));

        inv.setItem(15, GuiUtil.createGuiItem(Material.BARRIER, "❌ Закрыть"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onSettingsClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        switch (e.getRawSlot()) {
            case 11: // Время
                TimeSettingsGui.open(player, plugin);
                break;
            case 13: // Роли
                RoleSettingsGui.open(player, plugin);
                break;
            case 15: // Закрыть
                player.closeInventory();
                break;
        }
    }
}