package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class MessageSettingsGui implements Listener {
    private static final String TITLE = "💬 Настройка сообщений";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 36, TITLE);

        inv.setItem(10, GuiUtil.createGuiItem(Material.NAME_TAG, "✅ Сообщение при входе",
                "Текущее: §f" + plugin.getMessageJoinSuccess(),
                " ",
                "§eКликни, чтобы изменить"));

        inv.setItem(12, GuiUtil.createGuiItem(Material.NAME_TAG, "❌ Сообщение при выходе",
                "Текущее: §f" + plugin.getMessageLeaveSuccess(),
                " ",
                "§eКликни, чтобы изменить"));

        inv.setItem(14, GuiUtil.createGuiItem(Material.NAME_TAG, "🎉 Сообщение о победе мафии",
                "Текущее: §f" + plugin.getMessageWinMafia(),
                " ",
                "§eКликни, чтобы изменить"));

        inv.setItem(16, GuiUtil.createGuiItem(Material.NAME_TAG, "🎉 Сообщение о победе мирных",
                "Текущее: §f" + plugin.getMessageWinVillagers(),
                " ",
                "§eКликни, чтобы изменить"));

        inv.setItem(35, GuiUtil.createGuiItem(Material.BARRIER, "❌ Назад"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onMessageSettingsClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        switch (e.getRawSlot()) {
            case 10: // При входе
                // Здесь можно открыть GUI с вводом текста (если хочешь — могу сделать)
                player.sendMessage("💬 §cЭта функция пока не реализована через GUI.");
                break;
            case 12: // При выходе
                player.sendMessage("💬 §cЭта функция пока не реализована через GUI.");
                break;
            case 14: // Победа мафии
                player.sendMessage("💬 §cЭта функция пока не реализована через GUI.");
                break;
            case 16: // Победа мирных
                player.sendMessage("💬 §cЭта функция пока не реализована через GUI.");
                break;
            case 35: // Назад
                SettingsGui.open(player, plugin);
                break;
        }
    }
}