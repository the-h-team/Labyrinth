package com.github.sanctum.labyrinth.api;

/**
 * Detects legacy server environments.
 */
public interface LegacyCheckService extends Service {

    /**
     * Checks if the server environment is modded.
     * <p>
     * This generally means that the server is running forge or magma.
     *
     * @return true if the server is modded
     */
    boolean isModded();

    /**
     * Checks if the server environment is a legacy game version.
     *
     * @return true if the game version is 1.13 or lower
     */
    boolean isLegacy();

    /**
     * Checks if the server environment is a newer game version.
     * <p>
     * {@code 1.16} introduced hex color support for chat messages.
     *
     * @return true if the server version is 1.16 or higher
     */
    boolean isNew();

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
    boolean isLegacyVillager();
}
