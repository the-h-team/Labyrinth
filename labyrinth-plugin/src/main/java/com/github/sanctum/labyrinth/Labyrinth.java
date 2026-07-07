package com.github.sanctum.labyrinth;

import com.github.sanctum.labyrinth.api.LabyrinthAPI;
import com.github.sanctum.labyrinth.command.LabyrinthCommand;
import com.github.sanctum.labyrinth.command.LabyrinthCommandToken;
import com.github.sanctum.labyrinth.common.*;
import com.github.sanctum.labyrinth.data.*;
import com.github.sanctum.labyrinth.data.container.MemoryBank;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.data.reload.PrintManager;
import com.github.sanctum.labyrinth.data.service.*;
import com.github.sanctum.labyrinth.event.DefaultEvent;
import com.github.sanctum.labyrinth.event.EnableAfterEvent;
import com.github.sanctum.labyrinth.event.LabyrinthVentCall;
import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.formatting.component.ActionComponent;
import com.github.sanctum.labyrinth.formatting.string.CustomColor;
import com.github.sanctum.labyrinth.gui.basalt.InventoryContainer;
import com.github.sanctum.labyrinth.gui.basalt.InventorySize;
import com.github.sanctum.labyrinth.gui.basalt.InventorySnapshot;
import com.github.sanctum.labyrinth.gui.basalt.Menu;
import com.github.sanctum.labyrinth.gui.basalt.internal.InventoryModificationRequest;
import com.github.sanctum.labyrinth.gui.basalt.internal.SimpleBuilder;
import com.github.sanctum.labyrinth.gui.basalt.internal.event.action.ActionProcessor;
import com.github.sanctum.labyrinth.gui.basalt.internal.event.action.ActionType;
import com.github.sanctum.labyrinth.interfacing.Token;
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
import com.github.sanctum.panther.util.TaskChain;
import com.github.sanctum.templates.MetaTemplate;
import com.github.sanctum.templates.Template;
import com.google.common.collect.ImmutableList;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

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
public final class Labyrinth extends JavaPlugin implements Vent.Host, Listener, LabyrinthAPI, Message.Factory, VentService, TaskService, RecordService, MessagingService, CooldownService, ActionComponentService, PersistentDataService, LegacyCheckService {

