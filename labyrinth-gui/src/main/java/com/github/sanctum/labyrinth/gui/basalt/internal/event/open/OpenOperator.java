package com.github.sanctum.labyrinth.gui.basalt.internal.event.open;

import com.github.sanctum.labyrinth.gui.basalt.Menu;

@FunctionalInterface
public interface OpenOperator {

    void operate(Menu menu);

}
