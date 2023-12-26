package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

/**
 * @implNote Written prior to 1.9.4 but relocated at that version.
 * @since 1.9.4
 * @author Hempfest
 * @author ms5984
 */
@ApiStatus.Internal
public final class BukkitLegacyCheckService implements LegacyCheckService {
    final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
    boolean modded, legacy, isNew, legacyVillager;

    {
        modded = StringUtils.use(Bukkit.getServer().getName()).containsIgnoreCase("forge", "magma");

        String num = String.valueOf(version.charAt(2));
        String num2 = String.valueOf(version.charAt(3));
        if (StringUtils.use(num2).isInt()) {
            num += num2;
        }
        int fin = Integer.parseInt(num);
        legacy = fin >= 8 && fin < 14;
        isNew = fin >= 16;
        legacyVillager = fin >= 8 && fin < 15;
    }

    /**
     * {@inheritDoc}
     * @return true if the server is modded
     * @implNote Generally detects forge and magma servers.
     */
    @Override
    public boolean isModded() {
        return modded;
    }

    @Override
    public boolean isLegacy() {
        return legacy;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Checks if the server environment requires legacy villager support.
     * <p>
     * This version check mirrors {@link LegacyCheckService#isLegacy()} but
     * adds the villager and pillager update on top. Versions in this range
     * require a library-provided
     * {@link org.bukkit.configuration.serialization.ConfigurationSerializable}
     * implementation to allow proper yaml location/flat-file conversions.
     *
     * @return true if the server version is 1.14 or lower
     */
    @Override
    public boolean isLegacyVillager() {
        return legacyVillager;
    }
}
