package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.BukkitLegacyCheckService;
import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.PlaceholderFormatService;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.command.LabyrinthCommand;
import com.github.sanctum.labyrinth.command.LabyrinthCommandToken;
import com.github.sanctum.labyrinth.data.AdvancedEconomyImplementation;
import com.github.sanctum.labyrinth.data.ChunkSerializable;
import com.github.sanctum.labyrinth.data.DataTable;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.ItemStackSerializable;
import com.github.sanctum.labyrinth.data.LegacyConfigLocation;
import com.github.sanctum.labyrinth.data.LocationSerializable;
import com.github.sanctum.labyrinth.data.MessageSerializable;
import com.github.sanctum.labyrinth.data.MetaTemplateSerializable;
import com.github.sanctum.labyrinth.data.PlayerPlaceholders;
import com.github.sanctum.labyrinth.data.RegionServicesManagerImpl;
import com.github.sanctum.labyrinth.data.ServiceManager;
import com.github.sanctum.labyrinth.data.ServiceType;
import com.github.sanctum.labyrinth.data.TemplateSerializable;
import com.github.sanctum.labyrinth.data.container.KeyedServiceManager;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.data.reload.PrintManager;
import com.github.sanctum.labyrinth.data.service.AnvilMechanicsLoader;
import com.github.sanctum.labyrinth.data.service.ExternalDataService;
import com.github.sanctum.labyrinth.data.service.LabyrinthOption;
import com.github.sanctum.labyrinth.data.service.PlayerSearch;
import com.github.sanctum.labyrinth.data.service.VentMapImpl;
import com.github.sanctum.labyrinth.event.DefaultEvent;
import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.formatting.component.ActionComponent;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.interfacing.Token;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.ItemCompost;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.permissions.Permissions;
import com.github.sanctum.labyrinth.permissions.impl.DefaultImplementation;
import com.github.sanctum.labyrinth.permissions.impl.VaultImplementation;
import com.github.sanctum.labyrinth.task.SynchronousTaskChain;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.event.Subscribe;
import com.github.sanctum.panther.event.Vent;
import com.github.sanctum.panther.event.VentMap;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.panther.file.Node;
import com.github.sanctum.panther.placeholder.PlaceholderRegistration;
import com.github.sanctum.panther.recursive.ServiceFactory;
import com.github.sanctum.panther.util.Applicable;
import com.github.sanctum.panther.util.Deployable;
import com.github.sanctum.panther.util.PantherLogger;
import com.github.sanctum.panther.util.ResourceLookup;
import com.github.sanctum.panther.util.TaskChain;
import com.github.sanctum.templates.MetaTemplate;
import com.github.sanctum.templates.Template;
import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
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
@Vent.Link.Key("Core")
public final class Labyrinth extends JavaPlugin implements Vent.Host, Listener, LabyrinthAPI, Message.Factory {

	private final ServiceManager serviceManager = new ServiceManager();
	private final KeyedServiceManager<Plugin> servicesManager = new KeyedServiceManager<>();
	private final LinkedList<Cooldown> cooldowns = new LinkedList<>();
	private final Map<String, ActionComponent> components = new HashMap<>();
	private final ConcurrentLinkedQueue<Integer> tasks = new ConcurrentLinkedQueue<>();
	private final Set<PersistentContainer> containers = new HashSet<>();
	private final ItemCompost composter = new ItemCompost();
	private final PrintManager manager = new PrintManager();
	private final SynchronousTaskChain syncChain = new SynchronousTaskChain(this);
	private Token<Labyrinth> validCommandToken;
	private BukkitLegacyCheckService cachedLegacyCheckService;
	private int cachedComponentRemoval;
	private long time;

	@Override
	public void onLoad() {
		TaskChain.setChain(0, syncChain);
		this.validCommandToken = new LabyrinthCommandToken(this);
		PantherLogger.getInstance().setLogger(getLogger());
		if (VentMap.getInstance() instanceof VentMap.Default) {
			ServiceFactory.getInstance().getLoader(VentMap.class).supply(new VentMapImpl());
		}
	}

