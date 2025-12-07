package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.MafiaGame;
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

        // 🎨 Фон
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, GuiUtil.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        // 🎮 Управление игрой
        boolean gameRunning = plugin.getCurrentGame() != null;
        MafiaGame game = plugin.getCurrentGame();
        boolean inWaitingPhase = game != null && game.getPhase() == MafiaGame.Phase.WAITING;
        boolean inQueue = game != null && game.isPlayerInQueue(player);

        if (gameRunning) {
            inv.setItem(10, GuiUtil.createGuiItem(
                    Material.RED_CONCRETE,
                    "🔴 Игра уже идёт!",
                    " ",
                    "§cИгра уже запущена!"));

            inv.setItem(12, GuiUtil.createGuiItem(
                    Material.REDSTONE_BLOCK,
                    "⏹ Остановить игру",
                    " ",
                    "§7Принудительно завершить текущую игру"));
        } else {
            inv.setItem(10, GuiUtil.createGuiItem(
                    Material.EMERALD_BLOCK,
                    "▶ Начать игру (60с)",
                    " ",
                    "§7Запуск игры с 60-секундным отсчётом",
                    "§7Участники: §eзаписавшиеся или онлайн"));

            inv.setItem(11, GuiUtil.createGuiItem(
                    Material.LIME_CONCRETE,
                    "⚡ Начать игру (сразу)",
                    " ",
                    "§7Запуск игры без ожидания",
                    "§7Игра начнётся сразу"));
        }

        // 🔘 Кнопка "Зайти/Выйти из очереди"
        if (gameRunning && inWaitingPhase && inQueue) {
            inv.setItem(14, GuiUtil.createGuiItem(
                    Material.BARRIER,
                    "🚪 Выйти из очереди",
                    " ",
                    "§7Ты записан в игру.",
                    "§eКликни, чтобы выйти из очереди."));
        } else if (gameRunning && inWaitingPhase && !inQueue) {
            inv.setItem(14, GuiUtil.createGuiItem(
                    Material.PLAYER_HEAD,
                    "📥 Записаться в игру",
                    " ",
                    "§7Игра в режиме ожидания.",
                    "§eКликни, чтобы записаться."));
        } else if (gameRunning && !inWaitingPhase) {
            inv.setItem(14, GuiUtil.createGuiItem(
                    Material.BARRIER,
                    "❌ Игра уже идёт!",
                    " ",
                    "§cТы не можешь записаться или выйти.",
                    "§7Фаза: §e" + game.getPhase().name()));
        } else {
            inv.setItem(14, GuiUtil.createGuiItem(
                    Material.PLAYER_HEAD,
                    "📥 Записаться в игру",
                    " ",
                    "§7Записаться в игру.",
                    "§eКликни, чтобы записаться."));
        }

        // ⚙️ Настройки
        inv.setItem(20, GuiUtil.createGuiItem(Material.HOPPER, "⚙️ Настройки",
                " ",
                "§7Изменить время, роли, сообщения"));

        inv.setItem(21, GuiUtil.createGuiItem(Material.COMPARATOR, "🎭 Настроить роли",
                " ",
                "§7Включить/выключить роли"));

        // 📊 Информация (PlaceholderAPI)
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
                open(player, plugin); // обновляем GUI
                break;

            case 11: // Начать игру (сразу)
                if (plugin.getCurrentGame() == null) {
                    new dev.fassykite.snovymafia.game.MafiaGame(plugin).startGameImmediately(true);
                    player.sendMessage("🎭 §aИгра запущена СРАЗУ!");
                } else {
                    player.sendMessage("🎭 §cИгра уже идёт!");
                }
                open(player, plugin); // обновляем GUI
                break;

            case 12: // Остановить игру
                if (plugin.getCurrentGame() != null) {
                    plugin.getCurrentGame().endGame();
                    player.sendMessage("🎭 §cИгра остановлена.");
                } else {
                    player.sendMessage("🎭 §7Игра не запущена.");
                }
                open(player, plugin); // обновляем GUI
                break;

            case 14: // Зайти/Выйти из очереди
                MafiaGame game = plugin.getCurrentGame();
                if (game == null) {
                    // Игра не запущена — невозможно записаться
                    player.sendMessage("🎭 §cСначала начни игру!");
                    break;
                }

                if (game.getPhase() != MafiaGame.Phase.WAITING) {
                    player.sendMessage("🎭 §cИгра уже идёт, нельзя записаться/выйти.");
                    break;
                }

                if (game.isPlayerInQueue(player)) {
                    if (game.removePlayerFromQueue(player)) {
                        player.sendMessage("🎭 §aТы вышел из очереди.");
                    } else {
                        player.sendMessage("🎭 §cТы не записан в игру.");
                    }
                } else {
                    if (game.addPlayerToQueue(player)) {
                        player.sendMessage("🎭 §aТы записался в игру!");
                    } else {
                        player.sendMessage("🎭 §cТы уже в очереди!");
                    }
                }
                open(player, plugin); // обновляем GUI
                break;

            case 20: // Настройки
                SettingsGui.open(player, plugin);
                break;

            case 21: // Настроить роли
                RoleSettingsGui.open(player, plugin);
                break;

            case 53: // Закрыть
                player.closeInventory();
                break;
        }
    }
}