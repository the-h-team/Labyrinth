package com.github.sanctum.labyrinth.library;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public interface ParsedTimeFormat {

	long getDays();

	long getHours();

	long getMinutes();

	long getSeconds();

	default long toSeconds() {
		return TimeUnit.DAYS.toSeconds(getDays()) + TimeUnit.HOURS.toSeconds(getHours()) + TimeUnit.MINUTES.toSeconds(getMinutes()) + getSeconds();
	}

	@NotNull default Date getDate() {
		long seconds = toSeconds();
		long time = System.currentTimeMillis() + (seconds * 1000);
		return new Date(time);
	}

	static @NotNull ParsedTimeFormat of(@NotNull String format) throws IllegalTimeFormatException {
		return StringUtils.use(format).parseTime();
	}

	static @NotNull ParsedTimeFormat of(long days, long hours, long minutes, long seconds) {
		return new ParsedTimeFormat() {
			@Override
			public long getDays() {
				return days;
			}

			@Override
			public long getHours() {
				return hours;
			}

			@Override
			public long getMinutes() {
				return minutes;
			}

			@Override
			public long getSeconds() {
				return seconds;
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
		};
	}

}
