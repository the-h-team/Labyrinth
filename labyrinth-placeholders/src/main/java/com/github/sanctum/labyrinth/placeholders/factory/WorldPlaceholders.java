package com.github.sanctum.labyrinth.placeholders.factory;

import com.github.sanctum.labyrinth.placeholders.PlaceholderIdentifier;
import com.github.sanctum.labyrinth.placeholders.PlaceholderTranslation;
import com.github.sanctum.labyrinth.placeholders.PlaceholderVariable;
import java.util.Locale;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class WorldPlaceholders implements PlaceholderTranslation {

	private final PlaceholderIdentifier identifier = () -> "world";

	@Override
	public @Nullable String onTranslation(String parameter, PlaceholderVariable variable) {
		if (variable.exists() && variable.get() instanceof World) {
			World w = (World) variable.get();
			switch (parameter.toLowerCase(Locale.ROOT)) {
				case "name":
					return w.getName();
				case "player_count":
					return w.getPlayers().size() + "";
			}

		}
		return null;
	}

	@Override
	public @Nullable PlaceholderIdentifier getIdentifier() {
		return identifier;
	}
}
