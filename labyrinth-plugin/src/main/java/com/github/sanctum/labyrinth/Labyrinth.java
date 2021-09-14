package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.AdvancedEconomyImplementation;
import com.github.sanctum.labyrinth.data.Configurable;
import com.github.sanctum.labyrinth.data.DataTable;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.FileType;
import com.github.sanctum.labyrinth.data.ItemStackSerializable;
import com.github.sanctum.labyrinth.data.LegacyConfigLocation;
import com.github.sanctum.labyrinth.data.LocationSerializable;
import com.github.sanctum.labyrinth.data.RegionServicesManagerImpl;
import com.github.sanctum.labyrinth.data.ServiceManager;
import com.github.sanctum.labyrinth.data.VaultImplementation;
import com.github.sanctum.labyrinth.data.container.KeyedServiceManager;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.data.service.ExternalDataService;
import com.github.sanctum.labyrinth.data.service.LabyrinthOptions;
import com.github.sanctum.labyrinth.event.EasyListener;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.LabeledAs;
import com.github.sanctum.labyrinth.event.custom.Subscribe;
import com.github.sanctum.labyrinth.event.custom.VentMap;
import com.github.sanctum.labyrinth.event.custom.VentMapImpl;
import com.github.sanctum.labyrinth.formatting.Bulletin;
import com.github.sanctum.labyrinth.formatting.BulletinSerialization;
import com.github.sanctum.labyrinth.formatting.component.ActionComponent;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.formatting.string.RandomHex;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.task.AsynchronousTaskChain;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.SynchronousTaskChain;
import com.github.sanctum.labyrinth.task.TaskChain;
import com.github.sanctum.templates.MetaTemplate;
import com.github.sanctum.templates.Template;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
@LabeledAs("Core")
public final class Labyrinth extends JavaPlugin implements LabyrinthAPI, Bulletin.Factory {

	private final ServiceManager serviceManager = new ServiceManager();
	private final KeyedServiceManager<Plugin> servicesManager = new KeyedServiceManager<>();
	private final LinkedList<Cooldown> cooldowns = new LinkedList<>();
	private final Map<String, ActionComponent> components = new HashMap<>();
	private final ConcurrentLinkedQueue<Integer> tasks = new ConcurrentLinkedQueue<>();
	private final Set<PersistentContainer> containers = new HashSet<>();
	private final VentMap eventMap = new VentMapImpl();
	private final AsynchronousTaskChain ataskManager = new AsynchronousTaskChain();
	private final SynchronousTaskChain staskManager = new SynchronousTaskChain(this);
	private boolean cachedIsLegacy;
	private boolean cachedIsNew;
	private boolean cachedNeedsLegacyLocation;
	private int cachedComponentRemoval;
	private long time;

	@Override
	public void onEnable() {
		this.time = System.currentTimeMillis();
		LabyrinthProvider.instance = this;
		serviceManager.load(Service.VENT);
		serviceManager.load(Service.TASK);
		serviceManager.load(Service.RECORDING);
		serviceManager.load(Service.DATA);
		serviceManager.load(Service.MESSENGER);
		serviceManager.load(Service.LEGACY);
		serviceManager.load(Service.COOLDOWNS);
		serviceManager.load(Service.COMPONENTS);
		cachedIsLegacy = LabyrinthAPI.super.isLegacy();
		cachedIsNew = LabyrinthAPI.super.isNew();
		cachedNeedsLegacyLocation = LabyrinthAPI.super.requiresLocationLibrary();
		Configurable.registerClass(ItemStackSerializable.class);
		Configurable.registerClass(LocationSerializable.class);
		Configurable.registerClass(BulletinSerialization.class);
		Configurable.registerClass(CustomColor.class);
		ConfigurationSerialization.registerClass(RandomHex.class);
		ConfigurationSerialization.registerClass(Template.class);
		ConfigurationSerialization.registerClass(MetaTemplate.class);

		FileManager copy = FileList.search(this).get("config");
		InputStream stream = getResource("config.yml");
		assert stream != null;
		if (!copy.getRoot().exists()) {
			FileList.copy(stream, copy.getRoot().getParent());
		}
		this.cachedComponentRemoval = copy.read(f -> f.getInt("interactive-component-removal"));
		new EasyListener(DefaultEvent.Controller.class).call(this);
		getEventMap().register(this, this);
		getLogger().info("===================================================================");
		getLogger().info("Labyrinth; copyright Sanctum 2020, Open-source spigot development tool.");
		getLogger().info("===================================================================");
		Schedule.sync(() -> new AdvancedEconomyImplementation(this))
				.applyAfter(() -> new VaultImplementation(this)).wait(2);
		Schedule.sync(() -> CommandUtils.initialize(Labyrinth.this)).run();

		if (requiresLocationLibrary()) {
			ConfigurationSerialization.registerClass(LegacyConfigLocation.class);
		}

		if (LabyrinthOptions.IMPL_REGION_SERVICES.enabled()) {
			RegionServicesManagerImpl.initialize(this);
		}

		ExternalDataService.Handshake handshake = new ExternalDataService.Handshake(this);

		Schedule.sync(handshake::locate).applyAfter(handshake::register).run();


	}

