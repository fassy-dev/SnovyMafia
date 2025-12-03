package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class OtherSettingsGui implements Listener {
    private static final String TITLE = "🔧 Прочие настройки";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 36, TITLE);

        boolean silentDoors = plugin.isSilentDoorsEnabled();
        boolean syncTime = plugin.isSyncMinecraftTime();
        boolean scoreboard = plugin.isScoreboardEnabled();
        boolean allowJoin = plugin.isAllowJoinDuringGame();
        boolean allowLeave = plugin.isAllowLeaveDuringGame();

        inv.setItem(10, GuiUtil.createGuiItem(
                silentDoors ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE,
                "🔇 Тихие двери: " + (silentDoors ? "ВКЛ" : "ВЫКЛ"),
                " ",
                "§eКликни, чтобы переключить"));

        inv.setItem(12, GuiUtil.createGuiItem(
                syncTime ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE,
                "🌙 Синхронизация времени: " + (syncTime ? "ВКЛ" : "ВЫКЛ"),
                " ",
                "§eКликни, чтобы переключить"));

        inv.setItem(14, GuiUtil.createGuiItem(
                scoreboard ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE,
                "📊 Scoreboard: " + (scoreboard ? "ВКЛ" : "ВЫКЛ"),
                " ",
                "§eКликни, чтобы переключить"));

        inv.setItem(16, GuiUtil.createGuiItem(
                allowJoin ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE,
                "📥 Разрешить вход во время игры: " + (allowJoin ? "ВКЛ" : "ВЫКЛ"),
                " ",
                "§eКликни, чтобы переключить"));

        inv.setItem(35, GuiUtil.createGuiItem(Material.BARRIER, "❌ Назад"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onOtherSettingsClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        switch (e.getRawSlot()) {
            case 10: // Тихие двери
                plugin.setSilentDoorsEnabled(!plugin.isSilentDoorsEnabled());
                player.sendMessage("🔧 §fТихие двери: " + (plugin.isSilentDoorsEnabled() ? "ВКЛ" : "ВЫКЛ"));
                OtherSettingsGui.open(player, plugin);
                break;
            case 12: // Синхронизация времени
                plugin.setSyncMinecraftTime(!plugin.isSyncMinecraftTime());
                player.sendMessage("🔧 §fСинхронизация времени: " + (plugin.isSyncMinecraftTime() ? "ВКЛ" : "ВЫКЛ"));
                OtherSettingsGui.open(player, plugin);
                break;
            case 14: // Scoreboard
                plugin.setScoreboardEnabled(!plugin.isScoreboardEnabled());
                player.sendMessage("🔧 §fScoreboard: " + (plugin.isScoreboardEnabled() ? "ВКЛ" : "ВЫКЛ"));
                OtherSettingsGui.open(player, plugin);
                break;
            case 16: // Вход во время игры
                plugin.setAllowJoinDuringGame(!plugin.isAllowJoinDuringGame());
                player.sendMessage("🔧 §fВход во время игры: " + (plugin.isAllowJoinDuringGame() ? "ВКЛ" : "ВЫКЛ"));
                OtherSettingsGui.open(player, plugin);
                break;
            case 35: // Назад
                SettingsGui.open(player, plugin);
                break;
        }
    }
}