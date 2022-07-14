package com.github.sanctum.labyrinth.library;

import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherCollectors;
import com.github.sanctum.panther.container.PantherEntry;
import com.github.sanctum.panther.container.PantherEntryMap;
import com.github.sanctum.panther.container.PantherMap;

final class SecretWorkbench implements Workbench {

	final PantherMap<WorkbenchSlot, Character> map = new PantherEntryMap<>(9);

	public SecretWorkbench put(WorkbenchSlot slot, char symbol) {
		map.put(slot, symbol);
		return this;
	}

	@Override
	public PantherCollection<PantherEntry.Modifiable<WorkbenchSlot, Character>> get() {
		return map.stream().sorted((o1, o2) -> Integer.compare(o2.getKey().toInt(), o1.getKey().toInt())).collect(PantherCollectors.toSet());
	}

}
