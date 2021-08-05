package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.AdvancedHook;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.RegionServicesManagerImpl;
import com.github.sanctum.labyrinth.data.VaultHook;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.data.service.LabyrinthOptions;
import com.github.sanctum.labyrinth.data.service.MenuOverride;
import com.github.sanctum.labyrinth.data.service.ServiceHandshake;
import com.github.sanctum.labyrinth.event.EasyListener;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.event.custom.VentMap;
import com.github.sanctum.labyrinth.event.custom.VentMapImpl;
import com.github.sanctum.labyrinth.formatting.component.WrappedComponent;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.LegacyConfigLocation;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import com.github.sanctum.labyrinth.unity.impl.ItemElement;
import com.github.sanctum.labyrinth.unity.impl.ListElement;
import com.github.sanctum.labyrinth.unity.impl.menu.PaginatedMenu;
import com.github.sanctum.labyrinth.unity.impl.menu.builder.MenuType;
import com.github.sanctum.skulls.CustomHead;
import com.github.sanctum.templates.MetaTemplate;
import com.github.sanctum.templates.Template;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * ▄▄▌***▄▄▄·*▄▄▄▄·**▄·*▄▌▄▄▄**▪***▐*▄*▄▄▄▄▄*▄*.▄
 * ██•**▐█*▀█*▐█*▀█▪▐█▪██▌▀▄*█·██*•█▌▐█•██**██▪▐█
 * ██▪**▄█▀▀█*▐█▀▀█▄▐█▌▐█▪▐▀▀▄*▐█·▐█▐▐▌*▐█.▪██▀▐█
 * ▐█▌▐▌▐█*▪▐▌██▄▪▐█*▐█▀·.▐█•█▌▐█▌██▐█▌*▐█▌·██▌▐▀
 * .▀▀▀**▀**▀*·▀▀▀▀***▀*•*.▀**▀▀▀▀▀▀*█▪*▀▀▀*▀▀▀*·
 * Copyright (C) 2021 <strong>Sanctum</strong>
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * </p>
 * -
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * </p>
 * -
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 * </p>
 * Sanctum, hereby disclaims all copyright interest in the original features of this spigot library.
 */
public final class Labyrinth extends JavaPlugin implements Listener, MenuOverride {

	private static Labyrinth instance;
	private final LinkedList<Cooldown> cooldowns = new LinkedList<>();
	private final LinkedList<WrappedComponent> components = new LinkedList<>();
	private final ConcurrentLinkedQueue<Integer> tasks = new ConcurrentLinkedQueue<>();
	// outer key = namespace; inner key = key
	private final Map<String, Map<String, PersistentContainer>> containers = new ConcurrentHashMap<>();
	private final VentMap eventMap = new VentMapImpl();
	private boolean cachedIsLegacy;
	private boolean cachedNeedsLegacyLocation;
	private int cachedComponentRemoval;
	private long time;

	@Override
	public void onEnable() {
		instance = this;
		this.time = System.currentTimeMillis();
		LabyrinthProvider.instance = this;
		ConfigurationSerialization.registerClass(Template.class);
		ConfigurationSerialization.registerClass(MetaTemplate.class);

		cachedIsLegacy = MenuOverride.super.isLegacy();
		cachedNeedsLegacyLocation = MenuOverride.super.requiresLocationLibrary();

		FileManager copy = FileList.search(this).find("config");

		if (!copy.exists()) {
			FileManager.copy(getResource("config.yml"), copy);
		}

		this.cachedComponentRemoval = copy.readValue(f -> f.getInt("interactive-component-removal"));

		new EasyListener(DefaultEvent.Controller.class).call(this);

		Vent.Subscription.Builder.target(DefaultEvent.Communication.class).assign(Vent.Priority.HIGH).from(this).use((e, subscription) -> {
			switch (e.getCommunicationType()) {
				case CHAT:
					break;
				case COMMAND:


					DefaultEvent.Communication.ChatCommand cmd = e.getCommand().orElse(null);

					if (cmd == null) return;

					String label = cmd.getText().orElse(null);

					if (label == null) return;

					if (HUID.fromString(label.replace("/", "")) != null) {
						if (instance.components.stream().noneMatch(c -> c.toString().equals(label.replace("/", "")))) {
							e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
							e.setCancelled(true);
							return;
						}
					}
					for (WrappedComponent component : instance.components) {
						if (StringUtils.use(label.replace("/", "")).containsIgnoreCase(component.toString())) {
							Schedule.sync(() -> component.action().apply()).run();
							if (!component.isMarked()) {
								component.setMarked(true);
								Schedule.sync(component::remove).waitReal(this.cachedComponentRemoval);
							}
							e.setCancelled(true);
						}
					}

					break;
			}
		}).finish();

		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		getLogger().info("Labyrinth; copyright Sanctum 2021, Open-source spigot development tool.");
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		Schedule.sync(() -> new AdvancedHook(this)).applyAfter(() -> new VaultHook(this)).wait(2);
		Schedule.sync(() -> CommandUtils.initialize(Labyrinth.this)).run();

		// legacy check (FileConfiguration missing getLocation) (Hemp)
		if (isLegacy() || getServer().getVersion().contains("1.14")) {
			// Load our Location util
			ConfigurationSerialization.registerClass(LegacyConfigLocation.class);
		}

		if (LabyrinthOptions.IMPL_REGION_SERVICES.enabled()) {
			RegionServicesManagerImpl.initialize(this);
		}
		Schedule.sync(ServiceHandshake::locate).applyAfter(ServiceHandshake::register).run();

	}

