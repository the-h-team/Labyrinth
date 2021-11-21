package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileType;
import com.github.sanctum.labyrinth.data.Node;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Hempfest
 */
public abstract class Cooldown {

	private String format = "&e{DAYS} &rDays &e{HOURS} &rHours &e{MINUTES} &rMinutes &e{SECONDS} &rSeconds";

	/**
	 * Get this cooldown's delimiter-id
	 *
	 * @return the cooldown's custom delimiter
	 */
	public abstract String getId();

	/**
	 * Get the original cooldown period.
	 *
	 * @return the original specified cooldown period
	 */
	public abstract long getCooldown();

	public final long getTimePassed() {
		return (System.currentTimeMillis() - getCooldown()) / 1000;
	}

	/**
	 * Get the raw int value of total cooldown time remaining.
	 *
	 * @return the total amount of time left from the conversion table
	 */
	public final int getTimeLeft() {
		return (int) Math.abs(getTimePassed());
	}

	/**
	 * Get the amount of seconds left from the total cooldown length equated.
	 * <p>
	 * This != total cooldown time converted to days; it's a soft-cap
	 * representative of the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return the amount of days left within the conversion table
	 */
	public final int getDaysLeft() {
		return (int) TimeUnit.SECONDS.toDays(getTimeLeft());
	}

	/**
	 * Get the amount of hours left from the total cooldown length equated.
	 * <p>
	 * This != total cooldown time converted to hours; it's a soft-cap
	 * representative of the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return the amount of hours left within the conversion table
	 */
	public final long getHoursLeft() {
		return TimeUnit.SECONDS.toHours(getTimeLeft()) - (getDaysLeft() * 24);
	}

	/**
	 * Get the amount of minutes left from the total cooldown length equated.
	 * <p>
	 * This != total cooldown time converted to minutes; it's a soft-cap
	 * representative of the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return the amount of minutes left within the conversion table
	 */
	public final long getMinutesLeft() {
		return TimeUnit.SECONDS.toMinutes(getTimeLeft()) - (TimeUnit.SECONDS.toHours(getTimeLeft()) * 60);
	}

	/**
	 * Get the amount of seconds left from the total cooldown length equated.
	 * <p>
	 * This != total cooldown time converted to seconds; it's a soft-cap
	 * representative of the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return the amount of seconds left within the conversion table
	 */
	public final long getSecondsLeft() {
		return TimeUnit.SECONDS.toSeconds(getTimeLeft()) - (TimeUnit.SECONDS.toMinutes(getTimeLeft()) * 60);
	}

	/**
	 * Check if the cooldown from Labyrinth cache is complete.
	 *
	 * @return the result of completion for the cooldown
	 */
	public final boolean isComplete() {
		Long a = getCooldown();
		Long b = System.currentTimeMillis();
		int compareNum = a.compareTo(b);
		return LabyrinthProvider.getInstance().getCooldowns().contains(this) && compareNum <= 0;
	}

	/**
	 * Get a formatted string containing the remaining time for the cooldown.
	 * <p>
	 * It is recommended you override this and implement your own beautiful
	 * time format using the provided time variables such as
	 * {@link #getSecondsLeft()}, {@link #getMinutesLeft()} etc.
	 * <p>
	 * For persistent formats, use the {@link Cooldown#format(String)}
	 * method after using {@link Cooldown#getById(String)}.
	 *
	 * @return the full amount of time left within the cooldown from seconds to days
	 */
	public String fullTimeLeft() {
		return this.format
				.replace("{DAYS}", "" + getDaysLeft())
				.replace("{HOURS}", "" + getHoursLeft())
				.replace("{MINUTES}", "" + getMinutesLeft())
				.replace("{SECONDS}", "" + getSecondsLeft());
	}

	public Cooldown format(String format) {
		this.format = format;
		return this;
	}

	/**
	 * Save the cooldown to Labyrinth cache.
	 * <p>
	 * Note: If a cooldown is already saved with the same id, it will be overwritten.
	 */
	public synchronized final void save() {
		FileList.search(LabyrinthProvider.getInstance().getPluginInstance())
				.find("cooldowns", "Persistent", FileType.JSON)
				.write(t -> t.set("Library." + getId() + ".expiration", getCooldown()));
		Schedule.sync(() -> LabyrinthProvider.getInstance().getCooldowns().remove(this)).applyAfter(() -> LabyrinthProvider.getInstance().getCooldowns().add(this)).run();
	}

	/**
	 * Update the hard storage (persistence) to this live cooldown's remaining time.
	 * <p>
	 * Note: If a cooldown is already saved with the same id, it will be overwritten.
	 * <p>
	 * <em>Primarily to be used on plugin disable.</em>
	 */
	public synchronized final void update() {
		FileList.search(LabyrinthProvider.getInstance().getPluginInstance())
				.find("cooldowns", "Persistent", FileType.JSON)
				.write(t -> t.set("Library." + getId() + ".expiration", abv(getTimeLeft())));
		Schedule.sync(() -> LabyrinthProvider.getInstance().getCooldowns().remove(this)).applyAfter(() -> LabyrinthProvider.getInstance().getCooldowns().add(this)).run();
	}

	/**
	 * Convert seconds into milliseconds for final time conversions.
	 *
	 * @param seconds the amount of time in seconds to convert
	 * @return the milliseconds needed for conversion
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
	 * Get a native cooldown object by its set delimiter-id.
	 *
	 * @param id the custom delimiter to search for
	 * @return a cooldown based object retaining original values from save
	 * @deprecated use {@link com.github.sanctum.labyrinth.api.CooldownService#getCooldown(String)} instead!
	 */
	@Deprecated
	public static Cooldown getById(String id) {
		return LabyrinthProvider.getService(Service.COOLDOWNS).getCooldown(id);
	}

	/**
	 * Remove an object of Cooldown inheritance from Labyrinth cache.
	 *
	 * @param c the cooldown representative to remove from cache
	 */
	public static void remove(Cooldown c) {
		Node home = FileList.search(LabyrinthProvider.getInstance().getPluginInstance())
				.get("cooldowns", "Persistent", FileType.JSON)
				.read(t -> t.getNode("Library." + c.getId()));
		home.delete();
		home.save();
		Schedule.sync(() -> LabyrinthProvider.getInstance().getCooldowns().remove(c)).run();
	}


}
