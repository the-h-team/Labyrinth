package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.AdvancedHook;
import com.github.sanctum.labyrinth.data.DefaultProvision;
import com.github.sanctum.labyrinth.data.EconomyProvision;
import com.github.sanctum.labyrinth.data.RegionServicesManager;
import com.github.sanctum.labyrinth.data.VaultHook;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.event.EasyListener;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.VentMap;
import com.github.sanctum.labyrinth.formatting.component.WrappedComponent;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.SkullItem;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * ▄▄▌***▄▄▄·*▄▄▄▄·**▄·*▄▌▄▄▄**▪***▐*▄*▄▄▄▄▄*▄*.▄
 * ██•**▐█*▀█*▐█*▀█▪▐█▪██▌▀▄*█·██*•█▌▐█•██**██▪▐█
 * ██▪**▄█▀▀█*▐█▀▀█▄▐█▌▐█▪▐▀▀▄*▐█·▐█▐▐▌*▐█.▪██▀▐█
 * ▐█▌▐▌▐█*▪▐▌██▄▪▐█*▐█▀·.▐█•█▌▐█▌██▐█▌*▐█▌·██▌▐▀
 * .▀▀▀**▀**▀*·▀▀▀▀***▀*•*.▀**▀▀▀▀▀▀*█▪*▀▀▀*▀▀▀*·
 * Copyright: Sanctum 2021.
 */
public final class Labyrinth extends JavaPlugin implements Listener {

	public static final LinkedList<Cooldown> COOLDOWNS = new LinkedList<>();
	public static final LinkedList<WrappedComponent> COMPONENTS = new LinkedList<>();
	public static final ConcurrentLinkedQueue<Integer> TASKS = new ConcurrentLinkedQueue<>();
	private static final List<PersistentContainer> CONTAINERS = new LinkedList<>();
	private static Labyrinth INSTANCE;
	private VentMap eventMap;

	@Override
	public void onEnable() {

		INSTANCE = this;

		eventMap = new VentMap();

		new EasyListener(DefaultEvent.Controller.class).call(this);

		new EasyListener(ComponentListener.class).call(this);

		EconomyProvision provision = new DefaultProvision();
		Bukkit.getServicesManager().register(EconomyProvision.class, provision, this, ServicePriority.Normal);

		getLogger().info("- Registered factory economy impl, " + provision.getImplementation());
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		getLogger().info("Labyrinth; copyright Sanctum 2021, Open-source spigot development tool.");
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		Schedule.async(() -> Arrays.stream(Bukkit.getOfflinePlayers()).forEach(SkullItem.Head::search)).run();
		Schedule.sync(() -> new AdvancedHook(this)).applyAfter(() -> new VaultHook(this)).wait(2);
		Schedule.sync(() -> CommandUtils.initialize(Labyrinth.this)).run();

		RegionServicesManager.Initializer.start(this);

	}

	@Override
	public void onDisable() {

		for (PersistentContainer component : CONTAINERS) {
			for (String key : component.keySet()) {
				component.save(key);
			}
		}

		for (int id : TASKS) {
			getServer().getScheduler().cancelTask(id);
		}
		try {
			Thread.sleep(1);
		} catch (InterruptedException ignored) {
		}
		SkullItem.getLog().clear();
		if (Item.getCache().size() > 0) {
			for (Item i : Item.getCache()) {
				Item.removeEntry(i);
			}
		}

	}

	public VentMap getEventMap() {
		return eventMap;
	}

	/**
	 * @return All data containers linked to the specified plugin.
	 * <p>
	 * Containers will have had to have been initialized <strong>ATLEAST</strong> once prior to viewing.
	 */
	public static List<PersistentContainer> getContainers(Plugin plugin) {
		return CONTAINERS.stream().filter(c -> c.getKey().getNamespace().equalsIgnoreCase(plugin.getName())).collect(Collectors.toList());
	}

	/**
	 * Operate on a custom persistent data container using a specified name space.
	 *
	 * @param key The name space for this component.
	 * @return The desired data container or a new instance using the defined name space.
	 */
	@NotNull
	public static PersistentContainer getContainer(NamespacedKey key) {
		for (PersistentContainer component : CONTAINERS) {
			if (component.getKey().getNamespace().equals(key.getNamespace()) && component.getKey().getKey().equals(key.getKey())) {
				return component;
			}
		}
		PersistentContainer component = new PersistentContainer(key);
		CONTAINERS.add(component);
		return component;
	}

	/**
	 * Auxiliary <strong>library</strong> instance.
	 */
	public static Plugin getInstance() {
		return INSTANCE;
	}


	public static class ComponentListener implements Listener {
		@EventHandler
		public void onCommandNote(PlayerCommandPreprocessEvent e) {
			for (WrappedComponent component : COMPONENTS) {
				if (StringUtils.use(e.getMessage().replace("/", "")).containsIgnoreCase(component.toString())) {
					Schedule.sync(() -> component.action().apply()).run();
					if (!component.isMarked()) {
						component.setMarked(true);
						Schedule.sync(component::remove).waitReal(200);
					}
					e.setCancelled(true);
				}
			}
		}
	}


}
