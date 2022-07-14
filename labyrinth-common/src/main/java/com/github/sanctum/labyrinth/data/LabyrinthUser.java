package com.github.sanctum.labyrinth.data;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.ActionbarInstance;
import com.github.sanctum.labyrinth.formatting.ScoreboardInstance;
import com.github.sanctum.labyrinth.formatting.TablistInstance;
import com.github.sanctum.labyrinth.interfacing.Nameable;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.panther.util.ParsedTimeFormat;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LabyrinthUser extends Nameable {

	@NotNull String getName();

	String[] getPreviousNames();

	@NotNull UUID getId();

	@NotNull OfflinePlayer getPlayer();

	default @Nullable("User must be online!") TablistInstance getTablist() {
		return isOnline() ? TablistInstance.get(getPlayer().getPlayer()) : null;
	}

	default @Nullable("User must be online!") ScoreboardInstance getScoreboard() {
		return isOnline() ? ScoreboardInstance.get(getPlayer().getPlayer()) : null;
	}

	default @Nullable("User must be online!") ActionbarInstance getActionbar() {
		return isOnline() ? ActionbarInstance.of(getPlayer().getPlayer()) : null;
	}

	default @Nullable Cooldown getCooldown(@NotNull String key) {
		return LabyrinthProvider.getInstance().getCooldown(getId().toString() + "-" + key);
	}

	default @NotNull Cooldown getOrCreate(@NotNull String key, @NotNull ParsedTimeFormat format) {
		return Optional.ofNullable(getCooldown(key)).orElseGet(() -> {
			Cooldown c = newCooldown(key, format);
			c.save();
			return c;
		});
	}

	default @NotNull Cooldown newCooldown(@NotNull String key, @NotNull ParsedTimeFormat format) {
		return new Cooldown() {

			long cooldown;

			{
				abv(format.toSeconds());
			}

			@Override
			public String getId() {
				return key;
			}

			@Override
			public long getCooldown() {
				return cooldown;
			}
		};
	}

	default boolean isOnline() {
		return getPlayer().getPlayer() != null;
	}

}
