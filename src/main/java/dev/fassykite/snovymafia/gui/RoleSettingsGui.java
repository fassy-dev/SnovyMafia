package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.Role;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class RoleSettingsGui implements Listener {
    private static final String TITLE = "§6Настройка ролей";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, TITLE);
        int slot = 0;

        for (Role role : Role.values()) {
            if (slot >= 54) break;

            boolean enabled = plugin.isRoleEnabled(role);
            Material mat = enabled ? Material.GREEN_DYE : Material.RED_DYE;
            String name = (enabled ? "§a" : "§c") + role.getDisplayName();
            String[] lore = {
                    "§7" + role.getDescription(),
                    "",
                    enabled ? "§eКлик: выключить" : "§eКлик: включить"
            };

            inv.setItem(slot, GuiUtil.createGuiItem(mat, name, lore));
            slot++;
        }

        inv.setItem(53, GuiUtil.createGuiItem(Material.BARRIER, "§cНазад"));
        player.openInventory(inv);
    }

    @EventHandler
    public void onRoleSettingClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getRawSlot() == 53) {
            MainGui.open(player, plugin);
            return;
        }

        int index = e.getRawSlot();
        if (index < 0 || index >= Role.values().length) return;

        Role role = Role.values()[index];
        boolean currentlyEnabled = plugin.isRoleEnabled(role);
        plugin.setRoleEnabled(role, !currentlyEnabled);

        open(player, plugin);
    }
}