package com.github.sanctum.labyrinth.placeholders.factory;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.service.PlayerSearch;
import com.github.sanctum.labyrinth.formatting.string.ProgressBar;
import com.github.sanctum.labyrinth.placeholders.PlaceholderIdentifier;
import com.github.sanctum.labyrinth.placeholders.PlaceholderTranslation;
import com.github.sanctum.labyrinth.placeholders.PlaceholderTranslationInformation;
import com.github.sanctum.labyrinth.placeholders.PlaceholderVariable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerPlaceholders implements PlaceholderTranslation {

	private final PlaceholderIdentifier identifier = () -> "player";
	private final PlaceholderTranslationInformation information = new PlaceholderTranslationInformation() {
		@Override
		public @NotNull String getName() {
			return "Labyrinth-Player";
		}

		@Override
		public @NotNull String[] getAuthors() {
			return new String[]{"Hempfest"};
		}

		@Override
		public @NotNull String getVersion() {
			return "1.0";
		}
	};

	@Override
	public PlaceholderTranslationInformation getInformation() {
		return information;
	}

	int getPing(Player p) {
		final String v = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		if (!p.getClass().getName().equals("org.bukkit.craftbukkit." + v + ".entity.CraftPlayer")) { // compatibility check
			p = Bukkit.getPlayer(p.getUniqueId()); // cast to bukkit provision.
		}
		try {
			// if its 1.17+ invoke the natural get ping method.
			if (LabyrinthProvider.getInstance().isNew() && !v.contains("16")) {
				return (int) p.getClass().getDeclaredMethod("getPing").invoke(p);
			} else {
				// otherwise handle the ping through nms.
				Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
				return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
			}
		} catch (Exception e) {
			LabyrinthProvider.getInstance().getLogger().severe("A severe issue occurred while pinging a players response time.");
		}
		return 0;
	}

	String getPingColor(int num) {
		if (num <= 100) {
			return "&a";
		}
		if (num <= 350) {
			return "&e";
		}
		if (num <= 600) {
			return "&c";
		}
		return "&4";
	}

	String getPingColorPronounced(int num) {
		String color = getPingColor(num);
		if (num >= 600) {
			return color + "LAG!";
		}
		return color + num;
	}

	@Override
	public @Nullable String onTranslation(String parameter, PlaceholderVariable variable) {
		if (variable.exists() && variable.isPlayer()) {

			OfflinePlayer p = variable.getAsPlayer();

			if (p.isOnline()) {
				switch (parameter.toLowerCase(Locale.ROOT)) {
					case "ping":
						int ping = getPing(p.getPlayer());
						return  getPingColor(ping) + ping;
					case "ping_pronounced":
						return getPingColorPronounced(getPing(p.getPlayer()));
					case "name":
						return p.getName();
					case "world_player_count":
						return Optional.ofNullable(p.getPlayer().getWorld()).map(World::getPlayers).map(List::size).map(Object::toString).orElse(null);
					case "world_name":
						return Optional.ofNullable(p.getPlayer().getWorld()).map(World::getName).orElse(null);
					case "display_name":
						return p.getPlayer().getDisplayName();
					case "health_bar":
						return new ProgressBar().setProgress(((Number)p.getPlayer().getHealth()).intValue()).setGoal(20).toString();
					case "time_played":
						return PlayerSearch.of(p.getName()).getPlaytime().toString();
					case "hours_played":
						return PlayerSearch.of(p.getName()).getPlaytime().getHours() + "";
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
					case "hours_played":
						return PlayerSearch.of(p.getName()).getPlaytime().getHours() + "";
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
