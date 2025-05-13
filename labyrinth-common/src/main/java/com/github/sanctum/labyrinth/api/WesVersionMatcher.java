package com.github.sanctum.labyrinth.api;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

/**
 * Matches the server's NMS version to its wrapper
 *
 * @author Wesley Smith
 * @since 1.2.1
 */
public class WesVersionMatcher {
    /**
     * Maps a Minecraft version string to the corresponding revision string
     */
    private static final Map<String, String> VERSION_TO_REVISION = new HashMap<String, String>() {
        {
            this.put("1.20", "1_20_R1");
            this.put("1.20.1", "1_20_R1");
            this.put("1.20.2", "1_20_R2");
            this.put("1.20.3", "1_20_R3");
            this.put("1.20.4", "1_20_R3");
            this.put("1.20.5", "1_20_R4");
            this.put("1.20.6", "1_20_R4");
            this.put("1.21", "1_21_R1");
        }
    };
    /* This needs to be updated to reflect the newest available version wrapper */
    private static final String FALLBACK_REVISION = "1_21_R1";

    /**
     * Matches the server version to it's wrapper
     *
     * @return The wrapper for this server
     * @throws IllegalStateException If the version wrapper failed to be instantiated or is unable to be found
     */
    public String match() {
        String craftBukkitPackage = Bukkit.getServer().getClass().getPackage().getName();

        String rVersion;
        if (!craftBukkitPackage.contains(".v")) { // cb package not relocated (i.e. paper 1.20.5+)
            final String version = Bukkit.getBukkitVersion().split("-")[0];
            rVersion = VERSION_TO_REVISION.getOrDefault(version, FALLBACK_REVISION);
        } else {
            rVersion = craftBukkitPackage.split("\\.")[3].substring(1);
        }
        return rVersion;
    }
}