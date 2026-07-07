package com.github.sanctum.labyrinth.gui.basalt.internal;

import com.github.sanctum.labyrinth.gui.basalt.InventoryFormat;
import com.github.sanctum.labyrinth.gui.basalt.InventoryProperties;
import com.github.sanctum.labyrinth.gui.basalt.InventorySize;
import com.github.sanctum.labyrinth.gui.basalt.Menu;
import com.github.sanctum.panther.util.Check;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SimpleBuilder extends Menu.Builder<Menu.Simple> {

    private String title, id;
    private boolean shareable;
    private InventorySize size;

    public SimpleBuilder setTitle(@NotNull String title) {
        this.title = title;
        return this;
    }

    public SimpleBuilder setSize(@NotNull InventorySize size) {
        this.size = size;
        return this;
    }

    public SimpleBuilder setShareable(boolean shareable) {
        this.shareable = shareable;
        return this;
    }

    public SimpleBuilder setId(@NotNull String id) {
        this.id = id;
        return this;
    }

    @Override
    public Menu.Simple deploy() {
        Optional<Menu> test = Menu.FACTORY.get(id);
        if (test.isPresent()) return (Menu.Simple) test.get();
        InventoryProperties newProperties = new InventoryProperties(InventoryFormat.NORMAL, Check.forNull(size, "Inventory size cannot be null!"), Check.forNull(title, "Inventory title cannot be null! Use empty space instead if needed."), Check.forNull(id, "Menu id cannot be null"), shareable);
        return new Menu.Simple(newProperties);
    }
}
