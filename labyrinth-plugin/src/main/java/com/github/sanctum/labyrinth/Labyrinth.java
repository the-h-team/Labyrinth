package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.AdvancedHook;
import com.github.sanctum.labyrinth.data.DefaultProvision;
import com.github.sanctum.labyrinth.data.EconomyProvision;
import com.github.sanctum.labyrinth.data.RegionServicesManager;
import com.github.sanctum.labyrinth.data.VaultHook;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.data.service.ServiceHandshake;
import com.github.sanctum.labyrinth.event.EasyListener;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.VentMap;
import com.github.sanctum.labyrinth.formatting.component.WrappedComponent;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
public final class Labyrinth extends JavaPlugin implements Listener {

	private static final LinkedList<Cooldown> COOLDOWNS = new LinkedList<>();
	private static final LinkedList<WrappedComponent> COMPONENTS = new LinkedList<>();
	private static final ConcurrentLinkedQueue<Integer> TASKS = new ConcurrentLinkedQueue<>();
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
		Schedule.sync(() -> new AdvancedHook(this)).applyAfter(() -> new VaultHook(this)).wait(2);
		Schedule.sync(() -> CommandUtils.initialize(Labyrinth.this)).run();

		RegionServicesManager.Initializer.start(this);

		Schedule.sync(ServiceHandshake::locate).applyAfter(ServiceHandshake::register).run();

	}

	@Override
	public void onDisable() {

		for (PersistentContainer component : CONTAINERS) {
			for (String key : component.keySet()) {
				try {
					component.save(key);
				} catch (IOException e) {
					getLogger().severe("- Unable to save meta '" + key + "' from namespace " + component.getKey().getNamespace() + ":" + component.getKey().getKey());
					e.printStackTrace();
				}
			}
		}

		for (int id : TASKS) {
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

	public VentMap getEventMap() {
		return eventMap;
	}

	/**
	 * Get a queued list of running task id's
	 *
	 * @return A list of most running task id's
	 */
	public static ConcurrentLinkedQueue<Integer> getTasks() {
		return TASKS;
	}

	/**
	 * Get all pre-cached cooldowns.
	 *
	 * @return A list of all cached cooldowns.
	 */
	public static LinkedList<Cooldown> getCooldowns() {
		return COOLDOWNS;
	}

	/**
	 * Get all action wrapped text components.
	 *
	 * @return A list of all cached text components.
	 */
	public static LinkedList<WrappedComponent> getComponents() {
		return COMPONENTS;
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
	 * @return true if the given server version is legacy only or false if its pro-1.13
	 */
	public static boolean isLegacy() {
		return Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9")
				|| Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")
				|| Bukkit.getVersion().contains("1.12") || Bukkit.getVersion().contains("1.13");
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
			if (HUID.fromString(e.getMessage().replace("/", "")) != null) {
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
				e.setCancelled(true);
				return;
			}
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
