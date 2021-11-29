package com.github.sanctum.labyrinth.placeholders.factory;

import com.github.sanctum.labyrinth.placeholders.PlaceholderIdentifier;
import com.github.sanctum.labyrinth.placeholders.PlaceholderTranslation;
import com.github.sanctum.labyrinth.placeholders.PlaceholderVariable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class PlayerPlaceholders implements PlaceholderTranslation {

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
					case "health":
						return NumberFormat.getNumberInstance().format(p.getPlayer().getHealth());
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
	public @Nullable PlaceholderIdentifier getIdentifier() {
		return identifier;
	}
}
