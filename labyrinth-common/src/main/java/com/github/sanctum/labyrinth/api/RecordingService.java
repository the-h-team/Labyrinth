package com.github.sanctum.labyrinth.api;

import com.github.sanctum.labyrinth.library.TimeWatch;
import java.util.Date;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Used to keep track of time-span's
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public interface RecordingService extends Service {

	/**
	 * Get's the amount of time the plugin has been active.
	 *
	 * @return A time recording.
	 */
	@NotNull TimeWatch.Recording getTimeActive();

	/**
	 * Get a time recording from a date.
	 *
	 * @param date The start time to use.
	 * @return A new time recording.
	 */
	@NotNull TimeWatch.Recording getTimeFrom(Date date);

	/**
	 * Get a time recording from an already parsed time stamp.
	 *
	 * @param l The start time to use.
	 * @return A new time recording.
	 */
	@NotNull TimeWatch.Recording getTimeFrom(long l);

}
