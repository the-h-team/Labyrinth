package com.youtube.hempfest.hempcore.library;

import com.youtube.hempfest.hempcore.data.Config;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class Cooldown implements Serializable {

	public abstract String getId();

	public abstract long getCooldown();

	protected long getTimePassed() {
		return (System.currentTimeMillis() - getCooldown()) / 1000;
	}

	protected int getTimeLeft() {
		return Integer.parseInt(String.valueOf(getTimePassed()).replace("-", ""));
	}

	public int getDaysLeft() {
		return (int) TimeUnit.SECONDS.toDays(getTimeLeft());
	}

	public long getHoursLeft() {
		return TimeUnit.SECONDS.toHours(getTimeLeft()) - (getDaysLeft() * 24);
	}

	public long getMinutesLeft() {
		return TimeUnit.SECONDS.toMinutes(getTimeLeft()) - (TimeUnit.SECONDS.toHours(getTimeLeft()) * 60);
	}

	public long getSecondsLeft() {
		return TimeUnit.SECONDS.toSeconds(getTimeLeft()) - (TimeUnit.SECONDS.toMinutes(getTimeLeft()) * 60);
	}

	public boolean isComplete() {
		Config cooldowns = Config.get("Storage", "Cooldowns");
		Long a = cooldowns.getConfig().getLong(getId() + ".expiration");
		Long b = System.currentTimeMillis();
		int compareNum = a.compareTo(b);
		if (!cooldowns.getConfig().isLong(getId() + ".expiration")) {
			return true;
		}
		return compareNum <= 0;
	}

	public String fullTimeLeft() {
		return "(S)" + getSecondsLeft() + " : (M)" + getMinutesLeft() + " : (H)" + getHoursLeft() + " : (D)" + getDaysLeft();
	}

	public void save() {
		Config cooldowns = Config.get("Storage", "Cooldowns");
		cooldowns.getConfig().set(getId() + ".expiration", getCooldown());
		try {
			cooldowns.getConfig().set(getId() + ".instance", new HFEncoded(this).serialize());
		} catch (IOException e) {
			e.printStackTrace();
		}
		cooldowns.saveConfig();
	}

	protected long abv(int seconds) {
		return System.currentTimeMillis() + (seconds * 1000);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Cooldown)) return false;
		Cooldown cooldown = (Cooldown) o;
		return Objects.equals(getId(), cooldown.getId()) && Objects.equals(getCooldown(), cooldown.getCooldown());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getCooldown());
	}

	public static Cooldown getById(String id) {
		Config cooldowns = Config.get("Storage", "Cooldowns");
		Cooldown result = null;
		try {
			result = (Cooldown) new HFEncoded(cooldowns.getConfig().getString(id + ".instance")).deserialized();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void remove(Cooldown c) {
		Config cooldowns = Config.get("Storage", "Cooldowns");
		cooldowns.getConfig().set(c.getId(), null);
		cooldowns.saveConfig();
	}


}
