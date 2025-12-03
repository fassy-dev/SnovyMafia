package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class TimeSettingsGui implements Listener {
    private static final String TITLE = "⏱ Настройка времени";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 36, TITLE);

        inv.setItem(10, GuiUtil.createGuiItem(Material.REDSTONE, "🌙 Время ночи",
                "Текущее: §c" + plugin.getNightDurationSeconds() + "с",
                " ",
                "§eКликни, чтобы изменить"));

        inv.setItem(12, GuiUtil.createGuiItem(Material.LIME_DYE, "🗣️ Время голосования",
                "Текущее: §c" + plugin.getDayVoteDuration() + "с",
                " ",
                "§eКликни, чтобы изменить"));

        inv.setItem(14, GuiUtil.createGuiItem(Material.CLOCK, "⏰ Время отсчёта",
                "Текущее: §c" + plugin.getStartCountdownDuration() + "с",
                " ",
                "§eКликни, чтобы изменить"));

        inv.setItem(35, GuiUtil.createGuiItem(Material.BARRIER, "❌ Назад"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onTimeSettingsClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        switch (e.getRawSlot()) {
            case 10: // Время ночи
                NightTimeChoiceGui.open(player, plugin);
                break;
            case 12: // Время голосования
                VoteTimeChoiceGui.open(player, plugin);
                break;
            case 14: // Время отсчёта
                StartCountdownChoiceGui.open(player, plugin);
                break;
            case 35: // Назад
                SettingsGui.open(player, plugin);
                break;
        }
    }
}