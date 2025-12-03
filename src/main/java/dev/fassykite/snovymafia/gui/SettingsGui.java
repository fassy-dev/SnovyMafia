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
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 36, TITLE);

        inv.setItem(10, GuiUtil.createGuiItem(Material.CLOCK, "⏱ Время игры"));
        inv.setItem(11, GuiUtil.createGuiItem(Material.COMPARATOR, "🎭 Настройки ролей"));
        inv.setItem(12, GuiUtil.createGuiItem(Material.NAME_TAG, "💬 Префиксы и сообщения"));
        inv.setItem(13, GuiUtil.createGuiItem(Material.BARRIER, "❌ Прочие настройки"));

        inv.setItem(35, GuiUtil.createGuiItem(Material.BARRIER, "❌ Назад"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onSettingsClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        switch (e.getRawSlot()) {
            case 10: // Время
                TimeSettingsGui.open(player, plugin);
                break;
            case 11: // Роли
                RoleSettingsGui.open(player, plugin);
                break;
            case 12: // Префиксы
                MessageSettingsGui.open(player, plugin);
                break;
            case 13: // Прочее
                OtherSettingsGui.open(player, plugin);
                break;
            case 35: // Назад
                MainGui.open(player, plugin);
                break;
        }
    }
}