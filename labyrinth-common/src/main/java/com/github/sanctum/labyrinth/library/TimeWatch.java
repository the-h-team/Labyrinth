package com.github.sanctum.labyrinth.library;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Used for accurate time span inquisitions.
 *
 * @author Hempfest
 */
public class TimeWatch {

	private final long time;

	/**
	 * Use a specified starting date in milliseconds.
	 *
	 * @param milli the starting time interval in milliseconds
	 * @return a new time watch object
	 */
	public static TimeWatch start(long milli) {
		return new TimeWatch(milli);
	}

	/**
	 * Use a specified starting date in milliseconds.
	 *
	 * @param date the starting date
	 * @return a new time watch object
	 */
	public static TimeWatch start(Date date) {
		return new TimeWatch(date);
	}

	protected TimeWatch(long milli) {
		this.time = milli;
	}

	protected TimeWatch(Date date) {
		this.time = date.getTime();
	}

	/**
	 * Get the starting date for this object.
	 *
	 * @return the date this time watch started
	 */
	public Date getStart() {
		return new Date(time);
	}

	/**
	 * Calculates the allotted time interval between the starting date and the specified time instant.
	 *
	 * @param stop the target time instant to calculate time between for
	 * @return a time duration interval
	 */
	public Duration interval(Instant stop) {
		return Duration.between(getStart().toInstant(), stop);
	}

	/**
	 * Check if x amount of time is between now and the starting date.
	 *
	 * @param threshold the threshold to use for time conversions
	 * @param time      the time to use for time conversions
	 * @return true if time within threshold has elapsed
	 */
	public boolean isBetween(TimeUnit threshold, long time) {
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
	 * @param threshold the threshold to use for time conversions
	 * @param time      the time to use for time conversions
	 * @return true if time within threshold has elapsed
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

	public Recording toRecording() {
		return Recording.subtract(this.time);
	}

	/**
	 * Encapsulates stop-watch like data.
	 */
	public static class Recording implements ParsedTimeFormat {

		private final long time;

		protected Recording(long milli) {
			this.time = milli;
		}

		/**
		 * Subtract a timestamp from another manually to feed as a starting point.
		 *
		 * @param milli the already-equated timestamp
		 * @return a stop watch record for this timestamp
		 */
		public static Recording from(long milli) {
			return new Recording(milli);
		}

		/**
		 * Subtract the specified timestamp from the current time
		 * to feed as a starting point.
		 *
		 * @param milli the starting timestamp
		 * @return a stop watch record for this timestamp
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
			return (time / (1000 * 60 * 60 * 24));
		}

		@Override
		public String toString() {
			return getDays() + " days " + getHours() + " hours " + getMinutes() + " minutes " + getSeconds() + " seconds.";
		}

	}


}