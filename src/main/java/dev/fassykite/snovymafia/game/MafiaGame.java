package dev.fassykite.snovymafia.game;

import dev.fassykite.snovymafia.SnovyMafia;
import dev.fassykite.snovymafia.gui.GuiUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MafiaGame implements Listener {
    private final SnovyMafia plugin;
    private Phase phase = Phase.WAITING;
    private Map<UUID, Role> roles = new HashMap<>();
    private Map<UUID, Player> targets = new HashMap<>();
    private Map<UUID, Player> votes = new HashMap<>();
    private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();

    private Player lover1 = null;
    private Player lover2 = null;

    private int secondsLeft;
    private boolean gameActive = true;
    private static final String SCOREBOARD_TITLE = "🎭 SnovyMafia";

    private Set<UUID> queuedPlayers = new HashSet<>();
    private boolean acceptingPlayers = true;

    private final Set<UUID> invisiblePlayers = new HashSet<>();

    public MafiaGame(SnovyMafia plugin) {
        this.plugin = plugin;
    }

    public void startGameImmediately(boolean ignoreMinPlayers) {
        acceptingPlayers = false;
        plugin.setCurrentGame(this);
        assignRoles(ignoreMinPlayers);
        if (!gameActive) return;
        startVotingPhase();
    }

    public void startWithCountdown() {
        acceptingPlayers = false;
        plugin.setCurrentGame(this);
        broadcast("Игра начнётся через §c60 §fсекунд!");
        broadcast("Напиши §f/mafia join§f, чтобы записаться!");

        int[] countdown = {60};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown[0] <= 0) {
                    this.cancel();
                    startGameNow();
                    return;
                }

                if (countdown[0] <= 10 || countdown[0] % 10 == 0) {
                    broadcast("Игра начнётся через §c" + countdown[0] + " §fсекунд!");
                }

                countdown[0]--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void startGameNow() {
        assignRoles(false); // старый вызов
        if (!gameActive) return;
        startVotingPhase();
    }

    private void assignRoles(boolean ignoreMinPlayers) {
        List<Player> players = queuedPlayers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .collect(Collectors.toList());

        if (!ignoreMinPlayers && players.size() < 4) {
            broadcast("Недостаточно игроков! (Минимум §c4 §fигрока для начала игры.)");
            endGame();
            return;
        }

        if (ignoreMinPlayers && players.size() < 2) {
            broadcast("Слишком мало игроков для старта! (Минимум §c2 §fигрока.)");
            endGame();
            return;
        }

        roles.clear();
        targets.clear();
        votes.clear();
        List<Role> pool = new ArrayList<>();

        int totalMafia = Math.max(1, players.size() / 4);
        List<Role> mafiaRoles = new ArrayList<>();
        if (plugin.isRoleEnabled(Role.DON)) mafiaRoles.add(Role.DON);
        if (plugin.isRoleEnabled(Role.GANGSTER)) mafiaRoles.add(Role.GANGSTER);
        if (plugin.isRoleEnabled(Role.KAMIKAZE)) mafiaRoles.add(Role.KAMIKAZE);
        int mafiaBase = totalMafia - mafiaRoles.size();
        for (int i = 0; i < mafiaBase && plugin.isRoleEnabled(Role.MAFIA); i++) {
            mafiaRoles.add(Role.MAFIA);
        }
        pool.addAll(mafiaRoles);

        Role[] specialRoles = {
                Role.SHERIFF, Role.DOCTOR, Role.MANIAC, Role.LOVER,
                Role.STRIPPER, Role.SEER, Role.BODYGUARD, Role.JUDGE,
                Role.TERRORIST, Role.SERGEANT, Role.PROSECUTOR, Role.MAYOR,
                Role.WITNESS, Role.BARTENDER, Role.ANGEL
        };

        for (Role r : specialRoles) {
            if (plugin.isRoleEnabled(r)) {
                pool.add(r);
            }
        }

        while (pool.size() < players.size()) {
            pool.add(Role.VILLAGER);
        }

        long villagers = pool.stream().filter(r -> r == Role.VILLAGER).count();
        if (villagers < (pool.size() + 1) / 2) {
            int toReplace = (int) ((pool.size() + 1) / 2 - villagers);
            for (int i = 0; i < pool.size() && toReplace > 0; i++) {
                if (pool.get(i) != Role.VILLAGER && pool.get(i).getAlignment() == RoleAlignment.VILLAGER) {
                    pool.set(i, Role.VILLAGER);
                    toReplace--;
                }
            }
        }

        Collections.shuffle(pool);

        for (int i = 0; i < players.size(); i++) {
            roles.put(players.get(i).getUniqueId(), pool.get(i));
        }

        // Выдача книг
        ItemStack guideBook = GuiUtil.createRolesGuideBook(plugin);
        for (Player p : players) {
            p.getInventory().addItem(guideBook);
            giveRoleBook(p);
        }

        List<String> enabled = new ArrayList<>();
        for (Role role : Role.values()) {
            if (role != Role.VILLAGER && plugin.isRoleEnabled(role)) {
                enabled.add(role.getDisplayName());
            }
        }
        broadcast("Активные роли: " + String.join("§f, ", enabled) + "§f.");
        setMinecraftTime(true);
    }

    private void giveRoleBook(Player p) {
        Role role = roles.get(p.getUniqueId());
        if (role == null) return;
        p.getInventory().addItem(GuiUtil.createRoleBook(role));
    }

    private void startVotingPhase() {
        phase = Phase.VOTING;
        secondsLeft = plugin.getDayVoteDuration();
        votes.clear();
        broadcast("ДЕНЬ. Голосование за подозреваемого (" + secondsLeft + " сек)!");
        broadcast("Напиши §f/vote <ник> §fчтобы проголосовать.");
        setMinecraftTime(true);
        startTimer();
    }

    private void startNightPhase() {
        phase = Phase.NIGHT;
        secondsLeft = plugin.getNightDurationSeconds();
        targets.clear();
        broadcast("Начинается ночь... Ночные роли, действуйте...");
        setMinecraftTime(false);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (plugin.getLeaders().contains(p.getName())) continue;
            if (p.isDead()) continue;
            Role role = roles.get(p.getUniqueId());
            if (role == null || !role.hasNightAction()) continue;

            p.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.INVISIBILITY,
                    secondsLeft * 20 + 20, // на всё время ночи
                    0,
                    false,
                    false
            ));
            invisiblePlayers.add(p.getUniqueId());

            String title = "", subtitle = "";
            if (role == Role.MAFIA || role == Role.DON || role == Role.GANGSTER || role == Role.KAMIKAZE) {
                title = "Твой выход"; subtitle = "Кликни по жертве";
            } else if (role == Role.DOCTOR || role == Role.ANGEL) {
                title = "Твой выход"; subtitle = "Кликни по пациенту";
            } else if (role == Role.SHERIFF || role == Role.PROSECUTOR) {
                title = "Твой выход"; subtitle = "Кликни для проверки";
            } else if (role == Role.STRIPPER || role == Role.BARTENDER) {
                title = "Твой выход"; subtitle = "Кликни по цели";
            } else if (role == Role.MANIAC) {
                title = "Твой выход"; subtitle = "Кликни по жертве";
            } else if (role == Role.SEER || role == Role.WITNESS) {
                title = "Твой выход"; subtitle = "Жди откровения...";
            } else if (role == Role.BODYGUARD) {
                title = "Твой выход"; subtitle = "Кликни для защиты";
            } else if (role == Role.LOVER) {
                title = "Твой выход"; subtitle = "Кликни по возлюбленному <3";
            } else continue;

            p.sendTitle(title, subtitle, 10, 60, 10);
        }

        if (plugin.isRoleEnabled(Role.ANGEL)) {
            List<Player> alive = getAlivePlayers();
            if (!alive.isEmpty()) {
                Player angel = getPlayerByRole(Role.ANGEL);
                if (angel != null && angel.isOnline() && !angel.isDead()) {
                    Player random = alive.get(ThreadLocalRandom.current().nextInt(alive.size()));
                    targets.put(angel.getUniqueId(), random);
                }
            }
        }

        startTimer();
    }

    private void setMinecraftTime(boolean isDay) {
        World world = Bukkit.getWorlds().get(0);
        if (world == null) return;
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setTime(isDay ? 1000 : 13000);
    }

    private void startTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameActive || secondsLeft <= 0) {
                    cancel();
                    if (gameActive) onPhaseEnd();
                    return;
                }
                secondsLeft--;
                updateActionBar();
                updateScoreboardForAll();
                if (phase == Phase.VOTING && secondsLeft % 15 == 0) {
                    broadcastVotes();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void updateScoreboardForAll() {
        String phaseLine = (phase == Phase.VOTING) ? "Голосование" : "Ночь";
        String timeLine = String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!roles.containsKey(p.getUniqueId())) continue;

            Scoreboard board = playerScoreboards.computeIfAbsent(p.getUniqueId(), k -> {
                Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = sb.registerNewObjective("mafia", "dummy", SCOREBOARD_TITLE);
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                return sb;
            });

            Objective obj = board.getObjective("mafia");
            if (obj != null) {
                obj.getScore(" ").setScore(4);
                obj.getScore("Фаза: " + phaseLine).setScore(3);
                obj.getScore("Время: " + timeLine).setScore(2);
                obj.getScore("Статус: Игра").setScore(1);
            }
            p.setScoreboard(board);
        }
    }

    private void onPhaseEnd() {
        if (phase == Phase.VOTING) {
            processVoting();
            checkWinCondition();
            if (gameActive) startNightPhase();
        } else if (phase == Phase.NIGHT) {
            processNightActions();
            checkWinCondition();
            if (gameActive) startVotingPhase();
        }
    }

    private void processVoting() {
        if (votes.isEmpty()) {
            broadcast("Никто не был выбран. Никто не убит.");
            return;
        }

        Map<Player, Integer> voteCount = new HashMap<>();
        for (Player p : votes.values()) {
            voteCount.put(p, voteCount.getOrDefault(p, 0) + 1);
        }

        Player toKill = voteCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (toKill != null) {
            toKill.setHealth(0);
            broadcast("Казнён: " + toKill.getName() + " (" + voteCount.get(toKill) + " голосов)");
        }
    }

    public void castVote(Player voter, Player target) {
        if (phase != Phase.VOTING) return;
        if (voter.isDead() || target.isDead()) return;
        if (!roles.containsKey(voter.getUniqueId()) || !roles.containsKey(target.getUniqueId())) return;
        votes.put(voter.getUniqueId(), target);
    }

    private void broadcastVotes() {
        Map<Player, Integer> voteCount = new HashMap<>();
        for (Player p : votes.values()) {
            voteCount.put(p, voteCount.getOrDefault(p, 0) + 1);
        }

        StringBuilder sb = new StringBuilder("Голоса: ");
        if (voteCount.isEmpty()) {
            sb.append("ещё никто не голосовал");
        } else {
            voteCount.entrySet().stream()
                    .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> {
                        sb.append(entry.getKey().getName())
                                .append("(").append(entry.getValue()).append(") ");
                    });
        }
        Bukkit.broadcastMessage("🎭 §7" + sb.toString());
    }

    private void processNightActions() {
        Set<UUID> blocked = new HashSet<>();
        Player bartender = getPlayerByRole(Role.BARTENDER);
        if (bartender != null && targets.containsKey(bartender.getUniqueId())) {
            for (UUID id : roles.keySet()) {
                if (!id.equals(bartender.getUniqueId())) {
                    blocked.add(id);
                }
            }
        }

        Player stripper = getPlayerByRole(Role.STRIPPER);
        if (stripper != null && targets.containsKey(stripper.getUniqueId())) {
            Player target = targets.get(stripper.getUniqueId());
            blocked.add(target.getUniqueId());
        }

        Set<Player> toKill = new HashSet<>();
        Set<Player> toProtect = new HashSet<>();
        Map<Player, Player> kamikazeMap = new HashMap<>();

        List<Role> killingRoles = Arrays.asList(Role.MAFIA, Role.DON, Role.GANGSTER, Role.MANIAC, Role.KAMIKAZE);
        for (Role role : killingRoles) {
            Player p = getPlayerByRole(role);
            if (p != null && targets.containsKey(p.getUniqueId()) && !blocked.contains(p.getUniqueId())) {
                Player target = targets.get(p.getUniqueId());
                if (target != null && !target.isDead()) {
                    toKill.add(target);
                    if (role == Role.KAMIKAZE) {
                        kamikazeMap.put(p, target);
                    }
                }
            }
        }

        List<Role> protectorRoles = Arrays.asList(Role.DOCTOR, Role.BODYGUARD, Role.ANGEL);
        for (Role role : protectorRoles) {
            Player p = getPlayerByRole(role);
            if (p != null && targets.containsKey(p.getUniqueId()) && !blocked.contains(p.getUniqueId())) {
                Player target = targets.get(p.getUniqueId());
                if (target != null && !target.isDead()) {
                    toProtect.add(target);
                }
            }
        }

        List<Player> actuallyKilled = new ArrayList<>();
        for (Player victim : toKill) {
            if (!toProtect.contains(victim)) {
                victim.setHealth(0);
                actuallyKilled.add(victim);
                broadcast("Убит: " + victim.getName());
            } else {
                broadcast("Спасён: " + victim.getName());
            }
        }

        for (Player killer : kamikazeMap.keySet()) {
            if (actuallyKilled.contains(kamikazeMap.get(killer))) {
                killer.setHealth(0);
                broadcast("Камикадзе унёс с собой: " + killer.getName());
            }
        }

        if (lover1 != null && lover2 != null) {
            if (actuallyKilled.contains(lover1) && !actuallyKilled.contains(lover2)) {
                lover2.setHealth(0);
                broadcast("Любовник умер от горя: " + lover2.getName());
            } else if (actuallyKilled.contains(lover2) && !actuallyKilled.contains(lover1)) {
                lover1.setHealth(0);
                broadcast("Любовник умер от горя: " + lover1.getName());
            }
        }

        for (Player p : actuallyKilled) {
            if (roles.get(p.getUniqueId()) == Role.TERRORIST) {
                List<Player> alive = getAlivePlayers();
                if (!alive.isEmpty()) {
                    Player random = alive.get(ThreadLocalRandom.current().nextInt(alive.size()));
                    random.setHealth(0);
                    broadcast("Террорист унёс с собой: " + random.getName());
                }
            }
        }
    }

    private void checkWinCondition() {
        List<Player> alive = getAlivePlayers();
        if (alive.isEmpty()) {
            broadcast("Ничья!");
            endGame();
            return;
        }

        long mafiaCount = alive.stream()
                .map(p -> roles.get(p.getUniqueId()))
                .filter(r -> r != null && r.getAlignment() == RoleAlignment.MAFIA)
                .count();

        long villagerCount = alive.stream()
                .map(p -> roles.get(p.getUniqueId()))
                .filter(r -> r != null && r.getAlignment() == RoleAlignment.VILLAGER)
                .count();

        long neutralKilling = alive.stream()
                .map(p -> roles.get(p.getUniqueId()))
                .filter(r -> r != null && r.getAlignment() == RoleAlignment.NEUTRAL_KILLING)
                .count();

        if (mafiaCount > 0 && mafiaCount >= villagerCount + neutralKilling) {
            broadcast("Победила мафия!");
            endGame();
        } else if (mafiaCount == 0 && neutralKilling == 0) {
            broadcast("Победили мирные!");
            endGame();
        } else if (neutralKilling > 0 && mafiaCount == 0 && villagerCount <= 1) {
            broadcast("Победил маньяк!");
            endGame();
        }
    }

    private List<Player> getAlivePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> roles.containsKey(p.getUniqueId()) && !p.isDead())
                .collect(Collectors.toList());
    }

    private Player getPlayerByRole(Role role) {
        for (Map.Entry<UUID, Role> entry : roles.entrySet()) {
            if (entry.getValue() == role) {
                Player p = Bukkit.getPlayer(entry.getKey());
                if (p != null && p.isOnline() && !p.isDead()) {
                    return p;
                }
            }
        }
        return null;
    }

    private void updateActionBar() {
        String phaseText = (phase == Phase.VOTING) ? "Голосование" : "Ночь";
        String time = String.format("Осталось: %02d:%02d", secondsLeft / 60, secondsLeft % 60);
        String actionBar = phaseText + " | " + time;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!plugin.getLeaders().contains(p.getName()) && roles.containsKey(p.getUniqueId())) {
                p.spigot().sendMessage(
                        net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBar)[0]
                );
            }
        }
    }

    private void broadcast(String msg) {
        Bukkit.broadcastMessage("🎭 §f" + msg);
    }

    public void endGame() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (playerScoreboards.containsKey(p.getUniqueId())) {
                p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        }
        playerScoreboards.clear();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (invisiblePlayers.contains(p.getUniqueId())) {
                p.removePotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
            }
        }
        invisiblePlayers.clear();

        gameActive = false;
        plugin.setCurrentGame(null);
        roles.clear();
        targets.clear();
        votes.clear();
        queuedPlayers.clear();
        acceptingPlayers = true;
        lover1 = null;
        lover2 = null;

        World world = Bukkit.getWorlds().get(0);
        if (world != null) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player target)) return;
        Player player = e.getPlayer();
        if (plugin.getLeaders().contains(player.getName())) return;
        if (phase != Phase.NIGHT || !gameActive) return;
        if (player.isDead() || target.isDead()) return;

        Role role = roles.get(player.getUniqueId());
        if (role == null) return;

        if (role == Role.LOVER) {
            if (lover1 == null) {
                lover1 = target;
                player.sendMessage("🎭 §dВыбран первый возлюбленный: " + target.getName());
            } else if (lover2 == null && !lover1.equals(target)) {
                lover2 = target;
                player.sendMessage("🎭 §dВыбран второй возлюбленный: " + target.getName());
            }
            return;
        }

        if (role.hasNightAction()) {
            targets.put(player.getUniqueId(), target);
            player.sendMessage("🎭 §aЦель выбрана: " + target.getName());
        }
    }

    // 🔥 НОВЫЕ МЕТОДЫ ДЛЯ ОЧЕРЕДИ
    public boolean addPlayerToQueue(Player player) {
        if (!acceptingPlayers || queuedPlayers.contains(player.getUniqueId())) {
            return false;
        }
        queuedPlayers.add(player.getUniqueId());
        return true;
    }

    public boolean removePlayerFromQueue(Player player) {
        return queuedPlayers.remove(player.getUniqueId());
    }

    public boolean isPlayerInQueue(Player player) {
        return queuedPlayers.contains(player.getUniqueId());
    }

    public int getQueuedPlayerCount() {
        return queuedPlayers.size();
    }

    public Phase getPhase() { return phase; }
    public Map<UUID, Role> getRoles() { return roles; }

    public enum Phase {
        WAITING, VOTING, NIGHT
    }
}