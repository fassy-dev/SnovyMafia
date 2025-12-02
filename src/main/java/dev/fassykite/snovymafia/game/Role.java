package dev.fassykite.snovymafia.game;

public enum Role {
    VILLAGER("§fМирный", false, "villager", RoleAlignment.VILLAGER, "Ничего не делает ночью."),
    MAFIA("§cМафия", true, "mafia", RoleAlignment.MAFIA, "Убивает одного игрока ночью."),
    DON("§4Дон мафии", true, "don", RoleAlignment.MAFIA, "Видит мафию. Может блокировать шерифа."),
    SHERIFF("§9Шериф", true, "sheriff", RoleAlignment.VILLAGER, "Проверяет игрока: мафия или нет."),
    DOCTOR("§aДоктор", true, "doctor", RoleAlignment.VILLAGER, "Лечит одного игрока ночью."),
    MANIAC("§5Маньяк", true, "maniac", RoleAlignment.NEUTRAL_KILLING, "Убивает одного игрока (не мафия)."),
    LOVER("§dЛюбовница", true, "lover", RoleAlignment.NEUTRAL, "Связывает двух игроков. Если один умрёт — умрёт и второй."),
    STRIPPER("§dСтриптизёрша", true, "stripper", RoleAlignment.VILLAGER, "Отключает способность цели на ночь."),
    SEER("§bПровидец", true, "seer", RoleAlignment.VILLAGER, "Видит, кто умрёт этой ночью."),
    BODYGUARD("§6Телохранитель", true, "bodyguard", RoleAlignment.VILLAGER, "Защищает игрока от убийства (1 раз)."),
    GANGSTER("§8Гангстер", true, "gangster", RoleAlignment.MAFIA, "Может завербовать мирного в мафию."),
    JUDGE("§7Судья", false, "judge", RoleAlignment.VILLAGER, "Может отменить голосование днём (1 раз)."),
    TERRORIST("§4Террорист", false, "terrorist", RoleAlignment.NEUTRAL_KILLING, "При смерти убивает случайного игрока."),
    SERGEANT("§6Сержант", false, "sergeant", RoleAlignment.VILLAGER, "Может убить одного игрока днём (1 раз)."),
    PROSECUTOR("§9Прокурор", true, "prosecutor", RoleAlignment.VILLAGER, "Проверяет точную роль игрока."),
    MAYOR("§eМэр", false, "mayor", RoleAlignment.VILLAGER, "Имеет 2 голоса на голосовании."),
    WITNESS("§fСвидетель", false, "witness", RoleAlignment.VILLAGER, "После первой смерти видит убийцу."),
    KAMIKAZE("§cКамикадзе", false, "kamikaze", RoleAlignment.MAFIA, "При смерти убивает своего убийцу."),
    BARTENDER("§6Бармен", true, "bartender", RoleAlignment.VILLAGER, "Отключает способности всех ночью (кроме себя)."),
    ANGEL("§fАнгел", true, "angel", RoleAlignment.VILLAGER, "Автоматически защищает случайного игрока.");

    private final String displayName;
    private final boolean hasNightAction;
    private final String configKey;
    private final RoleAlignment alignment;
    private final String description;

    Role(String displayName, boolean hasNightAction, String configKey, RoleAlignment alignment, String description) {
        this.displayName = displayName;
        this.hasNightAction = hasNightAction;
        this.configKey = configKey;
        this.alignment = alignment;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public boolean hasNightAction() { return hasNightAction; }
    public String getConfigKey() { return configKey; }
    public RoleAlignment getAlignment() { return alignment; }
    public String getDescription() { return description; }
}