    private final ServiceManager serviceManager = new ServiceManager();
    private final MemoryBank<Plugin> servicesManager = new MemoryBank<>();
    private final LinkedList<Cooldown> cooldowns = new LinkedList<>();
    private final Map<String, ActionComponent> components = new HashMap<>();
    private final ConcurrentLinkedQueue<Integer> tasks = new ConcurrentLinkedQueue<>();
    private final Set<PersistentContainer> containers = new HashSet<>();
    private final ItemCompost composter = new ItemCompost();
    private final PrintManager manager = new PrintManager();
    private final SynchronousTaskChain syncChain = new SynchronousTaskChain(this);
    private Token<Labyrinth> validCommandToken;
    private boolean cachedIsLegacy;
    private boolean cachedIsNew;
    private boolean cachedNeedsLegacyLocation;
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
        // raw data can be manipulated but things like player data and world data are not yet loaded and will result in null when fetching, only use this space for finite pre-processing.
    }

    @Override
    public void onEnable() {
        this.time = System.currentTimeMillis();
        LabyrinthProvider.instance = this;
        getLogger().info("- Copyright Team Sanctum 2020, Open-source spigot development tool.");
        getLogger().info("- Boasting ease of use utilities from legacy to the latest of versions making your life easier..");
        getLogger().info("- Pre-Loading User Wrappers, please be patient...");
        PlayerInfo.reload().deploy();
        registerServices().deploy();
        registerJsonAdapters().deploy();
        registerYamlAdapters().deploy();
        // 1.12 and prior there is no command send event.
        boolean found = true;
        try {
            Class.forName("org.bukkit.event.player.PlayerCommandSendEvent");
        } catch (ClassNotFoundException e) {
            found = false;
        }
        if (found) {
            getVentMap().subscribeAll(this, new CommandVisibilityListener());
        }
        getVentMap().subscribeAll(this, new DefaultEvent.Controller(), this);
        registerImplementations().deploy();
        registerHandshake().deploy();
        registerDefaultPlaceholders().deploy();
        Plugin[] list = Bukkit.getPluginManager().getPlugins();
        TaskScheduler.of(() -> new LabyrinthVentCall<>(new EnableAfterEvent(Arrays.stream(list).filter(Plugin::isEnabled).map(Plugin::getName).toArray(String[]::new))).run()).scheduleLater("labyrinth;enable-after-ever", (Math.min(40L + list.length, 120))); // the bigger the plugin list the longer it tries to wait
    }

    @Subscribe
    public void onJoin2(DefaultEvent.Join join) {
        Player p = join.getPlayer();
        // can still make it animated with simple instructions!!!
        Menu menu = Menu.FACTORY.get("test").orElse(new SimpleBuilder().setId("test").setShareable(true).setSize(InventorySize.FIVE).setTitle("&6Test inventory").deploy());
        InventoryContainer container = menu.getContainer();
        ActionProcessor processor = (inventory, item, type) -> {
            if (type == ActionType.LEFT_CLICK) {

                return false;
            } else return true;
        };
        // numerous change requets to simulate snapshot slides for animation needs.
        InventoryModificationRequest request1 = c -> {
            final InventorySnapshot snapshot = c.newDuplicate();
            if (snapshot != null) {
                snapshot.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aH").build();
                snapshot.set(0, stack);
                // repeating animation logic
            }
            c.update();
        };
        InventoryModificationRequest request2 = c -> {
            final InventorySnapshot snapshot2 = c.newDuplicate();
            if (snapshot2 != null) {
                snapshot2.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aH&ee").build();
                snapshot2.set(0, stack);
            }
            c.update();
        };
        InventoryModificationRequest request3 = c -> {
            final InventorySnapshot snapshot3 = c.newDuplicate();
            if (snapshot3 != null) {
                snapshot3.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aHe&el").build();
                snapshot3.set(0, stack);
            }
            c.update();
        };
        InventoryModificationRequest request4 = c -> {
            final InventorySnapshot snapshot4 = c.newDuplicate();
            if (snapshot4 != null) {
                snapshot4.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aHel&el").build();
                snapshot4.set(0, stack);
            }
            c.update();
        };
        InventoryModificationRequest request5 = c -> {
            final InventorySnapshot snapshot5 = c.newDuplicate();
            if (snapshot5 != null) {
                snapshot5.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aHell&el").build();
                snapshot5.set(0, stack);
            }
            c.update();
        };
        InventoryModificationRequest request6 = c -> {
            final InventorySnapshot snapshot6 = c.newDuplicate();
            if (snapshot6 != null) {
                snapshot6.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aHelll&el&ao").build();
                snapshot6.set(0, stack);
            }
            c.update();
        };
        InventoryModificationRequest request7 = c -> {
            final InventorySnapshot snapshot7 = c.newDuplicate();
            if (snapshot7 != null) {
                snapshot7.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aHell&el").build();
                snapshot7.set(0, stack);
            }
            c.update();
        };
        InventoryModificationRequest request8 = c -> {
            final InventorySnapshot snapshot8 = c.newDuplicate();
            if (snapshot8 != null) {
                snapshot8.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aHel&el").build();
                snapshot8.set(0, stack);
            }
            c.update();
        };
        InventoryModificationRequest request9 = c -> {
            final InventorySnapshot snapshot9 = c.newDuplicate();
            if (snapshot9 != null) {
                snapshot9.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aHe&el").build();
                snapshot9.set(0, stack);
            }
            c.update();
        };
        InventoryModificationRequest request10 = c -> {
            final InventorySnapshot snapshot10 = c.newDuplicate();
            if (snapshot10 != null) {
                snapshot10.wipe();
                ItemStack stack = new CustomItemRecipe.Edit(Material.ACACIA_BUTTON).setTitle("&aH&ee").build();
                snapshot10.set(0, stack);
            }
            c.update();
        };
        // within one "inventory change" were cycling a bunch of snapshot updates, this is our pattern we could design a tool for this later.
        container.change(request1);
        container.change(request2);
        container.change(request3);
        container.change(request4);
        container.change(request5);
        container.change(request6);
        container.change(request7);
        container.change(request8);
        container.change(request9);
        container.change(request10);
        menu.open(p);
    }

    @Subscribe
    public void onEnableAfter(EnableAfterEvent e) {
        // Load economy
        new com.github.sanctum.labyrinth.data.VaultImplementation(this);

        // Load permission
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            VaultImplementation bridge = new VaultImplementation();
            getMemoryBank().register(bridge, bridge.getProvider(), ServicePriority.Normal);
        }
        Permissions instance = getMemoryBank().load(Permissions.class);
        assert instance != null;
        // we know it's not null because of the default implementation.
        if (instance.getProvider().equals(this)) {
            getLogger().info("- Using default labyrinth implementation for permissions (No provider).");
        } else {
            if (instance instanceof VaultImplementation) {
                getLogger().info("- Vault permission manager found. Now using: " + instance.getProvider().getName());
            } else {
                getLogger().info("- Custom permission manager found. Now using: " + instance.getProvider().getName());
            }
        }
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
            cachedIsLegacy = this.isLegacy();
            cachedIsNew = this.isNew();
            cachedNeedsLegacyLocation = this.isLegacyVillager();
            return this;
        }, 0);
    }

    Deployable<Labyrinth> registerImplementations() {
        return Deployable.of(() -> {
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
            this.cachedComponentRemoval = copy.read(f -> (Integer) f.getInt("interactive-component-removal"));
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
            for (CustomItemRecipe i : CustomItemRecipe.getRegistered()) {
                CustomItemRecipe.removeEntry(i);
            }
        }
    }

    @Subscribe(priority = Vent.Priority.LOW)
    public void onJoin(DefaultEvent.Join e) {
        PlayerInfo.of(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onComponent(PlayerCommandPreprocessEvent e) {
        String label = e.getMessage().split(" ")[0].replace("/", "");


        if (label.equalsIgnoreCase("testing")) {
            LabyrinthPlayer user = PlayerInfo.of(e.getPlayer());
            int x = new Random().nextInt(80124), y = new Random().nextInt(120), z = new Random().nextInt(80124);
            Teleport test = new SimpleTeleport(user, new Teleport.Location(new Location(e.getPlayer().getWorld(), x, y, z)));
            test.run();
        }
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
    public VentMap getVentMap() {
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
            if (library.read(f -> (Boolean) f.isNode("Library." + id))) {

                long time = library.read(f -> (Long) f.getLong("Library." + id + ".expiration"));
                String s = library.read(f -> f.getString("Library." + id + ".descriptor"));
                long a = time;
                long b = System.currentTimeMillis();
                int compareNum = Long.compare(a, b);
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
    public List<PersistentContainer> getPersistentContainers(Plugin plugin) {
        return ImmutableList.copyOf(containers.stream().filter(p -> p.getKey().getNamespace().equals(plugin.getName().toLowerCase(Locale.ROOT))).collect(Collectors.toSet()));
    }

    @Override
    @NotNull
    public PersistentContainer getPersistentContainer(@NotNull NamespacedKey namespacedKey) {
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
    public boolean isLegacyVillager() {
        return cachedNeedsLegacyLocation;
    }

    @Override
    public ItemCompost getItemComposter() {
        return this.composter;
    }

    @Override
    public PrintManager getPrintManager() {
        return manager;
    }

    @Override
    public MemoryBank<Plugin> getMemoryBank() {
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
    public @NotNull MessageComposer getEmptyMailer() {
        return MessageComposer.empty(this);
    }

    @Override
    public @NotNull MessageComposer getEmptyMailer(CommandSender sender) {
        return MessageComposer.empty(sender);
    }

    @Override
    public @NotNull MessageComposer getEmptyMailer(Plugin plugin) {
        return MessageComposer.empty(plugin);
    }

    @Override
    @NotNull
    public StopWatch.Recording getTimeActive() {
        return StopWatch.Recording.subtract(this.time);
    }

    @Override
    @NotNull
    public StopWatch.Recording getTimeFrom(Date date) {
        return getTimeFrom(date.getTime());
    }

    @Override
    @NotNull
    public StopWatch.Recording getTimeFrom(long l) {
        return StopWatch.Recording.subtract(l);
    }


}
