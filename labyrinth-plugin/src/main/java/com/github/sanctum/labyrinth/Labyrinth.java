package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.api.PlaceholderFormatService;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.command.CommandRegistration;
import com.github.sanctum.labyrinth.data.AdvancedEconomyImplementation;
import com.github.sanctum.labyrinth.data.ChunkSerializable;
import com.github.sanctum.labyrinth.data.Configurable;
import com.github.sanctum.labyrinth.data.DataTable;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.FileType;
import com.github.sanctum.labyrinth.data.ItemStackSerializable;
import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.data.LegacyConfigLocation;
import com.github.sanctum.labyrinth.data.LocationSerializable;
import com.github.sanctum.labyrinth.data.MessageSerializable;
import com.github.sanctum.labyrinth.data.MetaTemplateSerializable;
import com.github.sanctum.labyrinth.data.RegionServicesManagerImpl;
import com.github.sanctum.labyrinth.data.ServiceManager;
import com.github.sanctum.labyrinth.data.ServiceType;
import com.github.sanctum.labyrinth.data.TemplateSerializable;
import com.github.sanctum.labyrinth.data.container.KeyedServiceManager;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.data.reload.PrintManager;
import com.github.sanctum.labyrinth.data.service.ExternalDataService;
import com.github.sanctum.labyrinth.data.service.LabyrinthOptions;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.LabeledAs;
import com.github.sanctum.labyrinth.event.custom.Subscribe;
import com.github.sanctum.labyrinth.event.custom.Vent;
import com.github.sanctum.labyrinth.event.custom.VentMap;
import com.github.sanctum.labyrinth.event.custom.VentMapImpl;
import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import com.github.sanctum.labyrinth.formatting.component.ActionComponent;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.interfacing.OrdinalProcedure;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Cuboid;
import com.github.sanctum.labyrinth.library.Deployable;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.ItemCompost;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.labyrinth.library.TypeFlag;
import com.github.sanctum.labyrinth.permissions.Permissions;
import com.github.sanctum.labyrinth.permissions.impl.DefaultImplementation;
import com.github.sanctum.labyrinth.permissions.impl.VaultImplementation;
import com.github.sanctum.labyrinth.placeholders.Placeholder;
import com.github.sanctum.labyrinth.placeholders.PlaceholderRegistration;
import com.github.sanctum.labyrinth.placeholders.factory.PlayerPlaceholders;
import com.github.sanctum.labyrinth.placeholders.factory.WorldPlaceholders;
import com.github.sanctum.labyrinth.task.AsynchronousTaskChain;
import com.github.sanctum.labyrinth.task.SynchronousTaskChain;
import com.github.sanctum.labyrinth.task.TaskChain;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.templates.MetaTemplate;
import com.github.sanctum.templates.Template;
import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
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
@LabeledAs("Core")
public final class Labyrinth extends JavaPlugin implements Listener, LabyrinthAPI, Message.Factory {

	private final ServiceManager serviceManager = new ServiceManager();
	private final KeyedServiceManager<Plugin> servicesManager = new KeyedServiceManager<>();
	private final LinkedList<Cooldown> cooldowns = new LinkedList<>();
	private final Map<String, ActionComponent> components = new HashMap<>();
	private final ConcurrentLinkedQueue<Integer> tasks = new ConcurrentLinkedQueue<>();
	private final Set<PersistentContainer> containers = new HashSet<>();
	private final VentMap eventMap = new VentMapImpl();
	private final ItemCompost composter = new ItemCompost();
	private final PrintManager manager = new PrintManager();
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
		registerServices().deploy();
		cachedIsLegacy = LabyrinthAPI.super.isLegacy();
		cachedIsNew = LabyrinthAPI.super.isNew();
		cachedNeedsLegacyLocation = LabyrinthAPI.super.requiresLocationLibrary();
		registerJsonAdapters().deploy();
		registerFileConfigurationAdapters().deploy();
		FileManager copy = FileList.search(this).get("config");
		InputStream stream = getResource("config.yml");
		assert stream != null;
		if (!copy.getRoot().exists()) {
			FileList.copy(stream, copy.getRoot().getParent());
		}
		this.cachedComponentRemoval = copy.read(f -> f.getInt("interactive-component-removal"));
		getEventMap().subscribeAll(this, new DefaultEvent.Controller(), this);
		getLogger().info("===================================================================");
		getLogger().info("Labyrinth; copyright Sanctum 2020, Open-source spigot development tool.");
		getLogger().info("===================================================================");

