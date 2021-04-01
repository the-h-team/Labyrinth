package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;

public abstract class Cooldown {

	/**
	 * Get the cooldown object's delimiter-id
	 *
	 * @return The cooldown object's custom delimiter
	 */
	public abstract String getId();

	/**
	 * Get the original cooldown period.
	 *
	 * @return The original specified cooldown period.
	 */
	public abstract long getCooldown();

	public final long getTimePassed() {
		return (System.currentTimeMillis() - getCooldown()) / 1000;
	}

	/**
	 * The raw value for total cooldown time remaining.
	 *
	 * @return Gets the total amount of time left from the conversion table
	 */
	public final int getTimeLeft() {
		return Integer.parseInt(String.valueOf(getTimePassed()).replace("-", ""));
	}

	/**
	 * Get the amount of seconds left from the total cooldown length equated.
	 * This != total cooldown time converted to days its a soft=cap representative of
	 * the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return Get's the amount of days left within the conversion table.
	 */
	public final int getDaysLeft() {
		return (int) TimeUnit.SECONDS.toDays(getTimeLeft());
	}

	/**
	 * Get the amount of hours left from the total cooldown length equated.
	 * This != total cooldown time converted to hours its a soft=cap representative of
	 * the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return Get's the amount of hours left within the conversion table.
	 */
	public final long getHoursLeft() {
		return TimeUnit.SECONDS.toHours(getTimeLeft()) - (getDaysLeft() * 24);
	}

	/**
	 * Get the amount of minutes left from the total cooldown length equated.
	 * This != total cooldown time converted to minutes its a soft=cap representative of
	 * the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return Get's the amount of minutes left within the conversion table.
	 */
	public final long getMinutesLeft() {
		return TimeUnit.SECONDS.toMinutes(getTimeLeft()) - (TimeUnit.SECONDS.toHours(getTimeLeft()) * 60);
	}

	/**
	 * Get the amount of seconds left from the total cooldown length equated.
	 * This != total cooldown time converted to seconds its a soft=cap representative of
	 * the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return Get's the amount of seconds left within the conversion table.
	 */
	public final long getSecondsLeft() {
		return TimeUnit.SECONDS.toSeconds(getTimeLeft()) - (TimeUnit.SECONDS.toMinutes(getTimeLeft()) * 60);
	}

	/**
	 * Check's if the cooldown from Labyrinth cache is complete.
	 *
	 * @return The result of completion for the cooldown.
	 */
	public final boolean isComplete() {
		Long a = getCooldown();
		Long b = System.currentTimeMillis();
		int compareNum = a.compareTo(b);
		return Labyrinth.getInstance().COOLDOWNS.contains(this) && compareNum <= 0;
	}

	/**
	 * Get a formatted string containing the remaining time for the cooldown.
	 * It's reccomended you override this and implement your own beautiful time format using the
	 * provided time variables such as "getSeconds, getMinutes" etc.
	 *
	 * @return Get's the full amount of time left within the cooldown from seconds to days
	 */
	public String fullTimeLeft() {
		return "&e" + getDaysLeft() + " &rDays &e" + getHoursLeft() + " &rHours &e" + getMinutesLeft() + " &rMinutes &e" + getSecondsLeft() + " &rSeconds";
	}

	/**
	 * Save the cooldown to Labyrinth cache.
	 * Note: If a cooldown is already saved with the same Id it will be overwritten
	 */
	public synchronized final void save() {
		FileManager library = FileList.search(Labyrinth.getInstance()).find("Cooldowns", "Persistent");
		library.getConfig().set("Library." + getId() + ".expiration", getCooldown());
		library.saveConfig();
		Labyrinth.getInstance().COOLDOWNS.add(this);
	}

	/**
	 * Update the hard-storage for persistence to this cooldown's remaining time.
	 * Note: If a cooldown is already saved with the same Id it will be overwritten, primarily
	 * to be used on plugin disable.
	 */
	public synchronized final void update() {
		FileManager library = FileList.search(Labyrinth.getInstance()).find("Cooldowns", "Persistent");
		library.getConfig().set("Library." + getId() + ".expiration", abv(getTimeLeft()));
		library.saveConfig();
		Labyrinth.getInstance().COOLDOWNS.add(this);
	}

	/**
	 * Convert's seconds into milliseconds for final time conversions.
	 *
	 * @param seconds The amount of time to convert.
	 * @return The milliseconds needed for conversion.
	 */
	protected final long abv(int seconds) {
		return System.currentTimeMillis() + (seconds * 1000);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Cooldown)) return false;
		Cooldown cool = (Cooldown) o;
		return Objects.equals(getId(), cool.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	/**
	 * Get a native cooldown object by its set delimiter-id
	 *
	 * @param id The custom delimiter to search for
	 * @return A cooldown based object retaining original values from save.
	 */
	public static Cooldown getById(String id) {
		return Labyrinth.getInstance().COOLDOWNS.stream().filter(c -> c.getId().equals(id)).findFirst().orElseGet(() -> {
			FileManager library = FileList.search(Labyrinth.getInstance()).find("Cooldowns", "Persistent");
			if (library.getConfig().getConfigurationSection("Library." + id) != null) {

				long time = library.getConfig().getLong("Library." + id + ".expiration");
				Long a = time;
				Long b = System.currentTimeMillis();
				int compareNum = a.compareTo(b);
				if (!(compareNum <= 0)) {
					Cooldown toMake = new Cooldown() {
						@Override
						public String getId() {
							return id;
						}

						@Override
						public long getCooldown() {
							return time;
						}
					};
					toMake.save();
					return toMake;
				} else {
					library.getConfig().set("Library." + id, null);
					library.saveConfig();
				}
			}
			return null;
		});
	}

	/**
	 * Remove an object of Cooldown inheritance from Labyrinth cache.
	 *
	 * @param c The cooldown representative to remove from cache.
	 */
	public static void remove(Cooldown c) {
		FileManager library = FileList.search(Labyrinth.getInstance()).find("Cooldowns", "Persistent");
		library.getConfig().set("Library." + c.getId(), null);
		library.saveConfig();
		Schedule.sync(() -> Labyrinth.getInstance().COOLDOWNS.remove(c)).run();
	}


}
