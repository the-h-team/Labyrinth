package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.common.StringUtils;
import com.github.sanctum.labyrinth.formatting.string.BlockChar;
import com.github.sanctum.labyrinth.formatting.string.BufferedHeadSkin;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.file.JsonAdapter;
import com.github.sanctum.panther.util.SpecialID;
import com.google.gson.JsonArray;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

class LabyrinthPlayerImpl extends PlayerInfo {

    final OfflinePlayer parent;
    BufferedHeadSkin image;
    final String name;
    String[] names;
    final UUID reference;
    final boolean isOnlineMode;

    LabyrinthPlayerImpl(OfflinePlayer player) {
        String name = player.getName();
        if (name == null) {
            LabyrinthProvider.getInstance().getLogger().severe("- Attempted and failed to register corrupt player data (" + player + "), this is not the fault of labyrinth.");
            throw new IllegalStateException("Bad player object provided.");
        }
        this.parent = player;
        this.isOnlineMode = Bukkit.getOnlineMode();
        this.name = name;
        this.reference = player.getUniqueId();
        if (StringUtils.use(name).isAlphanumeric()) {
            TaskScheduler.of(() -> {
                try {
                    this.image = new BufferedHeadSkin("https://minotar.net/avatar/" + name + ".png", 8, BlockChar.SOLID) {
                    };
                } catch (Exception ignored) {
                }
            }).scheduleAsync().next(() -> {
                URL url;
                BufferedReader in = null;
                StringBuilder sb = new StringBuilder();
                try {
                    url = new URL("https://api.mojang.com/user/profiles/" + reference.toString() + "/names");
                    in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
                    String str;
                    while ((str = in.readLine()) != null) {
                        sb.append(str);
                    }
                } catch (Exception ignored) {
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
                if (sb.length() > 0) {
                    JsonArray array = JsonAdapter.getJsonBuilder().create().fromJson(sb.toString(), JsonArray.class);
                    PantherCollection<String> names = new PantherList<>();
                    array.forEach(element -> {
                        if (element.isJsonObject()) {
                            names.add(element.getAsJsonObject().get("name").getAsString());
                        }
                    });
                    this.names = names.stream().toArray(String[]::new);
                }
            }).scheduleAsync();
        } else this.names = new String[0];
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public String[] getPreviousNames() {
        return names;
    }

    @Override
    public @NotNull OfflinePlayer getPlayer() {
        return isOnlineMode ? parent : Bukkit.getOfflinePlayer(getName());
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return isOnlineMode ? reference : getPlayer().getUniqueId();
    }

    @Override
    @Note("Length of 12 (HUID)")
    public @NotNull SpecialID getSpecialId() {
        return SpecialID.builder().setLength(12).build(name);
    }

    @Override
    public BufferedHeadSkin getHeadImage() {
        if (image == null) return null;
        return new BufferedHeadSkin(image) {
        };
    }
}