	@Override
	public void onDisable() {

		for (Map<String, PersistentContainer> namespaceMap : containers.values()) {
			for (PersistentContainer component : namespaceMap.values()) {
				for (String key : component.keySet()) {
					try {
						component.save(key);
					} catch (IOException e) {
						getLogger().severe("- Unable to save meta '" + key + "' from namespace " + component.getKey().getNamespace() + ":" + component.getKey().getKey());
						e.printStackTrace();
					}
				}
			}
		}

		for (int id : tasks) {
			getServer().getScheduler().cancelTask(id);
		}
		try {
			Thread.sleep(1);
		} catch (InterruptedException ignored) {
		}

		if (!isLegacy()) {
			if (Item.getCache().size() > 0) {
				for (Item i : Item.getCache()) {
					Item.removeEntry(i);
				}
			}
		}

	}

	public @NotNull VentMap getEventMap() {
		return eventMap;
	}

	@Override
	public @NotNull ConcurrentLinkedQueue<Integer> getTasks() {
		return tasks;
	}

	@Override
	public @NotNull LinkedList<Cooldown> getCooldowns() {
		return cooldowns;
	}

	@Override
	public @NotNull LinkedList<WrappedComponent> getComponents() {
		return components;
	}

	@Override
	public @NotNull List<PersistentContainer> getContainers(Plugin plugin) {
		final Map<String, PersistentContainer> namespaceMap = containers.get(NamespacedKey.toNamespace(plugin));
		if (namespaceMap == null) return ImmutableList.of();
		return ImmutableList.copyOf(namespaceMap.values());
	}

	@Override
	public @NotNull PersistentContainer getContainer(NamespacedKey namespacedKey) {
		final String namespace = namespacedKey.getNamespace();
		final String key = namespacedKey.getKey();
		final Map<String, PersistentContainer> namespaceMap = Optional.ofNullable(containers.get(namespace)).orElseGet(ConcurrentHashMap::new);
		if (containers.containsKey(namespace) && namespaceMap.containsKey(key)) {
			return namespaceMap.get(key);
		}
		final PersistentContainer newContainer = new PersistentContainer(namespacedKey);
		namespaceMap.put(key, newContainer);
		if (containers.containsKey(namespace)) {
			containers.put(namespace, namespaceMap);
		}
		return newContainer;
	}

	@Override
	public boolean isLegacy() {
		return cachedIsLegacy;
	}

	@Override
	public boolean requiresLocationLibrary() {
		return cachedNeedsLegacyLocation;
	}

	@Override
	public Plugin getPluginInstance() {
		return this;
	}

	@Override
	public <T extends Menu, R> R getMenu(Predicate<Menu> predicate, Function<T, R> function) {
		return Menu.get(predicate, function);
	}

	@Override
	public @NotNull Message getNewMessage() {
		return Message.loggedFor(this);
	}

	@Override
	public @NotNull TimeWatch.Recording getTimeActive() {
		return TimeWatch.Recording.subtract(this.time);
	}

	@Override
	public @NotNull TimeWatch.Recording getTimeFrom(Date date) {
		return getTimeFrom(date.getTime());
	}

	@Override
	public @NotNull TimeWatch.Recording getTimeFrom(long l) {
		return TimeWatch.Recording.subtract(l);
	}


}
