package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.Role;
import dev.fassykite.snovymafia.game.RoleAlignment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiUtil {

    public static ItemStack createRoleBook(Role role) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta != null) {
            meta.setTitle("Твоя роль");
            meta.setAuthor("SnovyMafia");
            meta.addPage("§l" + role.getDisplayName() + "\n\n§f" + role.getDescription());
            book.setItemMeta(meta);
        }
        return book;
    }

    // 🆕 КНИГА СО ВСЕМИ РОЛЯМИ (даже выключенными)
    public static ItemStack createRolesGuideBook(SnovyMafia plugin) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta != null) {
            meta.setTitle("§6Руководство по Мафии");
            meta.setAuthor("SnovyMafia");

            List<String> lines = new ArrayList<>();
            lines.add("§l§6Все роли в игре:\n");

            // Группируем
            addRoles(lines, "§cМафия", RoleAlignment.MAFIA);
            addRoles(lines, "§aМирные", RoleAlignment.VILLAGER);
            addRoles(lines, "§dНейтралы", RoleAlignment.NEUTRAL);
            addRoles(lines, "§5Нейтралы-убийцы", RoleAlignment.NEUTRAL_KILLING);

            meta.addPage(String.join("\n", lines));
            book.setItemMeta(meta);
        }
        return book;
    }

    private static void addRoles(List<String> lines, String title, dev.fassykite.snovymafia.game.RoleAlignment alignment) {
        lines.add("\n" + title + ":");
        for (Role role : Role.values()) {
            if (role.getAlignment() == alignment) {
                lines.add("§l" + role.getDisplayName() + "§r");
                lines.add("  §7" + role.getDescription());
            }
        }
    }

    public static ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}