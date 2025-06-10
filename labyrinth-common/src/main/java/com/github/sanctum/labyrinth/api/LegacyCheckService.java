package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.panther.util.PantherString;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

/**
 * Detects legacy server environments.
 */
@Deprecated
public interface LegacyCheckService extends Service {

    String VERSION = new WesVersionMatcher().match();

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
     * Check if the server is up to date with server profiles.
     *
     * @return true if the server accepts the new server profiles API
     */
    default boolean isServerProfiles() {
        String num = String.valueOf(VERSION.charAt(2));
        String num2 = String.valueOf(VERSION.charAt(3));
        if (StringUtils.use(num2).isInt()) {
            num += num2;
        }
        int fin = Integer.parseInt(num);
        return fin >= 21;
    }

    /**
     * Check if the server uses java 20 or not.
     *
     * @return true if this server version uses a java virtual machine with major code version 65.
     */
    default boolean isJava20() {
        return new PantherString(VERSION).contains("1_20_R3", "1_20_R4", "1_20_R5", "1_20_R6", "1_21", "1_21_R1");
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
