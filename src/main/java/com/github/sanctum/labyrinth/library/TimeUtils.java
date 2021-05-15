package com.github.sanctum.labyrinth.library;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

	/**
	 * NOTE: Asynchronous logic applied
	 * Using the callable interface compare a time instance with a duration
	 * (Check if x amount of time has passed).
	 *
	 * @param then The starting point
	 * @param threshold The amount of time to check has passed.
	 * @return If the desired amount of time has since passed this will = true
	 */
	public static boolean timeHasElapsedSince(Instant then, Callable<Duration> threshold) {
		if (threshold == null) {
			return false;
		}
		Duration result = null;
		try {
			result = threshold.call();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (result == null) {
			return false;
		}
		return Duration.between(then, Instant.now()).getSeconds() > result.getSeconds();
	}

	/**
	 * Unlike the other is{Unit}Passed methods this method will ensure that the return value is
	 * based entirely off the fact that the provided date is within x amount of time of the given
	 * time unit threshold.
	 * <p>
	 * Alternatively you can use {@link TimeWatch#hasElapsed(TimeUnit, long)} from the {@link TimeWatch} object
	 * which is internally used for this method.
	 *
	 * @param date      The starting date to provide.
	 * @param time      The amount of time to check has passed
	 * @param threshold The time unit threshold to check
	 * @return true if the allotted amount of time has since passed.
	 */
	public static boolean timeElapsed(long date, long time, TimeUnit threshold) {
		return TimeWatch.start(date).hasElapsed(threshold, time);
	}

	/**
	 * Check if x amount of seconds has passed since a starting date.
	 *
	 * @param date    The starting point date
	 * @param seconds The amount of time to check has passed in seconds
	 * @return If the amount of time has passed this = true
	 */
	public static boolean isSecondsSince(Date date, long seconds) {
		return CompletableFuture.supplyAsync(() -> timeHasElapsedSince(date.toInstant(), () -> Duration.ofSeconds(seconds))).join();
	}

	/**
	 * Check if x amount of minutes has passed since a starting date.
	 *
	 * @param date The starting point date
	 * @param minutes The amount of time to check has passed in minutes
	 * @return If the amount of time has passed this = true
	 */
	public static boolean isMinutesSince(Date date, long minutes) {
		return CompletableFuture.supplyAsync(() -> timeHasElapsedSince(date.toInstant(), () -> Duration.ofMinutes(minutes))).join();
	}

	/**
	 * Check if x amount of hours has passed since a starting date.
	 *
	 * @param date The starting point date
	 * @param hours The amount of time to check has passed in hours
	 * @return If the amount of time has passed this = true
	 */
	public static boolean isHoursSince(Date date, long hours) {
		return CompletableFuture.supplyAsync(() -> timeHasElapsedSince(date.toInstant(), () -> Duration.ofHours(hours))).join();
	}

	/**
	 * Check if x amount of days has passed since a starting date.
	 *
	 * @param date The starting point date
	 * @param days The amount of time to check has passed in days
	 * @return If the amount of time has passed this = true
	 */
	public static boolean isDaysSince(Date date, long days) {
		return CompletableFuture.supplyAsync(() -> timeHasElapsedSince(date.toInstant(), () -> Duration.ofDays(days))).join();
	}

}
