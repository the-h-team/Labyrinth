package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.data.AdvancedHook;
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
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
public final class Labyrinth extends JavaPlugin implements Listener, LabyrinthAPI {

	private static Labyrinth instance;
	private final LinkedList<Cooldown> cooldowns = new LinkedList<>();
	private final LinkedList<WrappedComponent> components = new LinkedList<>();
	private final ConcurrentLinkedQueue<Integer> tasks = new ConcurrentLinkedQueue<>();
	private final List<PersistentContainer> containers = new LinkedList<>();
	private final VentMap eventMap = new VentMap();
	private boolean cachedIsLegacy;

	@Override
	public void onEnable() {

		instance = this;
		LabyrinthProvider.instance = this;

		cachedIsLegacy = LabyrinthAPI.super.isLegacy();

		new EasyListener(DefaultEvent.Controller.class).call(this);

		new EasyListener(ComponentListener.class).call(this);

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
		return containers.stream().filter(c -> c.getKey().getNamespace().equalsIgnoreCase(plugin.getName())).collect(Collectors.toList());
	}

	@Override
	public @NotNull PersistentContainer getContainer(NamespacedKey key) {
		for (PersistentContainer component : containers) {
			if (component.getKey().getNamespace().equals(key.getNamespace()) && component.getKey().getKey().equals(key.getKey())) {
				return component;
			}
		}
		PersistentContainer component = new PersistentContainer(key);
		containers.add(component);
		return component;
	}

	@Override
	public boolean isLegacy() {
		return cachedIsLegacy;
	}

	@Override
	public Plugin getPluginInstance() {
		return this;
	}

	public static class ComponentListener implements Listener {
		@EventHandler
		public void onCommandNote(PlayerCommandPreprocessEvent e) {
			if (HUID.fromString(e.getMessage().replace("/", "")) != null) {
				if (instance.components.stream().noneMatch(c -> c.toString().equals(e.getMessage().replace("/", "")))) {
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
					e.setCancelled(true);
					return;
				}
			}
			for (WrappedComponent component : instance.components) {
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
