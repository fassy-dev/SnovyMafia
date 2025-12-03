package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class StartCountdownChoiceGui implements Listener {
    private static final String TITLE = "⏰ Выбор времени отсчёта";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, TITLE);

        int[] times = {10, 30, 60, 90, 120, 180};
        int slot = 0;

        for (int time : times) {
            boolean current = plugin.getStartCountdownDuration() == time;
            Material mat = current ? Material.GREEN_CONCRETE : Material.GRAY_CONCRETE;
            String name = current ? "§a" + time + "с" : "§f" + time + "с";
            inv.setItem(slot, GuiUtil.createGuiItem(mat, name));
            slot++;
        }

        inv.setItem(26, GuiUtil.createGuiItem(Material.BARRIER, "❌ Назад"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onStartCountdownClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        if (e.getRawSlot() == 26) {
            TimeSettingsGui.open(player, plugin);
            return;
        }

        int[] times = {10, 30, 60, 90, 120, 180};
        if (e.getRawSlot() < times.length) {
            int newTime = times[e.getRawSlot()];
            plugin.setStartCountdownDuration(newTime);
            player.sendMessage("🎭 §fВремя отсчёта изменено на §c" + newTime + "с§f.");
            TimeSettingsGui.open(player, plugin);
        }
    }
}