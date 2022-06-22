package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.SimpleKeyedValue;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import org.bukkit.entity.Player;

public interface Workbench {

	Workbench put(WorkbenchSlot slot, char symbol);

	default Workbench put(char symbol, WorkbenchSlot... slots) {
		for (WorkbenchSlot slot : slots) {
			put(slot, symbol);
		}
		return this;
	}

	LabyrinthCollection<SimpleKeyedValue<WorkbenchSlot, Character>> get();

	static void open(Player player) {
		player.openWorkbench(null, true);
	}

}
