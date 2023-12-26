package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.data.ServiceType;

/**
 * Marks platform-specialized services for Bukkit.
 *
 * @since 1.9.4
 * @see Service
 * @author ms5984
 */
public interface BukkitService {
    ServiceType<BukkitPlatformKeyService> KEYS = new ServiceType<>();
}
