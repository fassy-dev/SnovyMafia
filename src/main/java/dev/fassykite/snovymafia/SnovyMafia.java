package dev.fassykite.snovymafia;

import dev.fassykite.snovymafia.commands.MafiaCommand;
import dev.fassykite.snovymafia.commands.MafiaTabCompleter;
import dev.fassykite.snovymafia.game.MafiaGame;
import dev.fassykite.snovymafia.game.Role;
import dev.fassykite.snovymafia.gui.*;
import dev.fassykite.snovymafia.listeners.NightListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class SnovyMafia extends JavaPlugin {
    private static SnovyMafia instance;
    private Set<String> leaders = new HashSet<>();
    private int nightDurationSeconds = 120;
    private int dayVoteDuration = 60;
    private int startCountdownDuration = 60;
    private int minPlayersToStart = 4;
    private boolean allowStartWithoutMinPlayers = false;
    private boolean silentDoorsEnabled = true;
    private boolean syncMinecraftTime = true;
    private boolean scoreboardEnabled = true;
    private boolean guideBookEnabled = true;
    private boolean roleBookEnabled = true;
    private boolean allowJoinDuringGame = false;
    private boolean allowLeaveDuringGame = false;
    private String chatPrefix = "🎭";
    private String messageJoinSuccess = "Ты успешно записался в игру!";
    private String messageLeaveSuccess = "Ты вышел из очереди.";
    private String messageGameStarted = "Игра запущена СРАЗУ!";
    private String messageGameStartedWithCountdown = "Игра начнётся через %s секунд!";
    private String messageNotEnoughPlayers = "Недостаточно игроков! (Минимум %s игрока для начала игры.)";
    private String messageWinMafia = "Победила мафия!";
    private String messageWinVillagers = "Победили мирные!";
    private String messageWinManiac = "Победил маньяк!";
    private String messageWinTie = "Ничья!";
    private String messageGameEnded = "Игра окончена.";

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
        getServer().getPluginManager().registerEvents(new StartCountdownChoiceGui(), this);
        getServer().getPluginManager().registerEvents(new MessageSettingsGui(), this);
        getServer().getPluginManager().registerEvents(new OtherSettingsGui(), this);
        getServer().getPluginManager().registerEvents(new MinPlayersChoiceGui(), this);
    }

    public void reloadConfigData() {
        reloadConfig();
        loadConfig();
    }

    private void loadConfig() {
        nightDurationSeconds = getConfig().getInt("night-duration", 120);
        dayVoteDuration = getConfig().getInt("day-vote-duration", 60);
        startCountdownDuration = getConfig().getInt("start-countdown-duration", 60);
        minPlayersToStart = getConfig().getInt("min-players-to-start", 4);
        allowStartWithoutMinPlayers = getConfig().getBoolean("allow-start-without-min-players", false);
        silentDoorsEnabled = getConfig().getBoolean("silent-actions.enable-silent-doors", true);
        syncMinecraftTime = getConfig().getBoolean("sync-minecraft-time", true);
        scoreboardEnabled = getConfig().getBoolean("scoreboard-enabled", true);
        guideBookEnabled = getConfig().getBoolean("guide-book-enabled", true);
        roleBookEnabled = getConfig().getBoolean("role-book-enabled", true);
        allowJoinDuringGame = getConfig().getBoolean("allow-join-during-game", false);
        allowLeaveDuringGame = getConfig().getBoolean("allow-leave-during-game", false);
        chatPrefix = getConfig().getString("chat-prefix", "🎭");
        messageJoinSuccess = getConfig().getString("message-join-success", "Ты успешно записался в игру!");
        messageLeaveSuccess = getConfig().getString("message-leave-success", "Ты вышел из очереди.");
        messageGameStarted = getConfig().getString("message-game-started", "Игра запущена СРАЗУ!");
        messageGameStartedWithCountdown = getConfig().getString("message-game-started-with-countdown", "Игра начнётся через %s секунд!");
        messageNotEnoughPlayers = getConfig().getString("message-not-enough-players", "Недостаточно игроков! (Минимум %s игрока для начала игры.)");
        messageWinMafia = getConfig().getString("message-win-mafia", "Победила мафия!");
        messageWinVillagers = getConfig().getString("message-win-villagers", "Победили мирные!");
        messageWinManiac = getConfig().getString("message-win-maniac", "Победил маньяк!");
        messageWinTie = getConfig().getString("message-win-tie", "Ничья!");
        messageGameEnded = getConfig().getString("message-game-ended", "Игра окончена.");

        leaders.clear();
        getConfig().getStringList("leaders").forEach(leaders::add);
    }

    // 🎮 Основные настройки
    public int getNightDurationSeconds() { return nightDurationSeconds; }
    public void setNightDurationSeconds(int seconds) {
        this.nightDurationSeconds = seconds;
        getConfig().set("night-duration", seconds);
        saveConfig();
    }

    public int getDayVoteDuration() { return dayVoteDuration; }
    public void setDayVoteDuration(int seconds) {
        this.dayVoteDuration = seconds;
        getConfig().set("day-vote-duration", seconds);
        saveConfig();
    }

    public int getStartCountdownDuration() { return startCountdownDuration; }
    public void setStartCountdownDuration(int seconds) {
        this.startCountdownDuration = seconds;
        getConfig().set("start-countdown-duration", seconds);
        saveConfig();
    }

    public int getMinPlayersToStart() { return minPlayersToStart; }
    public void setMinPlayersToStart(int count) {
        this.minPlayersToStart = count;
        getConfig().set("min-players-to-start", count);
        saveConfig();
    }

    public boolean isAllowStartWithoutMinPlayers() { return allowStartWithoutMinPlayers; }
    public void setAllowStartWithoutMinPlayers(boolean enabled) {
        this.allowStartWithoutMinPlayers = enabled;
        getConfig().set("allow-start-without-min-players", enabled);
        saveConfig();
    }

    // 🎭 Роли
    public boolean isRoleEnabled(Role role) {
        return getConfig().getBoolean("enabled-roles." + role.getConfigKey(), true);
    }

    public void setRoleEnabled(Role role, boolean enabled) {
        getConfig().set("enabled-roles." + role.getConfigKey(), enabled);
        saveConfig();
    }

    // 🔇 Тихие действия
    public boolean isSilentDoorsEnabled() { return silentDoorsEnabled; }
    public void setSilentDoorsEnabled(boolean enabled) {
        this.silentDoorsEnabled = enabled;
        getConfig().set("silent-actions.enable-silent-doors", enabled);
        saveConfig();
    }

    // 🌙 Синхронизация времени
    public boolean isSyncMinecraftTime() { return syncMinecraftTime; }
    public void setSyncMinecraftTime(boolean enabled) {
        this.syncMinecraftTime = enabled;
        getConfig().set("sync-minecraft-time", enabled);
        saveConfig();
    }

    // 📊 Scoreboard
    public boolean isScoreboardEnabled() { return scoreboardEnabled; }
    public void setScoreboardEnabled(boolean enabled) {
        this.scoreboardEnabled = enabled;
        getConfig().set("scoreboard-enabled", enabled);
        saveConfig();
    }

    // 📜 Книги
    public boolean isGuideBookEnabled() { return guideBookEnabled; }
    public boolean isRoleBookEnabled() { return roleBookEnabled; }

    // 🧩 Прочее
    public boolean isAllowJoinDuringGame() { return allowJoinDuringGame; }
    public void setAllowJoinDuringGame(boolean enabled) {
        this.allowJoinDuringGame = enabled;
        getConfig().set("allow-join-during-game", enabled);
        saveConfig();
    }

    public boolean isAllowLeaveDuringGame() { return allowLeaveDuringGame; }
    public void setAllowLeaveDuringGame(boolean enabled) {
        this.allowLeaveDuringGame = enabled;
        getConfig().set("allow-leave-during-game", enabled);
        saveConfig();
    }

    // 💬 Префиксы и сообщения
    public String getChatPrefix() { return chatPrefix; }
    public void setChatPrefix(String prefix) {
        this.chatPrefix = prefix;
        getConfig().set("chat-prefix", prefix);
        saveConfig();
    }

    public String getMessageJoinSuccess() { return messageJoinSuccess; }
    public void setMessageJoinSuccess(String msg) {
        this.messageJoinSuccess = msg;
        getConfig().set("message-join-success", msg);
        saveConfig();
    }

    public String getMessageLeaveSuccess() { return messageLeaveSuccess; }
    public void setMessageLeaveSuccess(String msg) {
        this.messageLeaveSuccess = msg;
        getConfig().set("message-leave-success", msg);
        saveConfig();
    }

    public String getMessageGameStarted() { return messageGameStarted; }
    public void setMessageGameStarted(String msg) {
        this.messageGameStarted = msg;
        getConfig().set("message-game-started", msg);
        saveConfig();
    }

    public String getMessageGameStartedWithCountdown() { return messageGameStartedWithCountdown; }
    public void setMessageGameStartedWithCountdown(String msg) {
        this.messageGameStartedWithCountdown = msg;
        getConfig().set("message-game-started-with-countdown", msg);
        saveConfig();
    }

    public String getMessageNotEnoughPlayers() { return messageNotEnoughPlayers; }
    public void setMessageNotEnoughPlayers(String msg) {
        this.messageNotEnoughPlayers = msg;
        getConfig().set("message-not-enough-players", msg);
        saveConfig();
    }

    public String getMessageWinMafia() { return messageWinMafia; }
    public void setMessageWinMafia(String msg) {
        this.messageWinMafia = msg;
        getConfig().set("message-win-mafia", msg);
        saveConfig();
    }

    public String getMessageWinVillagers() { return messageWinVillagers; }
    public void setMessageWinVillagers(String msg) {
        this.messageWinVillagers = msg;
        getConfig().set("message-win-villagers", msg);
        saveConfig();
    }

    public String getMessageWinManiac() { return messageWinManiac; }
    public void setMessageWinManiac(String msg) {
        this.messageWinManiac = msg;
        getConfig().set("message-win-maniac", msg);
        saveConfig();
    }

    public String getMessageWinTie() { return messageWinTie; }
    public void setMessageWinTie(String msg) {
        this.messageWinTie = msg;
        getConfig().set("message-win-tie", msg);
        saveConfig();
    }

    public String getMessageGameEnded() { return messageGameEnded; }
    public void setMessageGameEnded(String msg) {
        this.messageGameEnded = msg;
        getConfig().set("message-game-ended", msg);
        saveConfig();
    }

    public void saveLeaders() {
        getConfig().set("leaders", leaders.stream().toList());
        saveConfig();
    }

    // Геттеры
    public static SnovyMafia getInstance() { return instance; }
    public Set<String> getLeaders() { return leaders; }
    public MafiaGame getCurrentGame() { return currentGame; }
    public void setCurrentGame(MafiaGame game) { this.currentGame = game; }
}