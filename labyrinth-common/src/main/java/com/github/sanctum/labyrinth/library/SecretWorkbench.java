package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.SimpleKeyedValue;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthCollectors;
import com.github.sanctum.labyrinth.data.container.LabyrinthEntryMap;
import com.github.sanctum.labyrinth.data.container.LabyrinthMap;

final class SecretWorkbench implements Workbench {

	final LabyrinthMap<WorkbenchSlot, Character> map = new LabyrinthEntryMap<>(9);

	public SecretWorkbench put(WorkbenchSlot slot, char symbol) {
		map.put(slot, symbol);
		return this;
	}

	@Override
	public LabyrinthCollection<SimpleKeyedValue<WorkbenchSlot, Character>> get() {
		return map.stream().sorted((o1, o2) -> Integer.compare(o2.getKey().toInt(), o1.getKey().toInt())).collect(LabyrinthCollectors.toSet());
	}

}
