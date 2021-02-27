package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.Config;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class Cooldown implements Serializable {

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

	protected long getTimePassed() {
		return (System.currentTimeMillis() - getCooldown()) / 1000;
	}

	/**
	 * The raw value for total cooldown time remaining.
	 *
	 * @return Gets the total amount of time left from the conversion table
	 */
	protected int getTimeLeft() {
		return Integer.parseInt(String.valueOf(getTimePassed()).replace("-", ""));
	}
	/**
	 * Get the amount of seconds left from the total cooldown length equated.
	 * This != total cooldown time converted to days its a soft=cap representative of
	 * the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return Get's the amount of days left within the conversion table.
	 */
	public int getDaysLeft() {
		return (int) TimeUnit.SECONDS.toDays(getTimeLeft());
	}
	/**
	 * Get the amount of hours left from the total cooldown length equated.
	 * This != total cooldown time converted to hours its a soft=cap representative of
	 * the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return Get's the amount of hours left within the conversion table.
	 */
	public long getHoursLeft() {
		return TimeUnit.SECONDS.toHours(getTimeLeft()) - (getDaysLeft() * 24);
	}
	/**
	 * Get the amount of minutes left from the total cooldown length equated.
	 * This != total cooldown time converted to minutes its a soft=cap representative of
	 * the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return Get's the amount of minutes left within the conversion table.
	 */
	public long getMinutesLeft() {
		return TimeUnit.SECONDS.toMinutes(getTimeLeft()) - (TimeUnit.SECONDS.toHours(getTimeLeft()) * 60);
	}

	/**
	 * Get the amount of seconds left from the total cooldown length equated.
	 * This != total cooldown time converted to seconds its a soft=cap representative of
	 * the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return Get's the amount of seconds left within the conversion table.
	 */
	public long getSecondsLeft() {
		return TimeUnit.SECONDS.toSeconds(getTimeLeft()) - (TimeUnit.SECONDS.toMinutes(getTimeLeft()) * 60);
	}

	/**
	 * Check's if the cooldown from Labyrinth cache is complete.
	 *
	 * @return The result of completion for the cooldown.
	 */
	public boolean isComplete() {
		Config cooldowns = Config.get("Storage", "Cooldowns");
		Long a = cooldowns.getConfig().getLong(getId() + ".expiration");
		Long b = System.currentTimeMillis();
		int compareNum = a.compareTo(b);
		if (!cooldowns.getConfig().isLong(getId() + ".expiration")) {
			return true;
		}
		return compareNum <= 0;
	}

	/**
	 * Get a formatted string containing the remaining time for the cooldown.
	 * It's reccomended you override this and implement your own beautiful time format using the
	 * provided time variables such as "getSeconds, getMinutes" etc.
	 *
	 * @return Get's the full amount of time left within the cooldown from seconds to days
	 */
	public String fullTimeLeft() {
		return "(S)" + getSecondsLeft() + " : (M)" + getMinutesLeft() + " : (H)" + getHoursLeft() + " : (D)" + getDaysLeft();
	}

	/**
	 * Save the cooldown to Labyrinth cache.
	 * Note: If a cooldown is already saved with the same Id it will be overwritten
	 */
	public void save() {
		Config cooldowns = Config.get("Storage", "Cooldowns");
		cooldowns.getConfig().set(getId() + ".expiration", getCooldown());
		try {
			cooldowns.getConfig().set(getId() + ".instance", new HFEncoded(this).serialize());
		} catch (IOException e) {
			e.printStackTrace();
		}
		cooldowns.saveConfig();
	}

	/**
	 * Convert's seconds into milliseconds for final time conversions.
	 *
	 * @param seconds The amount of time to convert.
	 * @return The milliseconds needed for conversion.
	 */
	protected long abv(int seconds) {
		return System.currentTimeMillis() + (seconds * 1000);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Cooldown)) return false;
		Cooldown cooldown = (Cooldown) o;
		return Objects.equals(getId(), cooldown.getId()) && Objects.equals(getCooldown(), cooldown.getCooldown());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getCooldown());
	}

	/**
	 * Get a native cooldown object by its set delimiter-id
	 *
	 * @param id The custom delimiter to search for
	 * @return A cooldown based object retaining original values from save.
	 */
	public static Cooldown getById(String id) {
		Config cooldowns = Config.get("Storage", "Cooldowns");
		Cooldown result = null;
		try {
			result = (Cooldown) new HFEncoded(cooldowns.getConfig().getString(id + ".instance")).deserialized();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Remove an object of Cooldown inheritance from Labyrinth cache.
	 *
	 * @param c The cooldown representative to remove from cache.
	 */
	public static void remove(Cooldown c) {
		Config cooldowns = Config.get("Storage", "Cooldowns");
		cooldowns.getConfig().set(c.getId(), null);
		cooldowns.saveConfig();
	}


}