		registerImplementations().deploy();
		registerHandshake().deploy();
		registerDefaultPlaceholders().deploy();

	}

	Deployable<Labyrinth> registerServices() {
		return Deployable.of(this, instance -> {
			instance.serviceManager.load(Service.VENT);
			instance.serviceManager.load(Service.TASK);
			instance.serviceManager.load(Service.RECORDING);
			instance.serviceManager.load(Service.DATA);
			instance.serviceManager.load(Service.MESSENGER);
			instance.serviceManager.load(Service.LEGACY);
			instance.serviceManager.load(Service.COOLDOWNS);
			instance.serviceManager.load(Service.COMPONENTS);
			instance.serviceManager.load(new ServiceType<>(() -> (PlaceholderFormatService) (text, variable) -> {
				String result = text;
				if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
					if (variable instanceof OfflinePlayer) {
						result = PlaceholderAPI.setPlaceholders((OfflinePlayer) variable, text);
					}
				}
				return PlaceholderRegistration.getInstance().replaceAll(result, variable);
			}));
			instance.servicesManager.register(new DefaultImplementation(), this, ServicePriority.Low);
			CommandRegistration.use(new Command("labyrinth") {

				private final SimpleTabCompletion completion = SimpleTabCompletion.empty();
				private final TypeFlag<Player> conversion = TypeFlag.PLAYER;

				@Override
				public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
					Mailer mailer = Mailer.empty(sender).prefix().start("&2Labyrinth").middle(":").finish();

					if (args.length == 0) {
						mailer.chat("&6Currently running version &r" + Labyrinth.this.getDescription().getVersion()).deploy();
						return true;
					}

					if (args.length == 1) {
						String label = args[0];

						if (label.equalsIgnoreCase("placeholder")) {
							mailer.chat("&cInvalid usage: &6/" + commandLabel + " " + label + " <placeholder> | &8[playerName]");
							return true;
						}
						return true;
					}

					if (args.length == 2) {
						String label = args[0];
						String argument = args[1];

						if (label.equalsIgnoreCase("placeholder")) {
							if (sender instanceof Player) {
								sender.sendMessage(PlaceholderRegistration.getInstance().replaceAll(argument, conversion.cast(sender)));
							} else {
								sender.sendMessage(PlaceholderRegistration.getInstance().replaceAll(argument, sender));
							}
							return true;
						}
						return true;
					}

					if (args.length == 3) {
						String label = args[0];
						String argument = args[1];

						if (label.equalsIgnoreCase("placeholder")) {
							if (sender instanceof Player) {
								sender.sendMessage(PlaceholderRegistration.getInstance().replaceAll(argument, conversion.cast(sender)));
							} else {
								sender.sendMessage(PlaceholderRegistration.getInstance().replaceAll(argument, sender));
							}
							return true;
						}
						return true;
					}

					return false;
				}

				@NotNull
				@Override
				public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
					completion.fillArgs(args);
					completion.then(TabCompletionIndex.ONE, "placeholder");
					List<String> placeholders = new ArrayList<>();
					PlaceholderRegistration.getInstance().getHistory().entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().get())).forEach(e -> {
						for (Placeholder placeholder : e.getValue()) {
							placeholders.add(placeholder.toRaw().replace(String.valueOf(placeholder.start()), placeholder.start() + e.getKey().get()));
						}
					});
					completion.then(TabCompletionIndex.TWO, "placeholder", TabCompletionIndex.ONE, placeholders);
					completion.then(TabCompletionIndex.THREE, "placeholder", TabCompletionIndex.ONE, Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()));

					return completion.get();
				}
			});
		});
	}

	Deployable<Labyrinth> registerImplementations() {
		return Deployable.of(this, plugin -> {
			TaskScheduler.of(() -> new AdvancedEconomyImplementation(plugin)).scheduleLater(165)
					.next(() -> new com.github.sanctum.labyrinth.data.VaultImplementation(plugin)).scheduleLater(165)
					.next(() -> CommandUtils.initialize(plugin)).schedule()
					.next(() -> {
						if (getServer().getPluginManager().isPluginEnabled("Vault")) {
							VaultImplementation bridge = new VaultImplementation();
							getServicesManager().register(bridge, bridge.getProvider(), ServicePriority.Normal);
						}
						Permissions instance = getServicesManager().load(Permissions.class);
						if (instance.getProvider().equals(plugin)) {
							plugin.getLogger().info("- Using default labyrinth implementation for permissions (No provider).");
						} else {
							if (instance instanceof VaultImplementation) {
								plugin.getLogger().info("- Using " + instance.getProvider().getName() + " for permissions. (Vault)");
							} else {
								plugin.getLogger().info("- Using " + instance.getProvider().getName() + " for permissions. (Provider)");
							}
						}
					}).scheduleLater(40);

			if (requiresLocationLibrary()) {
				ConfigurationSerialization.registerClass(LegacyConfigLocation.class);
			}

			if (LabyrinthOptions.IMPL_REGION_SERVICES.enabled()) {
				RegionServicesManagerImpl.initialize(plugin);
			}
		});
	}

	Deployable<Labyrinth> registerHandshake() {
		return Deployable.of(this, plugin -> {
			ExternalDataService.Handshake handshake = new ExternalDataService.Handshake(plugin);

			TaskScheduler.of(handshake::locate).schedule().next(handshake::register).scheduleLater(1);
		});
	}

	Deployable<Labyrinth> registerDefaultPlaceholders() {
		return Deployable.of(this, plugin -> {
			PlaceholderRegistration registration = PlaceholderRegistration.getInstance();

			registration.registerTranslation(new PlayerPlaceholders()).deploy();
			registration.registerTranslation(new WorldPlaceholders()).deploy();
		});
	}

	Deployable<Labyrinth> registerJsonAdapters() {
		return Deployable.of(this, instance -> {
			Configurable.registerClass(ItemStackSerializable.class);
			Configurable.registerClass(LocationSerializable.class);
			Configurable.registerClass(TemplateSerializable.class);
			Configurable.registerClass(MetaTemplateSerializable.class);
			Configurable.registerClass(MessageSerializable.class);
			Configurable.registerClass(ChunkSerializable.class);
			Configurable.registerClass(CustomColor.class);
		});
	}

	Deployable<Labyrinth> registerFileConfigurationAdapters() {
		return Deployable.of(this, instance -> {
			ConfigurationSerialization.registerClass(CustomColor.class);
			ConfigurationSerialization.registerClass(Template.class);
			ConfigurationSerialization.registerClass(MetaTemplate.class);
		});
	}


	@Override
	public void onDisable() {

		for (int id : tasks) {
			getServer().getScheduler().cancelTask(id);
		}

		getScheduler(ASYNCHRONOUS).purge();
		getScheduler(SYNCHRONOUS).purge();

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

	@Subscribe(priority = Vent.Priority.READ_ONLY)
	public void onJoin(DefaultEvent.Join e) {
		LabyrinthUser user = LabyrinthUser.get(e.getPlayer().getName());
		if (user.isValid()) {
			if (OrdinalProcedure.select(user, 4, e.getPlayer()).cast(() -> Boolean.class)) {
				getEmptyMailer().info("- User " + e.getPlayer().getName() + "'s id has been updated.");
			}
		} else {
			getEmptyMailer().error("- User " + e.getPlayer().getName() + " has NO unique id!! (This is not the fault of labyrinth, perhaps cracked problems)");
		}

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
		return Collections.unmodifiableList(new ArrayList<>(components.values()));
	}

	@Override
	public Deployable<Void> registerComponent(ActionComponent component) {
		return Deployable.of(null, unused -> this.components.put(component.getId(), component));
	}

	@Override
	public Deployable<Void> removeComponent(ActionComponent component) {
		return Deployable.of(null, unused -> this.components.remove(component.getId()));
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
	@NotNull
	public com.github.sanctum.labyrinth.library.Message getNewMessage() {
		return com.github.sanctum.labyrinth.library.Message.loggedFor(this);
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
