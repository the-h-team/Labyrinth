package com.github.sanctum.labyrinth.unity.impl.inventory;


import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.labyrinth.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.unity.construct.Menu;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class SharedInventory extends InventoryElement {

	private final Set<Player> viewers;

	public SharedInventory(String title, Menu menu) {
		super(title, menu, true);
		this.viewers = new HashSet<>();
	}

	public Set<Player> getViewers() {
		return viewers;
	}

	@Override
	public void open(Player player) {
		viewers.add(player);
		super.open(player);
		for (Player p : viewers) {
			if (getElement() != null) {
				if (!p.getOpenInventory().getTopInventory().equals(getElement())) {
					Schedule.sync(() -> viewers.remove(p)).wait(1);
				}
			}
		}
	}
}
