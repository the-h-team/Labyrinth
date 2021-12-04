package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.Synchronous;
import com.github.sanctum.labyrinth.task.Task;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
	 * Activate this tab list display instance and include custom meta within the task, alternating displays every x y (x being time and y being the unit.)
	 *
	 * @param consumer The meta to include.
	 * @param unit The time unit to use
	 * @param period The amount of time to use.
	 * @return true if the instance was activated, false if already running.
	 */
	boolean enable(Consumer<Player> consumer, TimeUnit unit, long period);

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
				return enable(player1 -> {}, TimeUnit.MILLISECONDS, 40);
			}

			@Override
			public boolean enable(Consumer<Player> consumer) {
				return enable(consumer, TimeUnit.MILLISECONDS, 40);
			}

			@Override
			public boolean enable(Consumer<Player> consumer, TimeUnit unit, long period) {
				if (isEnabled()) return false;
				LabyrinthProvider.getInstance().getScheduler(TaskService.SYNCHRONOUS).repeat(task1 -> {

					if (getHolder() == null) {
						task1.cancel();
						return;
					}

					getGroups().stream().filter(TabGroup::isActive).findFirst().ifPresent(tabGroup -> {
						tabGroup.nextDisplayIndex(TabInfo.HEADER);
						tabGroup.nextDisplayIndex(TabInfo.FOOTER);
						getHolder().setPlayerListHeaderFooter(StringUtils.use(tabGroup.getHeader(tabGroup.getCurrentHeaderIndex()).toString()).translate(getHolder()), StringUtils.use(tabGroup.getFooter(tabGroup.getCurrentFooterIndex()).toString()).translate(getHolder()));
						consumer.accept(getHolder());
					});
				}, getHolder().getName() + "-tablist", unit.toMillis(period), unit.toMillis(period));
				return true;
			}

			@Override
			public boolean disable() {
				if (!isEnabled()) return false;
				LabyrinthProvider.getInstance().getScheduler(TaskService.SYNCHRONOUS).get(getHolder().getName() + "-tablist").cancel();
				return true;
			}

			@Override
			public boolean disable(Consumer<Player> consumer) {
				if (!isEnabled()) return false;
				LabyrinthProvider.getInstance().getScheduler(TaskService.SYNCHRONOUS).get(getHolder().getName() + "-tablist").cancel();
				consumer.accept(getHolder());
				return true;
			}

			@Override
			public boolean isEnabled() {
				return Optional.ofNullable(LabyrinthProvider.getInstance().getScheduler(TaskService.SYNCHRONOUS).get(getHolder().getName() + "-tablist")).isPresent();
			}

			@Override
			public void setActive(TabGroup group, boolean active) {
				if (active) {
					groups.values().forEach(group1 -> {
						if (!group1.equals(group)) {
							if (group1.isActive()) {
								group1.setActive(false);
								this.previous = group1.getKey();
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
