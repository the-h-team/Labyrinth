package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.AdvancedHook;
import com.github.sanctum.labyrinth.data.DefaultProvision;
import com.github.sanctum.labyrinth.data.EconomyProvision;
import com.github.sanctum.labyrinth.data.VaultHook;
import com.github.sanctum.labyrinth.data.container.DataContainer;
import com.github.sanctum.labyrinth.formatting.string.WrappedComponent;
import com.github.sanctum.labyrinth.library.Applicable;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.library.Cooldown;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.SkullItem;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.task.Synchronous;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class Labyrinth extends JavaPlugin implements Listener {

	public static final LinkedList<Cooldown> COOLDOWNS = new LinkedList<>();
	public static final LinkedList<WrappedComponent> COMPONENTS = new LinkedList<>();
	private static Labyrinth instance;
	public static boolean STOPPING;

	@Override
	public void onEnable() {
		instance = this;
		EconomyProvision provision = new DefaultProvision();
		Bukkit.getServicesManager().register(EconomyProvision.class, provision, this, ServicePriority.Normal);
		getLogger().info("- Registered factory implementation, " + provision.getImplementation());
		boolean success;
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		getLogger().info("Labyrinth (C) 2021, Open-source spigot development tool.");
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		try {
			DataContainer.querySaved();
			success = true;
		} catch (NullPointerException e) {
			getLogger().info("- Process ignored. No directory found to process.");
			getLogger().info("- Store a new instance of data for query to take effect on enable.");
			getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			success = false;
		}
		if (success) {
			if (DataContainer.get().length == 0) {
				getLogger().info("- Process ignored. No data found to process.");
			}
			getLogger().info("- Query success! All found meta cached. (" + DataContainer.get().length + ")");
		} else {
			getLogger().info("- Query failed! (SEE ABOVE FOR INFO)");
		}
		getLogger().info("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		Schedule.async(() -> {
			final boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
			final Material type = Items.getMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
			Arrays.stream(Bukkit.getOfflinePlayers()).forEach(p -> {
				ItemStack item = new ItemStack(type, 1);
				if (!isNew) {
					item.setDurability((short) 3);
				}
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				assert meta != null;
				meta.setOwningPlayer(p);
				item.setItemMeta(meta);

				// for some reason this is the ONLY way we can make sure the data loaded in live use matches content wise due to mojang skin updating.
				// initial use updates the item then we grab the updated item and map THAT.
				// flawless item scanning.
				Inventory fake = Bukkit.createInventory(null, 36);
				fake.setItem(0, item);
				new SkullItem(p.getUniqueId().toString(), fake.getItem(0));
			});
		}).run();
		run(() -> new VaultHook(this)).applyAfter(() -> new AdvancedHook(this)).wait(2);
		run(() -> CommandUtils.initialize(Labyrinth.this)).run();
	}

	@Override
	public void onDisable() {
		STOPPING = true;
		SkullItem.getLog().clear();
		if (Item.getCache().size() > 0) {
			for (Item i : Item.getCache()) {
				Item.removeEntry(i);
			}
		}

	}

	@EventHandler
	public void onCommandNote(PlayerCommandPreprocessEvent e) {
		for (WrappedComponent component : COMPONENTS) {
			if (StringUtils.use(e.getMessage().replace("/", "")).containsIgnoreCase(component.toString())) {
				run(() -> component.action().apply()).run();
				run(component::remove).waitReal(200);
				e.setCancelled(true);
			}
		}
	}

	public static Plugin getInstance() {
		return instance;
	}

	private Synchronous run(Applicable applicable) {
		return Schedule.sync(applicable);
	}


}
