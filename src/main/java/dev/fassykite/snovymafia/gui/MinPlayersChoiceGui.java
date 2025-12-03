package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class MinPlayersChoiceGui implements Listener {
    private static final String TITLE = "👥 Выбор мин. игроков";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, TITLE);

        int[] counts = {2, 3, 4, 5, 6};
        int slot = 0;

        for (int count : counts) {
            boolean current = plugin.getMinPlayersToStart() == count;
            Material mat = current ? Material.GREEN_CONCRETE : Material.GRAY_CONCRETE;
            String name = current ? "§a" + count : "§f" + count;
            inv.setItem(slot, GuiUtil.createGuiItem(mat, name));
            slot++;
        }

        inv.setItem(26, GuiUtil.createGuiItem(Material.BARRIER, "❌ Назад"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onMinPlayersClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        if (e.getRawSlot() == 26) {
            OtherSettingsGui.open(player, plugin);
            return;
        }

        int[] counts = {2, 3, 4, 5, 6};
        if (e.getRawSlot() < counts.length) {
            int newCount = counts[e.getRawSlot()];
            plugin.setMinPlayersToStart(newCount);
            player.sendMessage("🎭 §fМин. игроков изменено на §c" + newCount + "§f.");
            OtherSettingsGui.open(player, plugin);
        }
    }
}