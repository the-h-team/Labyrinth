package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.panther.annotation.Removal;
import org.bukkit.Bukkit;

/**
 * Detects legacy server environments.
 */
@Removal
public interface LegacyCheckService extends Service {

    String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

    default boolean isModded() {
        return StringUtils.use(Bukkit.getServer().getName()).containsIgnoreCase("forge", "magma");
    }

    /**
     * Checks if the server environment is a legacy game version.
     *
     * @return true if the game version is 1.13 or lower
     */
    default boolean isLegacy() {
        String num = String.valueOf(VERSION.charAt(2));
        String num2 = String.valueOf(VERSION.charAt(3));
        if (StringUtils.use(num2).isInt()) {
             num += num2;
        }
        int fin = Integer.parseInt(num);
        return fin >= 8 && fin < 14;
    }

    /**
     * Checks if the server environment is a newer game version.
     * <p>
     * {@code 1.16} introduced hex color support for chat messages.
     *
     * @return true if the server version is 1.16 or higher
     */
    default boolean isNew() {
        String num = String.valueOf(VERSION.charAt(2));
        String num2 = String.valueOf(VERSION.charAt(3));
        if (StringUtils.use(num2).isInt()) {
            num += num2;
        }
        int fin = Integer.parseInt(num);
        return fin >= 16;
    }

    /**
     * Checks if the server environment requires legacy villager support.
     * <p>
     * This version check mirrors {@link LegacyCheckService#isLegacy()} but adds
     * the villager and pillager update on top. Versions in this range require a
     * library-provided serialization to allow proper {@code yaml}
     * location/flat-file conversions.
     *
     * @return true if the server version is 1.14 or lower
     */
    default boolean isLegacyVillager() {
        String num = String.valueOf(VERSION.charAt(2));
        String num2 = String.valueOf(VERSION.charAt(3));
        if (StringUtils.use(num2).isInt()) {
            num += num2;
        }
        int fin = Integer.parseInt(num);
        return fin >= 8 && fin < 15;
    }
}