	@Override
	public void onDisable() {

		for (PersistentContainer component : containers) {
			for (String key : component.keySet()) {
				try {
					component.save(key);
				} catch (IOException e) {
					getLogger().severe("- Unable to save meta '" + key + "' from namespace " + component.getKey().getNamespace() + ":" + component.getKey().getKey());
					e.printStackTrace();
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
			for (Item i : Item.getCache()) {
				Item.removeEntry(i);
			}
		}

	}

	@Subscribe
	public void onComponent(DefaultEvent.Communication e) {
		if (e.getCommunicationType() == DefaultEvent.Communication.Type.COMMAND) {
			DefaultEvent.Communication.ChatCommand cmd = e.getCommand().orElse(null);
			if (cmd == null) return;
			String label = cmd.getText();

			ActionComponent component = components.get(label);

			if (component != null) {
				if (!component.isMarked()) {
					if (!component.isTooltip()) {
						Schedule.sync(() -> component.action().run()).run();
						component.setMarked(true);
						Schedule.sync(component::remove).waitReal(this.cachedComponentRemoval);
					} else {
						Schedule.sync(() -> component.action().run()).run();
					}
				}
				e.setCancelled(true);
			}
		}
	}

	@Override
	@NotNull
	public VentMap getEventMap() {
		return eventMap;
	}

	@Override
	@NotNull
	public ConcurrentLinkedQueue<Integer> getConcurrentTaskIds() {
		return tasks;
	}

	@Override
	public TaskChain getScheduler(int runtime) {
		if (runtime <= 1) {
			if (runtime == SYNCHRONOUS) {
				return staskManager;
			}
			if (runtime == ASYNCHRONOUS) {
				return ataskManager;
			}
		}
		return staskManager;
	}

	@Override
	@NotNull
	public LinkedList<Cooldown> getCooldowns() {
		return cooldowns;
	}

	@Override
	@Nullable
	public Cooldown getCooldown(String id) {
		return cooldowns.stream().filter(c -> c.getId().equals(id)).findFirst().orElseGet(() -> {
			FileManager library = FileList.search(this).get("cooldowns", "Persistent", FileType.JSON);
			if (library.read(f -> f.isNode("Library." + id))) {

				long time = library.read(f -> f.getLong("Library." + id + ".expiration"));
				Long a = time;
				Long b = System.currentTimeMillis();
				int compareNum = a.compareTo(b);
				if (!(compareNum <= 0)) {
					Cooldown toMake = new Cooldown() {
						@Override
						public String getId() {
							return id;
						}

						@Override
						public long getCooldown() {
							return time;
						}
					};
					toMake.save();
					return toMake;
				} else {
					DataTable table = DataTable.newTable();
					table.set("Library." + id, null);
					library.write(table);
				}
			}
			return null;
		});
	}

	@Override
	@NotNull
	public List<ActionComponent> getComponents() {
		return new ArrayList<>(components.values());
	}

	@Override
	public void addComponent(ActionComponent component) {
		components.put(component.getId(), component);
	}

	@Override
	public void removeComponent(ActionComponent component) {
		components.remove(component.getId());
	}

	@Override
	@NotNull
	public List<PersistentContainer> getContainers(Plugin plugin) {
		return ImmutableList.copyOf(containers.stream().filter(p -> p.getKey().getNamespace().equals(plugin.getName())).collect(Collectors.toSet()));
	}

	@Override
	@NotNull
	public PersistentContainer getContainer(@NotNull NamespacedKey namespacedKey) {
		return containers.stream().filter(p -> p.getKey().equals(namespacedKey)).findFirst().orElseGet(() -> {
			PersistentContainer container = new PersistentContainer(namespacedKey);
			this.containers.add(container);
			return container;
		});
	}

	@Override
	public boolean isLegacy() {
		return cachedIsLegacy;
	}

	@Override
	public boolean isNew() {
		return cachedIsNew;
	}

	@Override
	public boolean requiresLocationLibrary() {
		return cachedNeedsLegacyLocation;
	}

	@Override
	public KeyedServiceManager<Plugin> getServicesManager() {
		return this.servicesManager;
	}

	@Override
	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	@Override
	public Plugin getPluginInstance() {
		return this;
	}

	@Override
	@NotNull
	public Message getNewMessage() {
		return Message.loggedFor(this);
	}

	@Override
	@NotNull
	public TimeWatch.Recording getTimeActive() {
		return TimeWatch.Recording.subtract(this.time);
	}

	@Override
	@NotNull
	public TimeWatch.Recording getTimeFrom(Date date) {
		return getTimeFrom(date.getTime());
	}

	@Override
	@NotNull
	public TimeWatch.Recording getTimeFrom(long l) {
		return TimeWatch.Recording.subtract(l);
	}


}
