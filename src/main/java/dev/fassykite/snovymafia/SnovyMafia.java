package dev.fassykite.snovymafia;

import dev.fassykite.snovymafia.commands.MafiaCommand;
import dev.fassykite.snovymafia.commands.MafiaTabCompleter;
import dev.fassykite.snovymafia.game.MafiaGame;
import dev.fassykite.snovymafia.gui.*;
import dev.fassykite.snovymafia.listeners.NightListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class SnovyMafia extends JavaPlugin {
    private static SnovyMafia instance;
    private Set<String> leaders = new HashSet<>();
    private int nightDurationSeconds = 120;
    private int dayVoteDuration = 60; // ← ДОБАВЛЕНО
    private MafiaGame currentGame;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadConfig();

        // Регистрация команд
        getCommand("mafia").setExecutor(new MafiaCommand(this));
        getCommand("mafia").setTabCompleter(new MafiaTabCompleter());

        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(new MafiaGame(this), this);
        getServer().getPluginManager().registerEvents(new NightListener(this), this);
        getServer().getPluginManager().registerEvents(new MainGui(), this);
        getServer().getPluginManager().registerEvents(new RoleSettingsGui(), this);
        getServer().getPluginManager().registerEvents(new SettingsGui(), this);
        getServer().getPluginManager().registerEvents(new TimeSettingsGui(), this);
        getServer().getPluginManager().registerEvents(new NightTimeChoiceGui(), this);
        getServer().getPluginManager().registerEvents(new VoteTimeChoiceGui(), this);
    }

    public void reloadConfigData() {
        reloadConfig();
        loadConfig();
    }

    private void loadConfig() {
        nightDurationSeconds = getConfig().getInt("night-duration", 120);
        dayVoteDuration = getConfig().getInt("day-vote-duration", 60); // ← ДОБАВЛЕНО
        leaders.clear();
        getConfig().getStringList("leaders").forEach(leaders::add);
    }

    public boolean isRoleEnabled(dev.fassykite.snovymafia.game.Role role) {
        return getConfig().getBoolean("enabled-roles." + role.getConfigKey(), true);
    }

    public void setRoleEnabled(dev.fassykite.snovymafia.game.Role role, boolean enabled) {
        getConfig().set("enabled-roles." + role.getConfigKey(), enabled);
        saveConfig();
    }

    public void saveLeaders() {
        getConfig().set("leaders", leaders.stream().toList());
        saveConfig();
    }

    // Геттеры и сеттеры
    public static SnovyMafia getInstance() { return instance; }
    public Set<String> getLeaders() { return leaders; }
    public int getNightDurationSeconds() { return nightDurationSeconds; }
    public int getDayVoteDuration() { return dayVoteDuration; } // ← ДОБАВЛЕНО

    public void setNightDurationSeconds(int seconds) {
        this.nightDurationSeconds = seconds;
        getConfig().set("night-duration", seconds);
        saveConfig();
    }

    public void setDayVoteDuration(int seconds) { // ← ДОБАВЛЕНО
        this.dayVoteDuration = seconds;
        getConfig().set("day-vote-duration", seconds);
        saveConfig();
    }

    public int getStartCountdownDuration() {
        return getConfig().getInt("start-countdown-duration", 60);
    }

    public void setStartCountdownDuration(int seconds) {
        getConfig().set("start-countdown-duration", seconds);
        saveConfig();
    }
    
    public MafiaGame getCurrentGame() { return currentGame; }
    public void setCurrentGame(MafiaGame game) { this.currentGame = game; }
}