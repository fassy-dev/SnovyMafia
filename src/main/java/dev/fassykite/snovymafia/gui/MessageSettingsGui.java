package dev.fassykite.snovymafia.gui;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.utils.GradientUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageSettingsGui implements Listener {
    private static final String TITLE = "💬 Настройка сообщений";

    // 🔥 Карта для отслеживания игроков, ожидающих ввод текста
    private static final Map<UUID, String> waitingForInput = new HashMap<>();

    // 🔧 Ключи сообщений для разных слотов
    private static final Map<Integer, String> MESSAGE_KEYS = new HashMap<>();
    static {
        MESSAGE_KEYS.put(10, "message-join-success");
        MESSAGE_KEYS.put(12, "message-leave-success");
        MESSAGE_KEYS.put(14, "message-win-mafia");
        MESSAGE_KEYS.put(16, "message-win-villagers");
        MESSAGE_KEYS.put(18, "message-win-maniac");
        MESSAGE_KEYS.put(20, "message-win-tie");
        MESSAGE_KEYS.put(22, "message-game-started");
        MESSAGE_KEYS.put(24, "message-game-started-with-countdown");
        MESSAGE_KEYS.put(26, "message-not-enough-players");
        MESSAGE_KEYS.put(28, "message-game-ended");
    }

    public static void open(Player player, SnovyMafia plugin) {
        Inventory inv = Bukkit.createInventory(null, 36, TITLE);

        // 📝 Заполняем GUI сообщениями
        for (Map.Entry<Integer, String> entry : MESSAGE_KEYS.entrySet()) {
            String key = entry.getValue();
            String currentValue = getValueForKey(plugin, key);

            inv.setItem(entry.getKey(), GuiUtil.createGuiItem(
                    Material.NAME_TAG,
                    capitalizeFirstLetter(formatKey(key)),
                    " ",
                    "§fТекущее:",
                    "§7" + PlaceholderAPI.setPlaceholders(player, currentValue),
                    " ",
                    "§eКликни, чтобы изменить"
            ));
        }

        inv.setItem(35, GuiUtil.createGuiItem(Material.BARRIER, "❌ Назад"));

        player.openInventory(inv);
    }

    private static String getValueForKey(SnovyMafia plugin, String key) {
        switch (key) {
            case "message-join-success": return plugin.getMessageJoinSuccess();
            case "message-leave-success": return plugin.getMessageLeaveSuccess();
            case "message-win-mafia": return plugin.getMessageWinMafia();
            case "message-win-villagers": return plugin.getMessageWinVillagers();
            case "message-win-maniac": return plugin.getMessageWinManiac();
            case "message-win-tie": return plugin.getMessageWinTie();
            case "message-game-started": return plugin.getMessageGameStarted();
            case "message-game-started-with-countdown": return plugin.getMessageGameStartedWithCountdown();
            case "message-not-enough-players": return plugin.getMessageNotEnoughPlayers();
            case "message-game-ended": return plugin.getMessageGameEnded();
            default: return "Неизвестное сообщение";
        }
    }

    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String formatKey(String key) {
        return key.replace("message-", "").replace("-", " ");
    }

    @EventHandler
    public void onMessageSettingsClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        SnovyMafia plugin = SnovyMafia.getInstance();

        String key = MESSAGE_KEYS.get(e.getRawSlot());
        if (key != null) {
            // 🔥 Начинаем ожидать ввод текста
            waitingForInput.put(player.getUniqueId(), key);
            player.closeInventory();
            player.sendMessage(GradientUtil.parse(GradientUtil.applyMafiaGradient(
                    "📝 Введите новое сообщение для '" + capitalizeFirstLetter(formatKey(key)) + "':"
            )));
            player.sendMessage("💬 §7Введите текст в чат. Напишите §c\"cancel\"§7, чтобы отменить.");
            return;
        }

        if (e.getRawSlot() == 35) { // Назад
            SettingsGui.open(player, plugin);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!waitingForInput.containsKey(playerId)) return;

        e.setCancelled(true);

        String input = e.getMessage();
        if (input.equalsIgnoreCase("cancel")) {
            waitingForInput.remove(playerId);
            player.sendMessage(GradientUtil.parse(GradientUtil.applyMafiaGradient("Изменение сообщения отменено.")));
            open(player, SnovyMafia.getInstance());
            return;
        }

        String key = waitingForInput.remove(playerId);

        // 🔧 Устанавливаем новое значение
        setValueForKey(SnovyMafia.getInstance(), key, input);
        player.sendMessage(GradientUtil.parse(GradientUtil.applyMafiaGradient(
                "✅ Сообщение '" + capitalizeFirstLetter(formatKey(key)) + "' изменено на:"
        )));
        player.sendMessage("💬 §f" + PlaceholderAPI.setPlaceholders(player, input));

        // 🔁 Открываем GUI снова
        open(player, SnovyMafia.getInstance());
    }

    private static void setValueForKey(SnovyMafia plugin, String key, String value) {
        switch (key) {
            case "message-join-success": plugin.setMessageJoinSuccess(value); break;
            case "message-leave-success": plugin.setMessageLeaveSuccess(value); break;
            case "message-win-mafia": plugin.setMessageWinMafia(value); break;
            case "message-win-villagers": plugin.setMessageWinVillagers(value); break;
            case "message-win-maniac": plugin.setMessageWinManiac(value); break;
            case "message-win-tie": plugin.setMessageWinTie(value); break;
            case "message-game-started": plugin.setMessageGameStarted(value); break;
            case "message-game-started-with-countdown": plugin.setMessageGameStartedWithCountdown(value); break;
            case "message-not-enough-players": plugin.setMessageNotEnoughPlayers(value); break;
            case "message-game-ended": plugin.setMessageGameEnded(value); break;
        }
    }
}