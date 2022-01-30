package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Removal;
import com.github.sanctum.labyrinth.api.CooldownService;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileType;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Hempfest
 */
public abstract class Cooldown implements ParsedTimeFormat {

	private static final CooldownService SERVICE = LabyrinthProvider.getService(Service.COOLDOWNS);
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
	 * Get the amount of seconds left from the total cooldown length equated.
	 * <p>
	 * This != total cooldown time converted to days; it's a soft-cap
	 * representative of the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return the amount of days left within the conversion table
	 */
	@Override
	public long getDays() {
		return TimeUnit.SECONDS.toDays(getTimePassed());
	}

	/**
	 * Get the amount of hours left from the total cooldown length equated.
	 * <p>
	 * This != total cooldown time converted to hours; it's a soft-cap
	 * representative of the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return the amount of hours left within the conversion table
	 */
	@Override
	public long getHours() {
		return TimeUnit.SECONDS.toHours(getTimePassed()) - (getDays() * 24);
	}

	/**
	 * Get the amount of minutes left from the total cooldown length equated.
	 * <p>
	 * This != total cooldown time converted to minutes; it's a soft-cap
	 * representative of the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return the amount of minutes left within the conversion table
	 */
	@Override
	public long getMinutes() {
		return TimeUnit.SECONDS.toMinutes(getTimePassed()) - (TimeUnit.SECONDS.toHours(getTimePassed()) * 60);
	}

	/**
	 * Get the amount of seconds left from the total cooldown length equated.
	 * <p>
	 * This != total cooldown time converted to seconds; it's a soft-cap
	 * representative of the cooldown's 'SS:MM:HH:DD' format.
	 *
	 * @return the amount of seconds left within the conversion table
	 */
	@Override
	public long getSeconds() {
		return TimeUnit.SECONDS.toSeconds(getTimePassed()) - (TimeUnit.SECONDS.toMinutes(getTimePassed()) * 60);
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
		return SERVICE.getCooldowns().contains(this) && compareNum <= 0;
	}

	/**
	 * Change this cooldown instance's time display format.
	 *
	 * @param format The new full time format to use.
	 * @return The same cooldown instance.
	 */
	public Cooldown format(String format) {
		this.format = format;
		return this;
	}

	/**
	 * Get a formatted string containing the remaining time for the cooldown.
	 * <p>
	 * It is recommended you override this and implement your own beautiful
	 * time format using the provided time variables such as
	 * {@link #getSeconds()}, {@link #getMinutes()} etc.
	 * <p>
	 * For persistent formats, use the {@link Cooldown#format(String)}
	 * method after using {@link com.github.sanctum.labyrinth.api.CooldownService#getCooldown(String)}.
	 *
	 * @return the full amount of time left within the cooldown from seconds to days
	 */
	public String toFormat() {
		return this.format
				.replace("{DAYS}", "" + getDays())
				.replace("{HOURS}", "" + getHours())
				.replace("{MINUTES}", "" + getMinutes())
				.replace("{SECONDS}", "" + getSeconds());
	}

	public synchronized final void remove() {
		SERVICE.remove(this);
	}

	/**
	 * Save the cooldown to Labyrinth cache.
	 * <p>
	 * Note: If a cooldown is already saved with the same id, it will be overwritten.
	 */
	public synchronized final void save() {
		FileList.search(LabyrinthProvider.getInstance().getPluginInstance())
				.get("cooldowns", "Persistent", FileType.JSON)
				.write(t -> t.set("Library." + getId() + ".expiration", getCooldown()));
		TaskScheduler.of(() -> SERVICE.getCooldowns().add(this)).schedule();
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
				.get("cooldowns", "Persistent", FileType.JSON)
				.write(t -> t.set("Library." + getId() + ".expiration", abv(getTimePassed())));
	}

	/**
	 * Convert seconds into milliseconds for final time conversions.
	 *
	 * @param seconds the amount of time in seconds to convert
	 * @return the milliseconds needed for conversion
	 */
	protected final long abv(long seconds) {
		return System.currentTimeMillis() + (seconds * 1000L);
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

	@Override
	public String toString() {
		ZonedDateTime time = new Date().toInstant().atZone(ZoneId.systemDefault());
		String month = time.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		String year = String.valueOf(time.getYear());
		String day = String.valueOf(time.getDayOfMonth() + getDays());
		Date date = getDate();
		ZonedDateTime current = date.toInstant().atZone(ZoneId.systemDefault());
		String clock = current.getHour() + ":" + current.getMinute();
		// format 'Month day, year @ clock'
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return month + " " + day + ", " + year + " @ " + clock + (calendar.get(Calendar.AM_PM) == Calendar.PM ? "pm" : "am");
	}


}
