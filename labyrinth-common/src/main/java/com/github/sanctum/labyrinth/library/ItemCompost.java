package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthSet;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemCompost {

	private final LabyrinthCollection<ItemMatcher> matchers = new LabyrinthSet<>();

	public ItemCompost registerMatcher(@NotNull ItemMatcher matcher) {
		matchers.add(matcher);
		return this;
	}

	public ItemCompost unregisterMatcher(@NotNull ItemMatcher matcher) {
		matchers.remove(matcher);
		return this;
	}

	public boolean has(@NotNull CompostElement synchronizer) {
		int amount = 0;
		if (synchronizer instanceof ItemSync) {
			for (int i = 0; i < synchronizer.getParent().getSize(); i++) {
				ItemStack slot = synchronizer.getParent().getItem(i);
				for (ItemMatcher matcher : matchers.stream().filter(m -> ((ItemSync<ItemMatcher>) synchronizer).getMatcher().isAssignableFrom(m.getClass())).collect(Collectors.toList())) {
					if (slot == null || !matcher.comparesTo(slot)) continue;
					amount += slot.getAmount();
				}
			}
		} else {
			for (int i = 0; i < synchronizer.getParent().getSize(); i++) {
				ItemStack slot = synchronizer.getParent().getItem(i);
				for (ItemMatcher matcher : matchers) {
					if (slot == null || !matcher.comparesTo(slot)) continue;
					amount += slot.getAmount();
				}
			}
		}
		return amount >= synchronizer.getAmount();
	}

	public boolean remove(CompostElement synchronizer) {
		int remainingAmount = synchronizer.getAmount();
		ItemStack[] inv = synchronizer.getParent().getContents();
		if (synchronizer instanceof ItemSync) {
			if (remainingAmount == -1) {
				for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
					if (inv[j] == null) continue;
					for (ItemMatcher matcher : matchers.stream().filter(m -> ((ItemSync<ItemMatcher>) synchronizer).getMatcher().isAssignableFrom(m.getClass())).collect(Collectors.toList())) {
						if (matcher.comparesTo(inv[j])) {
							inv[j].setAmount(0);
							break;
						}
					}
				}
				return true;
			} else {
				for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
					if (inv[j] == null) continue;
					for (ItemMatcher matcher : matchers.stream().filter(m -> ((ItemSync<ItemMatcher>) synchronizer).getMatcher().isAssignableFrom(m.getClass())).collect(Collectors.toList())) {
						if (matcher.comparesTo(inv[j])) {
							int newAmount = inv[j].getAmount() - remainingAmount;
							if (newAmount > 0) {
								inv[j].setAmount(newAmount);
								break;
							} else {
								synchronizer.getParent().clear(j);
								remainingAmount = -newAmount;
								if (remainingAmount == 0) break;
							}
							break;
						}
					}
				}
				return remainingAmount == 0;
			}
		} else {
			if (remainingAmount == -1) {
				for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
					if (inv[j] == null) continue;
					for (ItemMatcher matcher : matchers) {
						if (matcher.comparesTo(inv[j])) {
							inv[j].setAmount(0);
							break;
						}
					}
				}
				return true;
			} else {
				for (int j = 0; j < synchronizer.getParent().getSize(); j++) {
					if (inv[j] == null) continue;
					for (ItemMatcher matcher : matchers) {
						if (matcher.comparesTo(inv[j])) {
							int newAmount = inv[j].getAmount() - remainingAmount;
							if (newAmount > 0) {
								inv[j].setAmount(newAmount);
								break;
							} else {
								synchronizer.getParent().clear(j);
								remainingAmount = -newAmount;
								if (remainingAmount == 0) break;
							}
							break;
						}
					}
				}
				return remainingAmount == 0;
			}
		}
	}

	public boolean add(ItemStack item, Inventory inventory, Location drop) {
		boolean success = true;
		Map<Integer, ItemStack> map = inventory.addItem(item);
		if (!map.isEmpty()) {
			success = false;
			map.forEach((integer, itemStack) -> drop.getWorld().dropItem(drop, itemStack));
		}
		return success;
	}

	public boolean add(ItemStack item, Player player) {
		return add(item, player.getInventory(), player.getLocation());
	}

}
