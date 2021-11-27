package com.github.sanctum.labyrinth.formatting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public interface TablistInstance {

	HumanEntity getViewer();

	Collection<TabGroup> getGroups();

	TabGroup getGroup(String key);

	TabGroup getPrevious();

	boolean add(TabGroup group);

	boolean remove(TabGroup group);

	void setActive(TabGroup group, boolean active);

	static TablistInstance get(Player target) {
		return TabInfo.instances.computeIfAbsent(target, player -> new TablistInstance() {
			private final Map<String, TabGroup> groups = new HashMap<>();
			private String previous;

			@Override
			public HumanEntity getViewer() {
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

}