	@Override
	public void onEnable() {
		this.time = System.currentTimeMillis();
		LabyrinthProvider.instance = this;
		getLogger().info("- Copyright Team Sanctum 2020, Open-source spigot development tool.");
		getLogger().info("- Loading user cache, please be patient...");
		// temporary, move components yaml to json file.
		FileManager manager = FileList.search(this).get("Components", "Persistent");
		if (manager.getRoot().exists()) {
			FileManager n = manager.toJSON("components", "Persistent");
			Configurable c = n.getRoot();
			c.save();
			c.reload();
			manager.getRoot().delete();
		}
		PlayerSearch.reload().deploy();
		registerServices().deploy();
		registerJsonAdapters().deploy();
		registerYamlAdapters().deploy();
		getEventMap().subscribeAll(this, new DefaultEvent.Controller(), this);
		registerImplementations().deploy();
		registerHandshake().deploy();
		registerDefaultPlaceholders().deploy();
	}

	Deployable<Labyrinth> registerServices() {
		return Deployable.of(() -> {
			serviceManager.load(Service.TASK);
			serviceManager.load(Service.RECORDING);
			serviceManager.load(Service.DATA);
			serviceManager.load(Service.MESSENGER);
			serviceManager.load(Service.LEGACY);
			serviceManager.load(Service.COOLDOWNS);
			serviceManager.load(Service.COMPONENTS);
			serviceManager.load(new ServiceType<>(() -> (PlaceholderFormatService) (text, variable) -> {
				String result = text;
				if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
					if (variable instanceof OfflinePlayer) {
						result = PlaceholderAPI.setPlaceholders((OfflinePlayer) variable, text);
					}
				}
				return PlaceholderRegistration.getInstance().replaceAll(result, variable);
			}));
			servicesManager.register(new DefaultImplementation(), this, ServicePriority.Low);
			try {
				CommandUtils.register(new LabyrinthCommand((LabyrinthCommandToken) validCommandToken));
			} catch (IllegalAccessException ignored) {
			}
			cachedLegacyCheckService = new BukkitLegacyCheckService();
			return this;
		}, 0);
	}

	Deployable<Labyrinth> registerImplementations() {
		return Deployable.of(() -> {
			TaskScheduler.of(() -> new AdvancedEconomyImplementation(this)).scheduleLater(12)
					.next(() -> new com.github.sanctum.labyrinth.data.VaultImplementation(this)).scheduleLater(12)
					.next(() -> {
						if (getServer().getPluginManager().isPluginEnabled("Vault")) {
							VaultImplementation bridge = new VaultImplementation();
							getServicesManager().register(bridge, bridge.getProvider(), ServicePriority.Normal);
						}
						Permissions instance = getServicesManager().load(Permissions.class);
						assert instance != null;
						// we know it's not null because of the default implementation.
						if (instance.getProvider().equals(this)) {
							getLogger().info("- Using default labyrinth implementation for permissions (No provider).");
						} else {
							if (instance instanceof VaultImplementation) {
								getLogger().info("- Using " + instance.getProvider().getName() + " for permissions. (Vault)");
							} else {
								getLogger().info("- Using " + instance.getProvider().getName() + " for permissions. (Provider)");
							}
						}
					}).scheduleLater(12);

			if (isLegacyVillager()) {
				ConfigurationSerialization.registerClass(LegacyConfigLocation.class);
			}
			if (LabyrinthOption.IMPL_REGION_SERVICES.enabled()) {
				RegionServicesManagerImpl.initialize(this);
			}
			return this;
		}, 0);
	}

	Deployable<Labyrinth> registerHandshake() {
		return Deployable.of(() -> {
			TaskScheduler.of(ExternalDataService.Handshake.getInstance(this)).schedule();
			return this;
		}, 0);
	}

	Deployable<Labyrinth> registerDefaultPlaceholders() {
		return Deployable.of(() -> {
			PlaceholderRegistration registration = PlaceholderRegistration.getInstance();
			registration.registerTranslation(new PlayerPlaceholders());
			return this;
		}, 0);
	}

	Deployable<Labyrinth> registerJsonAdapters() {
		return Deployable.of(() -> {
			Configurable.registerClass(ItemStackSerializable.class);
			Configurable.registerClass(LocationSerializable.class);
			Configurable.registerClass(TemplateSerializable.class);
			Configurable.registerClass(MetaTemplateSerializable.class);
			Configurable.registerClass(MessageSerializable.class);
			Configurable.registerClass(ChunkSerializable.class);
			Configurable.registerClass(CustomColor.class);
			return this;
		}, 0);
	}

	Deployable<Labyrinth> registerYamlAdapters() {
		return Deployable.of(() -> {
			ConfigurationSerialization.registerClass(CustomColor.class);
			ConfigurationSerialization.registerClass(Template.class);
			ConfigurationSerialization.registerClass(MetaTemplate.class);
			FileManager copy = FileList.search(this).get("config");
			InputStream stream = getResource("config.yml");
			assert stream != null;
			if (!copy.getRoot().exists()) {
				FileList.copy(stream, copy.getRoot().getParent());
			}
			this.cachedComponentRemoval = copy.read(f -> f.getInt("interactive-component-removal"));
			return this;
		}, 0);
	}


	@Override
	public void onDisable() {

		for (int id : tasks) {
			getServer().getScheduler().cancelTask(id);
		}

		getScheduler(ASYNCHRONOUS).shutdown();
		getScheduler(SYNCHRONOUS).shutdown();

		try {
			Thread.sleep(1); // let daemon catchup and remove background tasks
		} catch (InterruptedException ignored) {
		}

		if (!isLegacy() && !isModded()) {
			for (Item i : Item.getRegistered()) {
				Item.removeEntry(i);
			}
		}
	}

	@Subscribe(priority = Vent.Priority.LOW)
	public void onJoin(DefaultEvent.Join e) {
		PlayerSearch.of(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onComponent(PlayerCommandPreprocessEvent e) {
		String label = e.getMessage().split(" ")[0].replace("/", "");
		ActionComponent component = components.get(label);

		if (component != null) {
			Applicable action = component.action();
			if (!component.isMarked()) {
				if (!component.isTooltip()) {
					TaskScheduler.of(action).schedule();
					component.setMarked(true);
					TaskScheduler.of(component::remove).scheduleLater(cachedComponentRemoval);
				} else {
					TaskScheduler.of(action).schedule();
				}
			}
			e.setCancelled(true);
		}
	}

	@Override
	@NotNull
	public VentMap getEventMap() {
		return VentMap.getInstance();
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
				return syncChain;
			}
			if (runtime == ASYNCHRONOUS) {
				return TaskChain.getAsynchronous();
			}
		}
		return syncChain;
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
			FileManager library = FileList.search(this).get("cooldowns", "Persistent", Configurable.Type.JSON);
			if (library.read(f -> f.isNode("Library." + id))) {

				long time = library.read(f -> f.getLong("Library." + id + ".expiration"));
				String s = library.read(f -> f.getString("Library." + id + ".descriptor"));
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

						@Override
						public String getDescriptor() {
							return s;
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
	public boolean remove(Cooldown cooldown) {
		if (!cooldowns.contains(cooldown)) return false;
		Node home = FileList.search(LabyrinthProvider.getInstance().getPluginInstance())
				.get("cooldowns", "Persistent", Configurable.Type.JSON)
				.read(t -> t.getNode("Library." + cooldown.getId()));
		home.delete();
		home.save();
		TaskScheduler.of(() -> cooldowns.remove(cooldown)).schedule();
		return true;
	}

	@Override
	@NotNull
	public List<ActionComponent> getComponents() {
		return Collections.unmodifiableList(new ArrayList<>(components.values()));
	}

	@Override
	public Deployable<Void> registerComponent(ActionComponent component) {
		return Deployable.of(() -> {
			this.components.put(component.getId(), component);
		}, 0);
	}

	@Override
	public Deployable<Void> removeComponent(ActionComponent component) {
		return Deployable.of(() -> {
			this.components.remove(component.getId());
		}, 0);
	}

	@Override
	@NotNull
	public List<PersistentContainer> getContainers(Plugin plugin) {
		return ImmutableList.copyOf(containers.stream().filter(p -> p.getKey().getNamespace().equals(plugin.getName().toLowerCase(Locale.ROOT))).collect(Collectors.toSet()));
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
	public boolean isModded() {
		return cachedLegacyCheckService.isModded();
	}

	@Override
	public boolean isLegacy() {
		return cachedLegacyCheckService.isLegacy();
	}

	@Override
	public boolean isNew() {
		return cachedLegacyCheckService.isNew();
	}

	@Override
	public boolean isLegacyVillager() {
		return cachedLegacyCheckService.isLegacyVillager();
	}

	@Override
	public ItemCompost getItemComposter() {
		return this.composter;
	}

	@Override
	public PrintManager getLocalPrintManager() {
		return manager;
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
	public @NotNull Mailer getEmptyMailer() {
		return Mailer.empty(this);
	}

	@Override
	public @NotNull Mailer getEmptyMailer(CommandSender sender) {
		return Mailer.empty(sender);
	}

	@Override
	public @NotNull Mailer getEmptyMailer(Plugin plugin) {
		return Mailer.empty(plugin);
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
