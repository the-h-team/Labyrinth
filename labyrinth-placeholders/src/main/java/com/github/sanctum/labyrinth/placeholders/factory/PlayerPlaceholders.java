package com.github.sanctum.labyrinth.placeholders.factory;

import com.github.sanctum.labyrinth.placeholders.Placeholder;
import com.github.sanctum.labyrinth.placeholders.PlaceholderIdentifier;
import com.github.sanctum.labyrinth.placeholders.PlaceholderTranslation;
import com.github.sanctum.labyrinth.placeholders.PlaceholderVariable;
import java.util.Date;
import java.util.Locale;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerPlaceholders implements PlaceholderTranslation {

	private final Placeholder[] placeholders = new Placeholder[]{Placeholder.ANGLE_BRACKETS, Placeholder.CURLEY_BRACKETS, Placeholder.PERCENT};
	private final PlaceholderIdentifier identifier = () -> "player";

	@Override
	public @Nullable String onTranslation(String parameter, PlaceholderVariable variable) {
		if (variable.exists() && variable.isPlayer()) {

			OfflinePlayer p = variable.getAsPlayer();

			if (p.isOnline()) {

				switch (parameter.toLowerCase(Locale.ROOT)) {
					case "name":
						return p.getName();
					case "display_name":
						return p.getPlayer().getDisplayName();
					case "last_played":
						return new Date(p.getLastPlayed()).toLocaleString();

				}

			} else {
				switch (parameter.toLowerCase(Locale.ROOT)) {
					case "name":
					case "display_name":
						return p.getName();
					case "last_played":
						return new Date(p.getLastPlayed()).toLocaleString();

				}
			}

		}
		return null;
	}

	@Override
	public @NotNull Placeholder[] getPlaceholders() {
		return placeholders;
	}

	@Override
	public @Nullable PlaceholderIdentifier getIdentifier() {
		return identifier;
	}
}
