package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.Synchronous;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A utility used for player tab list provision.
 */
public interface TablistInstance {

	/**
	 * Get the human entity this instance is for.
	 *
	 * @return The human entity for this instance.
	 */
	Player getHolder();

	/**
	 * Get all tab info groups within this instance.
	 *
	 * @return A collection of tab groups.
	 */
	Collection<TabGroup> getGroups();

	/**
	 * Get a tab info group by its special key
	 *
	 * @param key The key to search for
	 * @return The desired tab group or null
	 */
	TabGroup getGroup(String key);

	/**
	 * Get the current active tab group.
	 *
	 * @return The current active tab group.
	 */
	TabGroup getCurrent();

	/**
	 * Get the previously used tab group.
	 *
	 * @return The previously active tab group if present or null
	 */
	TabGroup getPrevious();

	/**
	 * Add a custom tab group to this instance.
	 *
	 * @param group The group to add.
	 * @return true if added.
	 */
	boolean add(TabGroup group);

	/**
	 * Remove a tab group from this instance.
	 *
	 * @param group The group to remove.
	 * @return true if removed.
	 */
	boolean remove(TabGroup group);

	/**
	 * Activate this tab list display instance if it's not already active.
	 *
	 * @return true if the instance was activated, false if already running.
	 */
	boolean enable();

	/**
	 * Activate this tab list display instance and include custom meta within the task.
	 *
	 * @param consumer The meta to include.
	 * @return true if the instance was activated, false if already running.
	 */
	boolean enable(Consumer<Player> consumer);

	/**
	 * De-activate this tab list display instance if it's already active.
	 *
	 * @return true if the instance was de-activated, false if not running.
	 */
	boolean disable();

	/**
	 * De-activate this tba list display instance with custom included meta.
	 *
	 * @param consumer The meta to include.
	 * @return true if the instance was de-activated, false if not running.
	 */
	boolean disable(Consumer<Player> consumer);

	/**
	 * Check if this instance is currently activated.
	 *
	 * @return true if this tab list instance is being displayed to the player.
	 */
	boolean isEnabled();

	/**
	 * Set the activity status on a specific tab group.
	 *
	 * @param group  The tab group to modify
	 * @param active The status to set.
	 */
	void setActive(TabGroup group, boolean active);

	/**
	 * Get a cached reference of a players personal tab list instance.
	 *
	 * @param target The target to retrieve.
	 * @return A firmly tracked tab list reference.
	 */
	static @NotNull TablistInstance get(@NotNull Player target) {
		return TabInfo.instances.computeIfAbsent(target, player -> new TablistInstance() {
			private final Map<String, TabGroup> groups = new HashMap<>();
			private Synchronous task;
			private String previous;

			@Override
			public Player getHolder() {
				return player;
			}

			@Override
			public Collection<TabGroup> getGroups() {
				return groups.values();
			}

			@Override
			public TabGroup getGroup(String key) {
				return groups.get(key);
			}

			@Override
			public TabGroup getCurrent() {
				return getGroups().stream().filter(TabGroup::isActive).findFirst().orElse(null);
			}

			@Override
			public TabGroup getPrevious() {
				return groups.get(previous);
			}

			@Override
			public boolean add(TabGroup group) {
				groups.put(group.getKey(), group);
				return true;
			}

			@Override
			public boolean remove(TabGroup group) {
				groups.remove(group.getKey());
				return true;
			}

			@Override
			public boolean enable() {
				if (task != null && task.isRunning()) return false;
				if (task == null) {
					task = Schedule.sync(() -> getGroups().stream().filter(TabGroup::isActive).findFirst().ifPresent(tabGroup -> {
						tabGroup.nextDisplayIndex(TabInfo.HEADER);
						tabGroup.nextDisplayIndex(TabInfo.FOOTER);
						((Player) getHolder()).setPlayerListHeaderFooter(tabGroup.getHeader(tabGroup.getCurrentHeaderIndex()).toString(), tabGroup.getFooter(tabGroup.getCurrentFooterIndex()).toString());
					}));
				}
				task.repeatReal(0, 1);
				return true;
			}

			@Override
			public boolean enable(Consumer<Player> consumer) {
				if (task != null && task.isRunning()) return false;
				if (task == null) {
					task = Schedule.sync(() -> getGroups().stream().filter(TabGroup::isActive).findFirst().ifPresent(tabGroup -> {
						tabGroup.nextDisplayIndex(TabInfo.HEADER);
						tabGroup.nextDisplayIndex(TabInfo.FOOTER);
						getHolder().setPlayerListHeaderFooter(tabGroup.getHeader(tabGroup.getCurrentHeaderIndex()).toString(), tabGroup.getFooter(tabGroup.getCurrentFooterIndex()).toString());
						consumer.accept(getHolder());
					}));
				}
				task.repeatReal(0, 1);
				return true;
			}

			@Override
			public boolean disable() {
				if (task == null || !task.isRunning()) return false;
				task.cancelTask();
				return true;
			}

			@Override
			public boolean disable(Consumer<Player> consumer) {
				if (task == null || !task.isRunning()) return false;
				task.cancelTask();
				consumer.accept(getHolder());
				return true;
			}

			@Override
			public boolean isEnabled() {
				return task != null && task.isRunning();
			}

			@Override
			public void setActive(TabGroup group, boolean active) {
				if (active) {
					groups.values().forEach(group1 -> {
						if (!group1.equals(group)) {
							if (group1.isActive()) {
								group1.setActive(false);
								this.previous = group.getKey();
							}
						}

					});
					group.setActive(true);
				} else {
					this.previous = group.getKey();
					group.setActive(false);
				}
			}
		});
	}

	/**
	 * Get all cached tab list instances.
	 *
	 * @return all known tab list instances.
	 */
	static @NotNull TablistInstance[] getAll() {
		return Bukkit.getOnlinePlayers().stream().map(TablistInstance::get).toArray(TablistInstance[]::new);
	}

}
