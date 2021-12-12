package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.SimpleKeyedValue;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;

public interface Workbench {

	Workbench put(WorkbenchSlot slot, char symbol);

	default Workbench put(char symbol, WorkbenchSlot... slots) {
		for (WorkbenchSlot slot : slots) {
			put(slot, symbol);
		}
		return this;
	}

	LabyrinthCollection<SimpleKeyedValue<WorkbenchSlot, Character>> get();

}
