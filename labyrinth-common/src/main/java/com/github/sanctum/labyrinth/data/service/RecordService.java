package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.common.StopWatch;
import java.util.Date;

import org.jetbrains.annotations.NotNull;

/**
 * Used to keep track of time-span's
 */
public interface RecordService extends Service {

	/**
	 * Get's the amount of time the plugin has been active.
	 *
	 * @return A time recording.
	 */
	@NotNull StopWatch.Recording getTimeActive();

	/**
	 * Get a time recording from a date.
	 *
	 * @param date The start time to use.
	 * @return A new time recording.
	 */
	@NotNull StopWatch.Recording getTimeFrom(Date date);

	/**
	 * Get a time recording from an already parsed time stamp.
	 *
	 * @param l The start time to use.
	 * @return A new time recording.
	 */
	@NotNull StopWatch.Recording getTimeFrom(long l);

}
