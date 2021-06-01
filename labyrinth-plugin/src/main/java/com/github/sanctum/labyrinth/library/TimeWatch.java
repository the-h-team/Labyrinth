package com.github.sanctum.labyrinth.library;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Used for accurate time span inquisitions
 */
public class TimeWatch {

	private final long time;

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
		this.time = milli;
	}

	/**
	 * Get the starting date for this object.
	 *
	 * @return The date this time watch started.
	 */
	public Date getStart() {
		return new Date(time);
	}

	/**
	 * Gives you the allotted time interval between the starting date and the specified time instant.
	 *
	 * @param stop The target time instant to calculate time between for.
	 * @return A time duration interval.
	 */
	public Duration interval(Instant stop) {
		return Duration.between(getStart().toInstant(), stop);
	}

	/**
	 * Check if x amount of time is between now and the starting date.
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
				return TimeUnit.SECONDS.toSeconds(interval(Instant.now()).getSeconds()) <= time;
		}
		return false;
	}

	/**
	 * Check if x amount of time or more has passed since the starting date.
	 *
	 * @param threshold The threshold to use for time conversions.
	 * @param time      The time to use for time conversions
	 * @return true if x amount of time within y time threshold as since elapsed.
	 */
	public boolean isGreaterThan(TimeUnit threshold, long time) {
		switch (threshold) {
			case DAYS:
				return TimeUnit.SECONDS.toDays(interval(Instant.now()).getSeconds()) >= time;
			case HOURS:
				return TimeUnit.SECONDS.toHours(interval(Instant.now()).getSeconds()) >= time;
			case MINUTES:
				return TimeUnit.SECONDS.toMinutes(interval(Instant.now()).getSeconds()) >= time;
			case SECONDS:
				return TimeUnit.SECONDS.toSeconds(interval(Instant.now()).getSeconds()) >= time;
		}
		return false;
	}

	/**
	 * Encapsulates stop-watch like data.
	 */
	public static class Recording {

		private final long time;

		protected Recording(long milli) {
			this.time = milli;
		}

		/**
		 * Subtract a time stamp from another manually to feed as a starting point.
		 *
		 * @param milli The already equated time stamp.
		 * @return A stop watch record for this time stamp.
		 */
		public static Recording from(long milli) {
			return new Recording(milli);
		}

		/**
		 * Subtract the specified time stamp from the current time to feed as a starting point.
		 *
		 * @param milli The starting time stamp.
		 * @return A stop watch record for this time stamp.
		 */
		public static Recording subtract(long milli) {
			return new Recording(System.currentTimeMillis() - milli);
		}

		public long getSeconds() {
			return time / 1000 % 60;
		}

		public long getMinutes() {
			return time / (60 * 1000) % 60;
		}

		public long getHours() {
			return time / (60 * 60 * 1000);
		}

		public long getDays() {
			return time / (60 * 60 * 1000 * 24);
		}

	}


}