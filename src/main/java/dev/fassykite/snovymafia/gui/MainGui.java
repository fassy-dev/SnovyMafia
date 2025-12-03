package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.Role;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class MainGui implements Listener {
    private static final String TITLE = "🎭 SnovyMafia | Главное меню";

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        // 🎮 Управление игрой
        inv.setItem(10, GuiUtil.createGuiItem(Material.EMERALD_BLOCK, "▶ Начать игру (60с)",
                " ",
                "§7Запуск игры с 60-секундным отсчётом",
                "§7Участники: §eзаписавшиеся или онлайн"));

        inv.setItem(11, GuiUtil.createGuiItem(Material.LIME_CONCRETE, "⚡ Начать игру (сразу)",
                " ",
                "§7Запуск игры без ожидания",
                "§7Игра начнётся сразу"));

        inv.setItem(12, GuiUtil.createGuiItem(Material.REDSTONE_BLOCK, "⏹ Остановить игру",
                " ",
                "§7Принудительно завершить текущую игру"));

        // ⚙️ Настройки
        inv.setItem(20, GuiUtil.createGuiItem(Material.HOPPER, "⚙️ Настройки",
                " ",
                "§7Изменить время, роли, сообщения"));

        inv.setItem(21, GuiUtil.createGuiItem(Material.COMPARATOR, "🎭 Настроить роли",
                " ",
                "§7Включить/выключить роли"));

        // 👤 Участие
        inv.setItem(30, GuiUtil.createGuiItem(Material.PLAYER_HEAD, "📥 Записаться в игру",
                " ",
                "§7Добавить себя в очередь участников"));

        inv.setItem(31, GuiUtil.createGuiItem(Material.BARRIER, "🚪 Выйти из очереди",
                " ",
                "§7Убрать себя из очереди"));

        // 📊 Информация (с PlaceholderAPI)
        String enabledRolesPlaceholder = "%snovymafia_enabled_roles%";
        String queuedPlayersPlaceholder = "%snovymafia_queued_players%";

        inv.setItem(40, GuiUtil.createGuiItem(Material.OAK_SIGN, "🎭 Активные роли",
                " ",
                "§7Включено: §c" + PlaceholderAPI.setPlaceholders(player, enabledRolesPlaceholder)));

        inv.setItem(41, GuiUtil.createGuiItem(Material.PLAYER_HEAD, "👥 Игроки в очереди",
                " ",
                "§7Записано: §c" + PlaceholderAPI.setPlaceholders(player, queuedPlayersPlaceholder)));

        // ❌ Выход
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
            case 10: // Начать игру (60с)
                if (plugin.getCurrentGame() == null) {
                    new dev.fassykite.snovymafia.game.MafiaGame(plugin).startWithCountdown();
                    player.sendMessage("🎭 §aИгра начнётся через 60 секунд! Пишите §f/mafia join§a, чтобы записаться!");
                } else {
                    player.sendMessage("🎭 §cИгра уже идёт!");
                }
                break;

            case 11: // Начать игру (сразу)
                if (plugin.getCurrentGame() == null) {
                    new dev.fassykite.snovymafia.game.MafiaGame(plugin).startGameImmediately(true);
                    player.sendMessage("🎭 §aИгра запущена СРАЗУ!");
                } else {
                    player.sendMessage("🎭 §cИгра уже идёт!");
                }
                break;

            case 12: // Остановить игру
                if (plugin.getCurrentGame() != null) {
                    plugin.getCurrentGame().endGame();
                    player.sendMessage("🎭 §cИгра остановлена.");
                } else {
                    player.sendMessage("🎭 §7Игра не запущена.");
                }
                break;

            case 20: // Настройки
                SettingsGui.open(player, plugin);
                break;

            case 21: // Настроить роли
                RoleSettingsGui.open(player, plugin);
                break;

            case 30: // Записаться
                if (plugin.getCurrentGame() != null && plugin.getCurrentGame().getPhase() != dev.fassykite.snovymafia.game.MafiaGame.Phase.WAITING) {
                    player.sendMessage("🎭 §cИгра уже идёт или не в фазе ожидания.");
                    break;
                }
                var game = plugin.getCurrentGame();
                if (game == null) game = new dev.fassykite.snovymafia.game.MafiaGame(plugin);
                if (game.addPlayerToQueue(player)) {
                    player.sendMessage("🎭 §aТы успешно записался в игру!");
                } else {
                    player.sendMessage("🎭 §cТы уже в очереди!");
                }
                break;

            case 31: // Выйти из очереди
                if (plugin.getCurrentGame() != null && plugin.getCurrentGame().getPhase() != dev.fassykite.snovymafia.game.MafiaGame.Phase.WAITING) {
                    player.sendMessage("🎭 §cИгра уже идёт или не в фазе ожидания.");
                    break;
                }
                if (plugin.getCurrentGame() != null && plugin.getCurrentGame().removePlayerFromQueue(player)) {
                    player.sendMessage("🎭 §aТы вышел из очереди.");
                } else {
                    player.sendMessage("🎭 §cТы не записан в игру.");
                }
                break;

            case 53: // Закрыть
                player.closeInventory();
                break;
        }
    }
}