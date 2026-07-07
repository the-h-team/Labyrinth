package com.github.sanctum.labyrinth.gui.basalt.internal.event.close;

import com.github.sanctum.labyrinth.gui.basalt.Menu;

@FunctionalInterface
public interface CloseOperator {

    void operate(Menu menu);

}
