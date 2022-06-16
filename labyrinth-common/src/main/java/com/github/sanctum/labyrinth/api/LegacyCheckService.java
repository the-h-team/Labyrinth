package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.StringUtils;
import org.bukkit.Bukkit;

/**
 * Detects legacy server environments.
 */
public interface LegacyCheckService extends Service {

    String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

    default boolean isModded() {
        return StringUtils.use(Bukkit.getServer().getName()).containsIgnoreCase("forge", "magma");
    }

    /**
     * Check if the environment of the server is legacy.
     *
     * @return true if the server version is 1.13 or lower
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
     * Check if the environment of the server is newer.
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
     * This version check mirrors {@link LegacyCheckService#isLegacy()} but adds the villager and pillager
     * update on top, versions in this range require a labyrinth provided {@link org.bukkit.configuration.serialization.ConfigurationSerializable} implementation to allow
     * proper yaml location/flat-file conversions.
     *
     * @return true if the server version is 1.14 or lower.
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
