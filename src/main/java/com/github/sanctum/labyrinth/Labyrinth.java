package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.data.AdvancedHook;
import com.github.sanctum.labyrinth.data.Config;
import com.github.sanctum.labyrinth.data.DefaultProvision;
import com.github.sanctum.labyrinth.data.EconomyProvision;
import com.github.sanctum.labyrinth.data.VaultHook;
import com.github.sanctum.labyrinth.data.container.DataContainer;
import com.github.sanctum.labyrinth.gui.GuiLibrary;
import com.github.sanctum.labyrinth.gui.Menu;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.SkullItem;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class Labyrinth extends JavaPlugin implements Listener {


    private static Labyrinth instance;

    private static final HashMap<Player, GuiLibrary> guiManager = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getServicesManager().register(EconomyProvision.class, new DefaultProvision(), this, ServicePriority.Normal);
        run();
        Config main = new Config("Config", "Configuration");
        if (main.getConfig().getBoolean("use-click-event")) {
            Bukkit.getPluginManager().registerEvents(this, this);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> new VaultHook(this), 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> new AdvancedHook(this), 5);
        boolean success;
        try {
            DataContainer.querySaved();
            success = true;
        } catch (NullPointerException e) {
            getLogger().info("- Process failed. No directory found to process.");
            getLogger().info("- Store a new instance of data for query to take effect on enable.");
            success = false;
        }
        if (success) {
            if (DataContainer.get().length == 0) {
                success = false;
                getLogger().info("- Process failed. No data found to process.");
            }
            getLogger().info("- Query success! All found meta cached. (" + DataContainer.get().length + ")");
        } else {
            getLogger().info("- Query failed! (SEE ABOVE FOR INFO)");
        }

        boolean isNew = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {

            Material type = Material.matchMaterial(isNew ? "PLAYER_HEAD" : "SKULL_ITEM");
            ItemStack item = new ItemStack(type, 1);

            if (!isNew) {
                item.setDurability((short) 3);
            }

            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (!meta.hasOwner()) {
                meta.setOwningPlayer(p);
            }
            item.setItemMeta(meta);
            new SkullItem(p.getUniqueId().toString(), item);
        }
    }

    @Override
    public void onDisable() {
        guiManager.clear();

        if (Item.getCache().size() > 0) {
            for (Item i : Item.getCache()) {
                Item.removeEntry(i);
            }
        }

    }

    public static Labyrinth getInstance() {
        return instance;
    }

    public static GuiLibrary guiManager(Player p) {
        GuiLibrary gui;
        if (!(guiManager.containsKey(p))) {


            gui = new GuiLibrary(p);
            guiManager.put(p, gui);

            return gui;
        } else {
            return guiManager.get(p);
        }
    }

    private void run() {
        Config main = new Config("Config", "Configuration");
        if (!main.exists()) {
            InputStream is = getResource("Config.yml");
            Config.copy(is, main.getFile());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMenuClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        try {
            if (holder instanceof Menu) {
                e.setCancelled(true);
                if (e.getCurrentItem() == null) {
                    return;
                }
                Menu menu = (Menu) holder;
                menu.handleMenu(e);
            }
        } catch (AuthorNagException ignored) {
        }
    }


}
