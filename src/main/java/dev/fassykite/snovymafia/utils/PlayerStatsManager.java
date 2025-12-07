package dev.fassykite.snovymafia.utils;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.game.Role;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatsManager {
    private final SnovyMafia plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public PlayerStatsManager(SnovyMafia plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "player_data.yml");
        loadData();
    }

    public void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create player_data.yml: " + e.getMessage());
                return;
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player_data.yml: " + e.getMessage());
        }
    }

    // Получить статистику игрока
    public Map<String, Object> getPlayerStats(UUID uuid) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("games_played", dataConfig.getInt(uuid + ".games_played", 0));
        stats.put("wins", dataConfig.getInt(uuid + ".wins", 0));
        stats.put("losses", dataConfig.getInt(uuid + ".losses", 0));
        stats.put("kills", dataConfig.getInt(uuid + ".kills", 0));
        stats.put("heals", dataConfig.getInt(uuid + ".heals", 0));

        Map<Role, Integer> rolesPlayed = new HashMap<>();
        for (Role role : Role.values()) {
            rolesPlayed.put(role, dataConfig.getInt(uuid + ".roles." + role.getConfigKey(), 0));
        }
        stats.put("roles_played", rolesPlayed);

        return stats;
    }

    // Обновить статистику после игры
    public void updateStatsAfterGame(UUID uuid, boolean won, Role role, boolean wasKiller, boolean wasHealer) {
        int gamesPlayed = dataConfig.getInt(uuid + ".games_played", 0) + 1;
        int wins = dataConfig.getInt(uuid + ".wins", 0);
        int losses = dataConfig.getInt(uuid + ".losses", 0);
        int kills = dataConfig.getInt(uuid + ".kills", 0);
        int heals = dataConfig.getInt(uuid + ".heals", 0);

        if (won) wins++;
        else losses++;

        if (wasKiller) kills++;
        if (wasHealer) heals++;

        int rolePlayedCount = dataConfig.getInt(uuid + ".roles." + role.getConfigKey(), 0) + 1;

        dataConfig.set(uuid + ".games_played", gamesPlayed);
        dataConfig.set(uuid + ".wins", wins);
        dataConfig.set(uuid + ".losses", losses);
        dataConfig.set(uuid + ".kills", kills);
        dataConfig.set(uuid + ".heals", heals);
        dataConfig.set(uuid + ".roles." + role.getConfigKey(), rolePlayedCount);

        saveData();
    }

    // 🔥 Добавлен метод для получения конфига (для TopCommand)
    public FileConfiguration getDataConfig() {
        return dataConfig;
    }
}