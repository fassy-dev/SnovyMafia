package dev.fassykite.snovymafia.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class GradientUtil {

    /**
     * Применяет градиент от темно-красного к черному
     */
    public static String applyMafiaGradient(String text) {
        // Темно-красный (#8B0000) → Красный (#FF0000) → Черный (#000000)
        return "<gradient:#8B0000:#FF0000:#000000>" + text + "</gradient>";
    }

    /**
     * Парсит строку с MiniMessage в Component (для отправки в чат)
     */
    public static Component parse(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }
}