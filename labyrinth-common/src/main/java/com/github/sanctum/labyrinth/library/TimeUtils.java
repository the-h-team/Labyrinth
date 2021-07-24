package com.github.sanctum.labyrinth.library;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Hempfest
 */
public class TimeUtils {

	/**
	 * Unlike the other is{Unit}Passed methods this method will ensure that the return value is
	 * based entirely off the fact that the provided date is within x amount of time of the given
	 * time unit threshold.
	 * <p>
	 * Alternatively you can use {@link TimeWatch#isBetween(TimeUnit, long)} from the {@link TimeWatch} object
	 * which is internally used for this method.
	 *
	 * @param date      the starting date to provide
	 * @param time      the amount of time to check has passed
	 * @param threshold the time unit threshold to check
	 * @return true if the allotted amount of time has since passed
	 */
	public static boolean timeElapsed(long date, long time, TimeUnit threshold) {
		return TimeWatch.start(date).isGreaterThan(threshold, time);
	}

	/**
	 * Check if x amount of seconds has passed since a starting date.
	 *
	 * @param date    the starting point date
	 * @param seconds the amount of time to check has passed in seconds
	 * @return if the amount of time has passed this = true
	 */
	public static boolean isSecondsSince(Date date, long seconds) {
		return timeElapsed(date.getTime(), seconds, TimeUnit.SECONDS);
	}

	/**
	 * Check if x amount of minutes has passed since a starting date.
	 *
	 * @param date    the starting point date
	 * @param minutes the amount of time to check has passed in minutes
	 * @return if the amount of time has passed this = true
	 */
	public static boolean isMinutesSince(Date date, long minutes) {
		return timeElapsed(date.getTime(), minutes, TimeUnit.MINUTES);
	}

	/**
	 * Check if x amount of hours has passed since a starting date.
	 *
	 * @param date  the starting point date
	 * @param hours the amount of time to check has passed in hours
	 * @return if the amount of time has passed this = true
	 */
	public static boolean isHoursSince(Date date, long hours) {
		return timeElapsed(date.getTime(), hours, TimeUnit.HOURS);
	}

	/**
	 * Check if x amount of days has passed since a starting date.
	 *
	 * @param date the starting point date
	 * @param days the amount of time to check has passed in days
	 * @return if the amount of time has passed this = true
	 */
	public static boolean isDaysSince(Date date, long days) {
		return timeElapsed(date.getTime(), days, TimeUnit.DAYS);
	}

}
