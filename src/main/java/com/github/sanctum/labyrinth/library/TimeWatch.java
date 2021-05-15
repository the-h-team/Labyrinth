package com.github.sanctum.labyrinth.library;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Used for accurate time span inquisitions
 */
public class TimeWatch {

	private final Date time;

	/**
	 * Use a specified starting date in milliseconds.
	 *
	 * @param milli The starting time interval in milliseconds.
	 * @return A new time watch object.
	 */
	public static TimeWatch start(long milli) {
		return new TimeWatch(milli);
	}

	protected TimeWatch(long milli) {
		this.time = new Date(milli);
	}

	/**
	 * Get the starting date for this object.
	 *
	 * @return The date this time watch started.
	 */
	public Date getStart() {
		return time;
	}

	/**
	 * Gives you the allotted time interval between the starting date and the specified time instant.
	 *
	 * @param stop The target time instant to calculate time between for.
	 * @return A time duration interval.
	 */
	public Duration interval(Instant stop) {
		return Duration.between(time.toInstant(), stop);
	}

	/**
	 * Check if x amount of time has since elapsed since the targeted time unit threshold.
	 *
	 * @param threshold The threshold to use for time conversions.
	 * @param time      The time to use for time conversions
	 * @return true if x amount of time within y time threshold as since elapsed.
	 */
	public boolean hasElapsed(TimeUnit threshold, long time) {
		switch (threshold) {
			case DAYS:
				return TimeUnit.SECONDS.toDays(interval(Instant.now()).getSeconds()) <= time;
			case HOURS:
				return TimeUnit.SECONDS.toHours(interval(Instant.now()).getSeconds()) <= time;
			case MINUTES:
				return TimeUnit.SECONDS.toMinutes(interval(Instant.now()).getSeconds()) <= time;
			case SECONDS:
				return interval(Instant.now()).getSeconds() <= time;
		}
		return false;
	}


}