package com.github.sanctum.labyrinth.gui.basalt;

import com.github.sanctum.labyrinth.interfacing.Duplication;
import com.github.sanctum.labyrinth.interfacing.Snapshot;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.util.Check;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class Menu implements Duplication<InventoryContainer> {

    // TODO: make menu builder types for easy creation flow Menu.FACTORY.SIMPLE.setTitle("&5Test").setContents(1, Item.Craft).build(); Menu.FACTORY.get("idhere");
    public static Menu.Factory<?> FACTORY = new Factory<Object>() {
    };

    private final InventoryProperties properties;
    private final String id;
    private InventoryContainer container, localSnapshot;

    // TODO: if inventories are non sharable each player will get mapped their own cached instance. inventory gets cached either way. maybe decide some fun terminate logic if the menu hasnt been used for some time (run cleanup timer)
    // TODO;; setup underlying logic for repeating timer that updates item processing (instead of piling it up at execution)
    public Menu(@NotNull InventoryProperties properties) {
        this.properties = properties;
        this.id = properties.getId();
        this.container = new InventoryContainer(properties) {
        };
        FACTORY.MENUS.put(id, this);
    }

    @Override
    public void inform(@NotNull Snapshot snapshot) {
        if (snapshot instanceof InventoryContainer) {
            this.localSnapshot = (InventoryContainer) snapshot;
        }
    }

    // you cant have duplicate menu's but you can have duplicate containers simulating such!
    @Override
    public InventoryContainer newDuplicate() {
        return this.localSnapshot = new InventoryContainer(container.getInventory(), container.getProperties()) {
        };
    }

    public @NotNull String getId() {
        return id;
    }

    public InventoryContainer getContainer() {
        if (getProperties().isShareable()) {
            // cached instance
            return container;
        }
        return container = new InventoryContainer(this.properties) {
        }; // fresh instance build inventory
    }

    public void open(Player player) {
        Inventory inventory = getContainer().getInventory();
        if (Check.isNull(inventory)) return;
        player.openInventory(inventory);
    }

    public void delete() {
        FACTORY.MENUS.remove(getId());
    }

    public boolean update() {
        if (this.localSnapshot != null) {
            container.inventoryModificationTask.getTask().cancel();
            this.container = this.localSnapshot;
            this.localSnapshot = null;
            return true;
        }
        return container != null && container.update();
    }

    public @NotNull InventoryProperties getProperties() {
        return properties;
    }

    public static Menu getInstance(@NotNull String id) {
        return FACTORY.MENUS.get(id);
    }

    public static class Simple extends Menu {

        public Simple(@NotNull InventoryProperties properties) {
            super(properties);
        }

        @Override
        public void open(Player player) {

            // run check for animated to do special per menu task. InventoryFrames work sep from ModificationRequests

            if (getProperties().isLivemeta())

            super.open(player);
        }
    }

    public static class Paginated extends Menu {

        public Paginated(@NotNull InventoryProperties properties) {
            super(properties);
        }
    }

    public static class Anvil extends Menu {

        public Anvil(@NotNull InventoryProperties properties) {
            super(properties);
        }
    }

    public abstract static class Factory<T> {
        private final PantherMap<String, Menu> MENUS = new PantherEntryMap<>();

        public @NotNull Optional<Menu> get(@NotNull String id) {
            return Optional.ofNullable(MENUS.get(id));
        }

    }

    public abstract static class Builder<T extends Menu> {

        public abstract T deploy();

    }

}
